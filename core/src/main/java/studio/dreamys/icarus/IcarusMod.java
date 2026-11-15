package studio.dreamys.icarus;

import studio.dreamys.icarus.config.ConfigStore;
import studio.dreamys.icarus.hud.HudContext;
import studio.dreamys.icarus.hud.HudElement;
import studio.dreamys.icarus.hud.Notifications;
import studio.dreamys.icarus.platform.Platform;
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
 * One mod's Icarus: its settings, configs, keybinds, menu, theme, notifications and HUD. Create it with
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
    private final Notifications notifications = new Notifications();

    private Theme theme;
    private ConfigPanel configPanel;
    private Menu menu;
    private int menuKey;

    private final FrameRenderer hudFrame = new FrameRenderer();
    private final HudContext hudContext = new HudContext();
    private final Runnable hudPaint = this::paintHud;

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
        hud.add(notifications);
    }

    private Menu menu() {
        if (menu == null) {
            if (options.configUi || options.themeSelector) configPanel = new ConfigPanel(this, options.configUi, options.themeSelector);
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

    /** Accent colour (ARGB): the one from the options, or the theme's. */
    public int getAccent() {
        return options.accent != null ? options.accent : theme.getDefaultAccent();
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

    public Notifications notifications() {
        return notifications;
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
            } else if (setting.getType() == SettingTypes.CHECKBOX) {
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
            if (binding.getValue() != key || binding.getKey().getType() != SettingTypes.CHECKBOX) continue;
            @SuppressWarnings("unchecked") Setting<Boolean> toggle = (Setting<Boolean>) binding.getKey();
            KeybindMode mode = keybinds.getMode(toggle);
            if (mode == KeybindMode.ON_HOTKEY) toggle.set(false);
            else if (mode == KeybindMode.OFF_HOTKEY) toggle.set(true);
        }
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
        hudFrame.render(platform.getRenderer(), Icarus.getAtlas(), scale, fbWidth, fbHeight, hudPaint);
    }

    private void paintHud() {
        for (HudElement element : hud) element.render(hudFrame.getCanvas(), hudContext);
    }

    /* ---- called by the menu ---- */

    public void interactionEnded() {
        store.interactionEnded();
    }

    public void menuClosed() {
        store.closed();
    }
}
