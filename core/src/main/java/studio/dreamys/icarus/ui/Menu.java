package studio.dreamys.icarus.ui;

import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.IcarusMod;
import studio.dreamys.icarus.config.UiPrefs;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.platform.Platform;
import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.render.FrameRenderer;
import studio.dreamys.icarus.settings.GroupNode;
import studio.dreamys.icarus.settings.PageNode;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.settings.SubPageNode;
import studio.dreamys.icarus.theme.ConfigUiStyle;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.ui.popup.ContextPopup;
import studio.dreamys.icarus.ui.popup.Popup;
import studio.dreamys.icarus.ui.widget.ButtonWidget;
import studio.dreamys.icarus.ui.widget.ColorWidget;
import studio.dreamys.icarus.ui.widget.FieldWidget;
import studio.dreamys.icarus.ui.widget.KeybindWidget;
import studio.dreamys.icarus.ui.widget.ListWidget;
import studio.dreamys.icarus.ui.widget.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * The menu window of one mod. Each frame runs in a fixed order so nothing ever lags or overlaps wrongly:
 * <ol>
 *     <li>input: window drag and captured widgets (e.g. a slider) update from the mouse</li>
 *     <li>layout: the theme positions the window, tabs, groups and every widget</li>
 *     <li>hover is resolved against that layout</li>
 *     <li>paint: chrome, then each group (frame, clipped widgets, foreground), then the one open popup last</li>
 * </ol>
 * Clicks go to the open popup first, then the widget under the cursor, then tabs, then the drag area.
 * Keyboard input goes only to the focused widget.
 */
public final class Menu implements MenuController {
    /** Design units of the window that must stay on screen when dragging. */
    private static final double KEEP_VISIBLE = 24;
    private static final double SCROLL_STEP = 24;

    private final IcarusMod mod;
    private final ConfigPanel configPanel;
    private Theme theme;

    private final List<TabView> tabs = new ArrayList<>();
    private final List<TabView> tabsView = Collections.unmodifiableList(tabs);
    private final Map<GroupNode, GroupView> groupViews = new IdentityHashMap<>();
    private int tab;
    private int subTab;

    /** Window frame; the theme sets its size, the menu its position. */
    public final Rect window = new Rect();
    /** Area that starts a window drag, set by the theme. */
    public final Rect dragArea = new Rect();
    /** Area the theme gives to groups (for reference by painters). */
    public final Rect content = new Rect();

    private Widget hovered, captured, focused;
    private Popup popup;
    private boolean popupCaptured;
    private boolean dragging;
    private double grabX, grabY;
    private boolean positioned;

    private double mouseX, mouseY;
    private double viewWidth, viewHeight;
    private long lastFrame;
    private float dt;

    private String footerLeft = "", footerCenter = "", footerRight = "";
    private String title;

    private final FrameRenderer frame = new FrameRenderer();
    private final Canvas layoutCanvas = new Canvas();
    private final Runnable paintPass = this::paint;

    /** Open/close fade: ease-out over {@link #FADE_NANOS}; reversed when closing. */
    private static final long FADE_NANOS = 150_000_000L;
    private long fadeStart;
    private boolean closing;

    public Menu(IcarusMod mod, Theme theme, ConfigPanel configPanel) {
        this.mod = mod;
        this.theme = theme;
        this.configPanel = configPanel;
        this.title = mod.getModid().toUpperCase(java.util.Locale.ROOT);
        rebuild();
        restorePrefs(mod.getConfigStore().getPrefs());
    }

    /* ---- structure ---- */

