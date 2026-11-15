package studio.dreamys.icarus.settings;

import com.google.gson.JsonElement;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Everything Icarus needs to know about one kind of setting: which fields it applies to, how the value is read
 * from / written to the field, and how it's stored in config files. Widgets are looked up by type (see
 * {@code IcarusMod#replaceWidget}). The built-in types are in {@link SettingTypes}.
 */
public abstract class SettingType<T> {
    private final String id;

    protected SettingType(String id) {
        this.id = id;
    }

    /** Stable id, e.g. "checkbox". */
    public final String getId() {
        return id;
    }

    /** Whether a static field declares a setting of this type. */
    public abstract boolean matches(Field field);

    @SuppressWarnings("unchecked")
    public T load(Field field) throws IllegalAccessException {
        return (T) field.get(null);
    }

    public void store(Field field, T value) throws IllegalAccessException {
        field.set(null, value);
    }

    /** A copy safe to keep as a default (for mutable values such as arrays). */
    public T copy(T value) {
        return value;
    }

    public boolean equal(T a, T b) {
        return Objects.equals(a, b);
    }

    /** Whether values are saved in config files. */
    public boolean isPersistent() {
        return true;
    }

    public abstract JsonElement toJson(T value);

    /** Parses a stored value; returns null if it isn't valid for this setting (the current value is then kept). */
    public abstract T fromJson(JsonElement json, Setting<T> setting);

    @Override
    public String toString() {
        return id;
    }
}
