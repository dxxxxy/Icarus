package studio.dreamys.icarus;

import studio.dreamys.icarus.config.ConfigStore;
import studio.dreamys.icarus.hud.HudContext;
import studio.dreamys.icarus.hud.HudElement;
import studio.dreamys.icarus.hud.Toasts;
import studio.dreamys.icarus.input.Keys;
import studio.dreamys.icarus.platform.Platform;
import studio.dreamys.icarus.render.DrawList;
import studio.dreamys.icarus.render.FrameRenderer;
import studio.dreamys.icarus.settings.KeybindMode;
import studio.dreamys.icarus.settings.Keybinds;
import studio.dreamys.icarus.settings.ModelBuilder;
import studio.dreamys.icarus.settings.Setting;
import studio.dreamys.icarus.settings.SettingListener;
import studio.dreamys.icarus.settings.SettingType;
import studio.dreamys.icarus.settings.SettingTypes;
import studio.dreamys.icarus.settings.SettingsModel;
import studio.dreamys.icarus.theme.Theme;
import studio.dreamys.icarus.theme.Themes;
import studio.dreamys.icarus.ui.ConfigPanel;
import studio.dreamys.icarus.ui.Menu;
import studio.dreamys.icarus.ui.WidgetFactory;
import studio.dreamys.icarus.ui.widget.Widget;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * One mod's Icarus: its settings, configs, keybinds, menu, theme, toasts and HUD. Create it with
 * {@link Icarus#create}. Several mods can each have their own instance in the same game.
 */
public final class IcarusMod implements SettingListener, Keybinds.Listener {
    private static final Logger LOG = Logger.getLogger("Icarus");

    private final String modid;
    private final IcarusOptions options;
    private final SettingsModel model;
    private final Keybinds keybinds;
    private final ConfigStore store;
    private final Map<SettingType<?>, WidgetFactory<?>> factories = WidgetFactory.defaults();
    private final Map<Setting<?>, List<Consumer<Object>>> callbacks = new IdentityHashMap<>();
    private final List<IntConsumer> keyListeners = new CopyOnWriteArrayList<>();
    private final List<HudElement> hud = new CopyOnWriteArrayList<>();
    private final Toasts toasts = new Toasts();
    /** Bound non-checkbox settings whose key is down, and those toggled on (see {@link #isKeybindActive(Setting)}). */
    private final java.util.Set<Setting<?>> held = java.util.Collections.newSetFromMap(new IdentityHashMap<>());
    private final java.util.Set<Setting<?>> toggled = java.util.Collections.newSetFromMap(new IdentityHashMap<>());

    private Theme theme;
    private ConfigPanel configPanel;
    private Menu menu;
    private int menuKey;

    private final FrameRenderer hudFrame = new FrameRenderer();
    private final HudContext hudContext = new HudContext();
    private final Runnable hudPaint = this::paintHud;
    private final FrameRenderer overlayFrame = new FrameRenderer();
    private final Runnable overlayPaint = () -> toasts.render(overlayFrame.getCanvas(), hudContext);
    private volatile boolean menuOpen;

    IcarusMod(String modid, Theme theme, IcarusOptions options, List<Class<?>> pageClasses, Path configDir, Path legacyDir) {
        this.modid = modid;
        this.options = options;
        this.menuKey = options.menuKey;
        this.model = new ModelBuilder(SettingTypes.builtIn()).build(pageClasses);
        this.keybinds = new Keybinds(model);
        this.store = new ConfigStore(configDir, legacyDir, model, keybinds, options.configUi);
        store.init();

        for (Setting<?> setting : model.getSettings()) setting.setListener(this);
        keybinds.setListener(this);

        Theme preferred = options.themeSelector ? Themes.byId(store.getPrefs().getTheme()) : null;
        this.theme = preferred != null ? preferred : theme;
        hud.add(toasts);
    }

    private Menu menu() {
        if (menu == null) {
            boolean recorderToggle = options.recorderToggle && Icarus.canHideFromRecorders();
            if (options.configUi || options.themeSelector || recorderToggle) {
                configPanel = new ConfigPanel(this, options.configUi, options.themeSelector, recorderToggle);
            }
            menu = new Menu(this, theme, configPanel);
        }
        return menu;
    }

    public String getModid() {
        return modid;
    }

    public SettingsModel getModel() {
        return model;
    }

    public Keybinds getKeybinds() {
        return keybinds;
    }

    public ConfigStore getConfigStore() {
        return store;
    }

    /** The menu window (created on first use). */
    public Menu getMenu() {
        return menu();
    }

    /* ---- theme and look ---- */

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
        if (menu != null) menu.setTheme(theme);
        store.setTheme(theme.getId());
    }

    /**
     * Whether this mod's drawing (its menu, toasts, watermark and every other {@link #addHud HUD element}) goes into
     * separate windows that recorders and screen sharing (OBS, Discord, Snipping Tool) can't see, laid over the game
     * so it looks the same to the player. Saved in {@code icarus.json}. Has no effect where
     * {@link Icarus#canHideFromRecorders()} is false.
     * <p>
     * Anything a mod draws through a {@link HudElement} follows this, so a mod can keep its own overlays (ESP boxes,
     * indicators, ...) off recordings by drawing them as HUD elements.
     */
    public boolean isHiddenFromRecorders() {
        return store.getPrefs().isHideFromRecorders();
    }

    public void setHiddenFromRecorders(boolean hidden) {
        store.setHideFromRecorders(hidden);
        if (!hidden) Icarus.getRecorderOverlay().hide();
    }

    /** Accent colour (ARGB): the one from the options, or the theme's. */
    public int getAccent() {
        return options.accent != null ? options.accent : theme.getDefaultAccent();
    }

    /** Changes the accent colour (ARGB) at runtime, e.g. from a "Menu color" setting. */
    public void setAccent(int argb) {
        options.accent = argb;
    }

    /** Changes the menu scale at runtime (1 = the theme's natural size), e.g. from a "DPI scale" setting. */
    public void setMenuScale(double scale) {
        if (!(scale > 0)) throw new IllegalArgumentException("menu scale must be positive");
        options.menuScale = scale;
    }

    /** Design units to framebuffer pixels: 1 at Minecraft GUI scale 2, times the menu scale option. */
    public double renderScale(double guiScale) {
        return guiScale / 2 * options.menuScale;
    }

    /** Footer text of the menu window (empty by default). */
    public void setFooter(String left, String center, String right) {
        menu().setFooter(left, center, right);
    }

    /** Window title, for themes that show one (the mod id in capitals by default). */
    public void setTitle(String title) {
        menu().setTitle(title);
    }

    public int getMenuKey() {
        return menuKey;
    }

    public void setMenuKey(int key) {
        menuKey = key;
    }

    /* ---- callbacks and widgets ---- */

    /** Runs {@code callback} with the new value whenever the setting changes (by the user, a config load or a reset). */
    @SuppressWarnings("unchecked")
    public <T> void onChange(Setting<T> setting, Consumer<T> callback) {
        callbacks.computeIfAbsent(setting, s -> new ArrayList<>()).add((Consumer<Object>) callback);
    }

    /** {@link #onChange(Setting, Consumer)} for the setting declared as {@code field} in the {@code @IGroup} class {@code group}. */
    @SuppressWarnings("unchecked")
    public <T> void onChange(Class<?> group, String field, Consumer<T> callback) {
        Setting<?> setting = model.find(group, field);
        if (setting == null) throw new IllegalArgumentException("No setting " + group.getName() + "." + field);
        onChange((Setting<T>) setting, callback);
    }

    /** Uses {@code factory} to create the widgets of one setting type (e.g. a custom checkbox). */
    public <T> void replaceWidget(SettingType<T> type, WidgetFactory<T> factory) {
        factories.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public <T> Widget createWidget(Menu menu, Setting<T> setting) {
        WidgetFactory<T> factory = (WidgetFactory<T>) factories.get(setting.getType());
        if (factory == null) {
            LOG.warning("[Icarus] No widget for setting type " + setting.getType() + " (" + setting + ")");
            return null;
        }
        return factory.create(menu, setting);
    }

    @Override
    public void changed(Setting<?> setting, boolean byUser) {
        if (byUser && setting.isPersistent()) store.markDirty();
        List<Consumer<Object>> list = callbacks.get(setting);
        if (list == null) return;
        Object value = setting.get();
        for (int i = 0; i < list.size(); i++) {
            try {
                list.get(i).accept(value);
            } catch (RuntimeException e) {
                LOG.log(Level.WARNING, "[Icarus] Callback for " + setting + " failed", e);
            }
        }
    }

    @Override
    public void keybindChanged(Setting<?> setting, boolean byUser) {
        if (byUser) {
            store.markDirty();
            applyRestingState(setting);
        }
    }

    /* ---- HUD, keys, ticks ---- */

    /** Adds an element to this mod's HUD. */
    public void addHud(HudElement element) {
        hud.add(element);
    }

    public void removeHud(HudElement element) {
        hud.remove(element);
    }

    public Toasts toasts() {
        return toasts;
    }

    /** Listens for key presses in game while no screen is open ({@link studio.dreamys.icarus.input.Keys} codes). */
    public void addKeyListener(IntConsumer listener) {
        keyListeners.add(listener);
    }

    /** Opens the menu. */
    public void openMenu() {
        Icarus.getPlatform().openScreen(menu());
    }

    void onKeyPressed(int key) {
        if (key == menuKey) {
            openMenu();
            return;
        }
        for (Map.Entry<Setting<?>, Integer> binding : keybinds.asMap().entrySet()) {
            if (binding.getValue() != key) continue;
            Setting<?> setting = binding.getKey();
            if (setting.getType() == SettingTypes.BUTTON) {
                Runnable action = (Runnable) setting.get();
                if (action != null) action.run();
            } else if (setting.getType() != SettingTypes.CHECKBOX) {
                held.add(setting);
                if (keybinds.getMode(setting) == KeybindMode.TOGGLE && !toggled.remove(setting)) toggled.add(setting);
            } else {
                @SuppressWarnings("unchecked") Setting<Boolean> toggle = (Setting<Boolean>) setting;
                switch (keybinds.getMode(setting)) {
                    case TOGGLE:
                        toggle.set(!Boolean.TRUE.equals(toggle.get()));
                        break;
                    case ON_HOTKEY:
                        toggle.set(true);
                        break;
                    case OFF_HOTKEY:
                        toggle.set(false);
                        break;
                    default:
                        break;
                }
            }
        }
        for (IntConsumer listener : keyListeners) listener.accept(key);
    }

    void onKeyReleased(int key) {
        for (Map.Entry<Setting<?>, Integer> binding : keybinds.asMap().entrySet()) {
            if (binding.getValue() != key) continue;
            held.remove(binding.getKey());
            if (binding.getKey().getType() != SettingTypes.CHECKBOX) continue;
            @SuppressWarnings("unchecked") Setting<Boolean> toggle = (Setting<Boolean>) binding.getKey();
            KeybindMode mode = keybinds.getMode(toggle);
            if (mode == KeybindMode.ON_HOTKEY) toggle.set(false);
            else if (mode == KeybindMode.OFF_HOTKEY) toggle.set(true);
        }
    }

    /**
     * Whether a bound setting is active by its keybind mode: always on, held (on hotkey), not held (off hotkey) or
     * toggled by presses. For checkboxes that's simply their value; for other rows (e.g. an "Activation type" label)
     * the mod asks here. A setting without a key counts as active.
     */
    public boolean isKeybindActive(Setting<?> setting) {
        if (setting.getType() == SettingTypes.CHECKBOX) return Boolean.TRUE.equals(setting.get());
        if (keybinds.get(setting) == Keys.KEY_NONE) return true;
        switch (keybinds.getMode(setting)) {
            case ALWAYS_ON:
                return true;
            case ON_HOTKEY:
                return held.contains(setting);
            case OFF_HOTKEY:
                return !held.contains(setting);
            default:
                return toggled.contains(setting);
        }
    }

    /** {@link #isKeybindActive(Setting)} for the field {@code name} in a page or group class. */
    public boolean isKeybindActive(Class<?> owner, String name) {
        Setting<?> setting = model.find(owner, name);
        if (setting == null) throw new IllegalArgumentException("No setting " + name + " in " + owner.getName());
        return isKeybindActive(setting);
    }

    /** Puts a bound checkbox in the resting state of its mode (after the user picked a mode). */
    private void applyRestingState(Setting<?> setting) {
        if (setting.getType() != SettingTypes.CHECKBOX) return;
        @SuppressWarnings("unchecked") Setting<Boolean> toggle = (Setting<Boolean>) setting;
        KeybindMode mode = keybinds.getMode(setting);
        if (mode == KeybindMode.ALWAYS_ON || mode == KeybindMode.OFF_HOTKEY) toggle.set(true);
        else if (mode == KeybindMode.ON_HOTKEY) toggle.set(false);
    }

    void tick() {
        for (HudElement element : hud) element.tick();
        store.tick(menu != null && menu.isInteracting());
    }

    void renderHud(float partialTick) {
        if (hud.isEmpty()) return;
        Platform platform = Icarus.getPlatform();
        double scale = renderScale(platform.getGuiScale());
        int fbWidth = platform.getFramebufferWidth(), fbHeight = platform.getFramebufferHeight();
        hudContext.set(theme, getAccent(), fbWidth / scale, fbHeight / scale, partialTick);
        if (isHiddenFromRecorders() && Icarus.canHideFromRecorders()) {
            Icarus.getRecorderOverlay().submit(hudFrame.paint(platform.getRenderer(), Icarus.getAtlas(), scale, fbWidth, fbHeight, hudPaint));
        } else {
            hudFrame.render(platform.getRenderer(), Icarus.getAtlas(), scale, fbWidth, fbHeight, hudPaint);
        }
    }

    private void paintHud() {
        for (HudElement element : hud) {
            //while the menu is open its own frame draws the toasts on top of it
            if (element == toasts && menuOpen) continue;
            element.render(hudFrame.getCanvas(), hudContext);
        }
    }

    /** Draws the toasts over the open menu (called by the menu after it paints). */
    public void renderOverMenu(float partialTick) {
        DrawList list = paintOverMenu(partialTick);
        if (list != null && !list.isEmpty()) Icarus.getPlatform().getRenderer().render(list);
    }

    /** Paints the toasts over the open menu without drawing them; null when there are none. */
    public DrawList paintOverMenu(float partialTick) {
        if (!toasts.hasActive()) return null;
        Platform platform = Icarus.getPlatform();
        double scale = renderScale(platform.getGuiScale());
        int fbWidth = platform.getFramebufferWidth(), fbHeight = platform.getFramebufferHeight();
        hudContext.set(theme, getAccent(), fbWidth / scale, fbHeight / scale, partialTick);
        return overlayFrame.paint(platform.getRenderer(), Icarus.getAtlas(), scale, fbWidth, fbHeight, overlayPaint);
    }

    /** Whether this mod's menu is on screen (set by the menu). */
    public void setMenuOpen(boolean open) {
        menuOpen = open;
    }

    public boolean isMenuOpen() {
        return menuOpen;
    }

    /* ---- called by the menu ---- */

    public void interactionEnded() {
        store.interactionEnded();
    }

    public void menuClosed() {
        store.closed();
    }
}