    private void rebuild() {
        tabs.clear();
        GroupView extra = null;
        boolean pageStyle = theme.getConfigUiStyle() == ConfigUiStyle.PAGE;
        if (configPanel != null) extra = groupView(configPanel.group(pageStyle));

        boolean merged = false;
        for (PageNode page : mod.getModel().getPages()) {
            TabView tabView = new TabView(page, page.getLabel(), page.getIcon());
            //a page with the config icon hosts the built-in config UI (first, in the first column) instead of a tab of its own
            boolean host = extra != null && pageStyle && !merged && "config".equals(page.getIconName());
            for (SubPageNode sub : page.getSubPages()) {
                SubTabView subView = new SubTabView(sub, sub.getLabel());
                if (host) {
                    extra.getGroup().column(0);
                    subView.groups.add(extra);
                    host = false;
                    merged = true;
                }
                for (GroupNode group : sub.getGroups()) subView.groups.add(groupView(group));
                if (extra != null && !pageStyle) subView.groups.add(extra);
                tabView.subTabs.add(subView);
            }
            tabs.add(tabView);
        }
        if (extra != null && pageStyle && !merged) {
            TabView configTab = new TabView(null, extra.getLabel(), theme.getConfigIcon());
            SubTabView sub = new SubTabView(null, extra.getLabel());
            sub.groups.add(extra);
            configTab.subTabs.add(sub);
            tabs.add(configTab);
        }
        if (tabs.isEmpty()) {
            TabView empty = new TabView(null, "", ' ');
            empty.subTabs.add(new SubTabView(null, ""));
            tabs.add(empty);
        }
        tab = Math.min(tab, tabs.size() - 1);
        subTab = Math.min(subTab, tabs.get(tab).subTabs.size() - 1);
    }

    private GroupView groupView(GroupNode group) {
        GroupView view = groupViews.get(group);
        if (view != null) return view;
        view = new GroupView(group);
        for (Setting<?> setting : group.getSettings()) {
            Widget widget = mod.createWidget(this, setting);
            if (widget == null) continue;
            widget.setGroup(view);
            if (setting.isBindable()) {
                KeybindWidget keybind = new KeybindWidget(this, setting, mod.getKeybinds(), widget);
                keybind.setGroup(view);
                widget.setKeybind(keybind);
            }
            if (configPanel != null && configPanel.owns(group) && widget instanceof ButtonWidget && setting.getName().startsWith("Save")) {
                String base = setting.getLabel();
                ((ButtonWidget) widget).setLabelSource(() -> configPanel.saveLabel(base));
            }
            view.add(widget);
        }
        groupViews.put(group, view);
        return view;
    }

    private void restorePrefs(UiPrefs prefs) {
        if (prefs.hasPosition()) {
            window.x = prefs.getX();
            window.y = prefs.getY();
            positioned = true;
        }
        if (prefs.getPage() != null) {
            for (int i = 0; i < tabs.size(); i++) {
                TabView t = tabs.get(i);
                String name = t.getPage() == null ? "" : t.getPage().getName();
                if (!name.equals(prefs.getPage())) continue;
                tab = i;
                for (int j = 0; j < t.subTabs.size(); j++) {
                    SubPageNode sub = t.subTabs.get(j).getSubPage();
                    if (sub != null && sub.getName() != null && sub.getName().equals(prefs.getSubPage())) subTab = j;
                }
            }
        }
    }

    public IcarusMod getMod() {
        return mod;
    }

    public Theme getTheme() {
        return theme;
    }

    /** Switches theme (rebuilds tabs, since themes place the config UI differently). */
    public void setTheme(Theme theme) {
        if (theme == this.theme) return;
        closePopup();
        focus(null);
        this.theme = theme;
        rebuild();
    }

    public List<TabView> getTabs() {
        return tabsView;
    }

    public int getTabIndex() {
        return tab;
    }

    public TabView getCurrentTab() {
        return tabs.get(tab);
    }

    public int getSubTabIndex() {
        return subTab;
    }

    public SubTabView getCurrentSubTab() {
        return getCurrentTab().subTabs.get(subTab);
    }

    public void selectTab(int index) {
        if (index < 0 || index >= tabs.size() || index == tab) return;
        closePopup();
        focus(null);
        tab = index;
        subTab = 0;
        rememberPage();
    }

