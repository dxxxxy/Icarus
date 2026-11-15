package studio.dreamys.icarus.settings;

/** Receives every setting change (implemented by the owning {@code IcarusMod}). */
public interface SettingListener {
    /**
     * @param byUser true for a change by the user or mod code (a pending config change), false when a value was
     *               applied from a config file or a reset
     */
    void changed(Setting<?> setting, boolean byUser);
}