    public void selectSubTab(int index) {
        if (index < 0 || index >= getCurrentTab().subTabs.size() || index == subTab) return;
        closePopup();
        focus(null);
        subTab = index;
        rememberPage();
    }

    private void rememberPage() {
        TabView t = getCurrentTab();
        SubPageNode sub = getCurrentSubTab().getSubPage();
        mod.getConfigStore().setPage(t.getPage() == null ? "" : t.getPage().getName(), sub == null ? null : sub.getName());
    }

    /* ---- title and footer ---- */

    /** Window title for themes that show one (the mod id in capitals by default). */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title;
    }

    public void setFooter(String left, String center, String right) {
        footerLeft = left == null ? "" : left;
        footerCenter = center == null ? "" : center;
        footerRight = right == null ? "" : right;
    }

    public String getFooterLeft() {
        return footerLeft;
    }

    public String getFooterCenter() {
        return footerCenter;
    }

    public String getFooterRight() {
        return footerRight;
    }

    /* ---- state for themes ---- */

    /** Accent colour (ARGB). */
    public int getAccent() {
        return mod.getAccent();
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    /** Seconds since the last frame (capped), for animations. */
    public float getDeltaTime() {
        return dt;
    }

    public Popup getPopup() {
        return popup;
    }

    public Widget getFocused() {
        return focused;
    }

    @Override
    public boolean isTyping() {
        return !closing && (focused instanceof FieldWidget || focused instanceof ListWidget);
    }

    public Widget getHovered() {
        return hovered;
    }

    public boolean isDragging() {
        return dragging;
    }

    /** Whether the user is mid-interaction (mouse held on something, or typing), so auto-save waits. */
    public boolean isInteracting() {
        return captured != null || popupCaptured || dragging || focused != null;
    }

    /** Whether there are unsaved config changes (profile mode). */
    public boolean hasUnsavedChanges() {
        return mod.getConfigStore().isProfileMode() && mod.getConfigStore().hasUnsavedChanges();
    }

    /* ---- popups and focus ---- */

    public void togglePopup(Popup p) {
        if (popup == p) {
            closePopup();
            return;
        }
        closePopup();
        focus(null);
        popup = p;
        p.resetAnimation();
    }

    /** Opens a right-click menu at the given point (design units), replacing any open popup. */
    public void openContext(ContextPopup context, double x, double y) {
        closePopup();
        focus(null);
        context.anchorX = x;
        context.anchorY = y;
        popup = context;
        context.resetAnimation();
    }

    public void closePopup() {
        if (popup == null) return;
        if (popupCaptured) popup.mouseReleased(mouseX, mouseY, 0);
        popupCaptured = false;
        popup = null;
    }

    /** Gives keyboard focus to a widget (null clears it). The previous one is told it lost focus. */
    public void focus(Widget widget) {
        if (focused == widget) return;
        Widget old = focused;
        focused = widget;
        if (old != null) old.focusLost();
    }

    /** An interaction finished (release, unfocus, pick): lets auto-save write. */
    public void interactionEnded() {
        mod.interactionEnded();
    }

    /* ---- layout helpers for themes ---- */

    /**
     * Lays out the current sub-tab's groups in {@code columns} columns inside {@code area}. Groups go to their
     * {@code column} hint (the last column for the config panel) or the shortest column; columns are filled
     * top-down, pinned groups stack at the bottom, and spare height is shared out. Groups that don't fit scroll.
     */
    public void layoutColumns(Canvas canvas, Rect area, int columns, double gap, GroupMetrics metrics) {
        layoutColumns(canvas, area, columns, gap, metrics, null);
    }

    /** Like {@link #layoutColumns(Canvas, Rect, int, double, GroupMetrics)}, with column {@code i} starting {@code topOffsets[i]} lower. */
    public void layoutColumns(Canvas canvas, Rect area, int columns, double gap, GroupMetrics metrics, double[] topOffsets) {
        content.set(area);
        List<GroupView> groups = getCurrentSubTab().groups;
        columns = Math.max(1, columns);
        double columnWidth = (area.w - gap * (columns - 1)) / columns;

        @SuppressWarnings("unchecked")
        List<GroupView>[] perColumn = new List[columns];
        double[] used = new double[columns];
        for (int c = 0; c < columns; c++) perColumn[c] = new ArrayList<>();

        double innerWidth = columnWidth - metrics.padLeft - metrics.padRight;
        for (GroupView g : groups) {
            g.contentHeight = metrics.rowOffset + measureRows(canvas, g, innerWidth, metrics.rowGap);
            g.slack = metrics.rowOffset; //rows start that far down but also end that far above their height
            g.clipBelow = metrics.clipBelow;
            int hint = g.getGroup().getColumn();
            int column;
            if (ConfigPanel.isLastColumn(hint)) column = columns - 1;
            else if (hint >= 0) column = Math.min(hint, columns - 1);
            else {
                column = 0;
                for (int c = 1; c < columns; c++) if (used[c] < used[column]) column = c;
            }
            perColumn[column].add(g);
            used[column] += natural(g, metrics) + gap;
        }

        for (int c = 0; c < columns; c++) {
            double x = area.x + c * (columnWidth + gap);
            double offset = topOffsets != null && c < topOffsets.length ? topOffsets[c] : 0;
            placeColumn(canvas, perColumn[c], x, area.y + offset, columnWidth, area.h - offset, metrics.stackGap >= 0 ? metrics.stackGap : gap, metrics);
        }
    }

    private double natural(GroupView g, GroupMetrics m) {
        double fixed = g.getGroup().getHeight();
        if (fixed > 0) return fixed;
        return Math.max(m.minHeight, m.padTop + g.contentHeight + m.padBottom);
    }

    private static boolean grows(GroupView g) {
        return !g.getGroup().isPinBottom() && g.getGroup().getHeight() <= 0;
    }

    private void placeColumn(Canvas canvas, List<GroupView> column, double x, double y, double width, double height, double gap, GroupMetrics m) {
        if (column.isEmpty()) return;
        List<GroupView> top = new ArrayList<>(), bottom = new ArrayList<>();
        for (GroupView g : column) (g.getGroup().isPinBottom() ? bottom : top).add(g);

        double gaps = gap * (column.size() - 1);
        double total = 0;
        for (GroupView g : column) total += natural(g, m);
        double available = height - gaps;

        double[] heights = new double[column.size()];
        if (total <= available) {
            double extra = available - total;
            int growers = 0;
            for (GroupView g : column) if (grows(g)) growers++;
            for (int i = 0; i < column.size(); i++) {
                GroupView g = column.get(i);
                double h = natural(g, m);
                if (grows(g) && growers > 0) h += extra / growers;
                heights[i] = h;
            }
        } else {
            //fixed-height groups keep their size; the others share what's left in proportion and scroll
            double fixedTotal = 0, flexTotal = 0;
            for (GroupView g : column) {
                if (g.getGroup().getHeight() > 0) fixedTotal += g.getGroup().getHeight();
                else flexTotal += natural(g, m);
            }
            double flexSpace = Math.max(0, available - fixedTotal);
            for (int i = 0; i < column.size(); i++) {
                GroupView g = column.get(i);
                double fixed = g.getGroup().getHeight();
                heights[i] = fixed > 0 ? fixed : Math.max(m.minHeight, flexTotal > 0 ? natural(g, m) * flexSpace / flexTotal : 0);
            }
        }

        double cursor = y;
        for (int i = 0; i < column.size(); i++) {
            GroupView g = column.get(i);
            if (g.getGroup().isPinBottom()) continue;
            g.frame.set(x, cursor, width, heights[i]);
            cursor += heights[i] + gap;
        }
        double bottomCursor = y + height;
        for (int i = column.size() - 1; i >= 0; i--) {
            GroupView g = column.get(i);
            if (!g.getGroup().isPinBottom()) continue;
            bottomCursor -= heights[i];
            g.frame.set(x, bottomCursor, width, heights[i]);
            bottomCursor -= gap;
        }

        for (GroupView g : column) {
            g.content.set(g.frame.x + m.padLeft, g.frame.y + m.padTop, g.frame.w - m.padLeft - m.padRight, g.frame.h - m.padTop - m.padBottom);
            g.scrollBy(0); //clamp to the new size
            positionRows(canvas, g, m);
        }
    }

    private double measureRows(Canvas canvas, GroupView g, double width, double rowGap) {
        return stackRows(canvas, g, 0, 0, width, rowGap);
    }

    private void positionRows(Canvas canvas, GroupView g, GroupMetrics m) {
        stackRows(canvas, g, g.content.x, g.content.y + m.rowOffset - g.scroll, g.content.w, m.rowGap);
    }

    /** Lays rows out top-down from {@code y}; returns the total height. */
    private double stackRows(Canvas canvas, GroupView g, double x, double y, double width, double rowGap) {
        double cursor = y;
        boolean first = true;
        Widget row = null;
        int attached = 0;
        boolean rowHidden = false;
        for (Widget w : g.getWidgets()) {
            if (row != null && w instanceof ColorWidget && ((ColorWidget) w).isAttached()) {
                //an attached swatch follows its row, also when the row is hidden
                w.hidden = rowHidden || !w.getSetting().isVisible();
                if (!w.hidden) theme.layoutAttachedColor(canvas, (ColorWidget) w, row, attached++);
                continue;
            }
            attached = 0;
            rowHidden = w.hidden = !w.getSetting().isVisible();
            if (w.hidden) {
                row = w; //its attached swatches hide with it
                continue;
            }
            double rowY = first ? cursor : cursor + rowGap;
            double h = w.layout(theme, canvas, x, rowY, width);
            if (h <= 0) continue;
            cursor = rowY + h;
            first = false;
            row = w;
        }
        return cursor - y;
    }

    /* ---- MenuController ---- */

    @Override
    public void opened() {
        mod.setMenuOpen(true);
        if (closing || fadeStart == 0) fadeStart = System.nanoTime(); //a resize re-opens without fading again
        closing = false;
        if (configPanel != null) configPanel.refresh();
        lastFrame = 0;
    }

    @Override
    public void render(double guiX, double guiY, float partialTick) {
        Platform platform = Icarus.getPlatform();
        double guiScale = platform.getGuiScale();
        double scale = mod.renderScale(guiScale);
        int fbWidth = platform.getFramebufferWidth(), fbHeight = platform.getFramebufferHeight();
        viewWidth = fbWidth / scale;
        viewHeight = fbHeight / scale;
        mouseX = guiX * guiScale / scale;
        mouseY = guiY * guiScale / scale;

        long now = System.nanoTime();
        dt = lastFrame == 0 ? 0 : Math.min(0.1f, (now - lastFrame) / 1e9f);
        lastFrame = now;

        //1. input that moves things
        if (dragging) {
            window.x = mouseX - grabX;
            window.y = mouseY - grabY;
            clampWindow();
        }
        if (captured != null) captured.mouseDragged(mouseX, mouseY);
        if (popup != null && popupCaptured) popup.mouseDragged(mouseX, mouseY);

        //2. layout
        layoutCanvas.setup(Icarus.getAtlas(), scale);
        theme.layout(this, layoutCanvas);
        if (!positioned) {
            window.x = Math.round((viewWidth - window.w) / 2);
            window.y = Math.round((viewHeight - window.h) / 2);
            positioned = true;
            theme.layout(this, layoutCanvas);
        } else if (clampWindow()) {
            theme.layout(this, layoutCanvas);
        }
        if (popup != null) popup.layout(theme, layoutCanvas);

        //3. hover
        Widget hit = popup != null && popup.contains(mouseX, mouseY) ? null : hitWidget(mouseX, mouseY);
        if (hovered != hit) {
            if (hovered != null) hovered.setHovered(false);
            hovered = hit;
            if (hovered != null) hovered.setHovered(true);
        }
        for (GroupView g : getCurrentSubTab().groups) {
            for (Widget w : g.getWidgets()) {
                w.animate(dt);
                if (w.getKeybind() != null) w.getKeybind().animate(dt);
                if (w instanceof ListWidget) ((ListWidget) w).hover(mouseX, mouseY);
            }
        }
        if (popup != null) {
            popup.hover(mouseX, mouseY);
            popup.animate(dt);
        }

        //4. paint, then the toasts on top
        frame.render(platform.getRenderer(), Icarus.getAtlas(), scale, fbWidth, fbHeight, paintPass);
        mod.renderOverMenu(partialTick);
    }

    /** Keeps part of the window on screen; returns whether it moved. */
    private boolean clampWindow() {
        if (window.w <= 0 || viewWidth <= 0) return false;
        double x = Math.max(KEEP_VISIBLE - window.w, Math.min(viewWidth - KEEP_VISIBLE, window.x));
        double y = Math.max(0, Math.min(viewHeight - KEEP_VISIBLE, window.y));
        boolean moved = x != window.x || y != window.y;
        window.x = x;
        window.y = y;
        return moved;
    }

    private void paint() {
        Canvas canvas = frame.getCanvas();
        canvas.setAlpha(fadeAlpha());
        theme.paintWindow(canvas, this);
        for (GroupView g : getCurrentSubTab().groups) {
            if (g.frame.isEmpty()) continue;
            theme.paintGroup(canvas, this, g);
            canvas.pushClip(g.content.x, g.content.y, g.content.w, g.content.h + g.clipBelow);
            for (Widget w : g.getWidgets()) {
                if (w.hidden || w.bounds.bottom() < g.content.y || w.bounds.y > g.content.bottom() + g.clipBelow) continue;
                w.paint(theme, canvas);
                if (w.getKeybind() != null) w.getKeybind().paint(theme, canvas);
            }
            canvas.popClip();
            theme.paintGroupForeground(canvas, this, g);
        }
        if (popup != null) {
            canvas.pushClip(0, 0, viewWidth, viewHeight);
            popup.paint(theme, canvas);
            canvas.popClip();
        }
        canvas.setAlpha(1);
    }

    private double fadeProgress() {
        if (fadeStart == 0) return 1;
        return Math.max(0, Math.min(1, (System.nanoTime() - fadeStart) / (double) FADE_NANOS));
    }

    /** The whole menu's opacity: eased in after opening, out after {@link #requestClose()}. */
    public double fadeAlpha() {
        double t = fadeProgress();
        double eased = 1 - (1 - t) * (1 - t) * (1 - t);
        return closing ? 1 - eased : eased;
    }

    @Override
    public void requestClose() {
        if (closing) return;
        //reverse from wherever the fade-in got to
        double shown = fadeAlpha();
        closing = true;
        double t = 1 - Math.cbrt(shown); //the closing progress whose eased value leaves "shown" visible
        fadeStart = System.nanoTime() - (long) (t * FADE_NANOS);
        closePopup();
        focus(null);
    }

    @Override
    public boolean isFadedOut() {
        return closing && fadeProgress() >= 1;
    }

    /** Whether the menu is fading out (input is ignored). */
    public boolean isClosing() {
        return closing;
    }

    /** Shows the menu fully at once (no fade), e.g. for snapshots. */
    public void skipFade() {
        fadeStart = 0;
        closing = false;
    }

    private Widget hitWidget(double x, double y) {
        for (GroupView g : getCurrentSubTab().groups) {
            if (!g.content.contains(x, y)) continue;
            //last first: a swatch attached to a row sits on top of that row's widget
            List<Widget> widgets = g.getWidgets();
            for (int i = widgets.size() - 1; i >= 0; i--) {
                Widget w = widgets.get(i);
                if (w.hidden || w.getSetting().isDisabled()) continue;
                KeybindWidget keybind = w.getKeybind();
                if (keybind != null && keybind.contains(x, y)) return keybind;
                if (w.contains(x, y)) return w;
            }
        }
        return null;
    }

    private GroupView groupAt(double x, double y) {
        for (GroupView g : getCurrentSubTab().groups) if (g.frame.contains(x, y)) return g;
        return null;
    }

    @Override
    public void mousePressed(double guiX, double guiY, int button) {
        updateMouse(guiX, guiY);
        if (closing) return;
        double x = mouseX, y = mouseY;

        if (focused instanceof KeybindWidget && ((KeybindWidget) focused).bindMouse(button)) return;

        if (popup != null) {
            if (popup.contains(x, y)) {
                popupCaptured = popup.mousePressed(x, y, button);
            } else {
                closePopup();
            }
            return;
        }

        Widget target = hitWidget(x, y);
        if (focused != null && focused != target) focus(null);

        if (target != null) {
            if (target.mousePressed(x, y, button)) captured = target;
            return;
        }

        if (button == 0) {
            List<TabView> tabList = tabs;
            for (int i = 0; i < tabList.size(); i++) {
                if (tabList.get(i).bounds.contains(x, y)) {
                    selectTab(i);
                    return;
                }
            }
            List<SubTabView> subs = getCurrentTab().subTabs;
            for (int i = 0; i < subs.size(); i++) {
                if (subs.get(i).bounds.contains(x, y)) {
                    selectSubTab(i);
                    return;
                }
            }
            if (dragArea.contains(x, y)) {
                dragging = true;
                grabX = x - window.x;
                grabY = y - window.y;
            }
        }
    }

    @Override
    public void mouseReleased(double guiX, double guiY, int button) {
        updateMouse(guiX, guiY);
        if (dragging) {
            dragging = false;
            mod.getConfigStore().setPosition(window.x, window.y);
        }
        if (captured != null) {
            captured.mouseReleased(mouseX, mouseY, button);
            captured = null;
            interactionEnded();
        }
        if (popup != null && popupCaptured) {
            popup.mouseReleased(mouseX, mouseY, button);
            popupCaptured = false;
        }
    }

    @Override
    public void mouseScrolled(double guiX, double guiY, double amount) {
        updateMouse(guiX, guiY);
        if (popup != null) {
            if (popup.contains(mouseX, mouseY)) popup.scroll(amount);
            return;
        }
        Widget target = hitWidget(mouseX, mouseY);
        if (target != null && target.scroll(amount)) return;
        GroupView g = groupAt(mouseX, mouseY);
        if (g != null) g.scrollBy(-amount * SCROLL_STEP);
    }

    @Override
    public boolean keyPressed(int key) {
        if (closing) return true;
        if (key == Keys.KEY_ESCAPE && popup != null) {
            closePopup();
            return true;
        }
        if (focused != null) return focused.keyPressed(key);
        return false;
    }

    @Override
    public void charTyped(int codePoint) {
        if (focused != null) focused.charTyped(codePoint);
    }

    @Override
    public void closed() {
        mod.setMenuOpen(false);
        fadeStart = 0; //the next open fades in again
        closing = false;
        closePopup();
        focus(null);
        if (captured != null) {
            captured.mouseReleased(mouseX, mouseY, 0);
            captured = null;
        }
        if (dragging) {
            dragging = false;
            mod.getConfigStore().setPosition(window.x, window.y);
        }
        mod.menuClosed();
    }

    private void updateMouse(double guiX, double guiY) {
        Platform platform = Icarus.getPlatform();
        double guiScale = platform.getGuiScale();
        double scale = mod.renderScale(guiScale);
        mouseX = guiX * guiScale / scale;
        mouseY = guiY * guiScale / scale;
    }

    /** Visible area in design units (valid after the first frame). */
    public double getViewWidth() {
        return viewWidth;
    }

    public double getViewHeight() {
        return viewHeight;
    }
}
