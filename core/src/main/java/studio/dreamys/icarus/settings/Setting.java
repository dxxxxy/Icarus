package studio.dreamys.icarus.settings;

import studio.dreamys.icarus.input.Keys;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * One configurable value: a static field of an {@code @IGroup} class, or a virtual value created in code
 * (e.g. the built-in config UI). All reads and writes go through here so every change marks the config dirty and
 * fires callbacks, whatever widget made it.
 */
public final class Setting<T> {
    /** Storage behind a setting. */
    public interface Holder<T> {
        T get();

        void set(T value);
    }

    private final GroupNode group;
    private final String name;
    private final String label;
    private final SettingType<T> type;
    private final Holder<T> holder;
    private final Field field;
    private final boolean persistent;

    private final List<String> options;
    private final SliderSpec slider;
    private final int maxLength;
    private final int defaultKey;

    private T defaultValue;
    private SettingListener listener;
    private Object extra;

    Setting(GroupNode group, String name, SettingType<T> type, Holder<T> holder, Field field, boolean persistent,
            List<String> options, SliderSpec slider, int maxLength, int defaultKey) {
        this.group = group;
        this.name = name;
        this.label = name.replace('_', ' ');
        this.type = type;
        this.holder = holder;
        this.field = field;
        this.persistent = persistent;
        this.options = options == null ? Collections.<String>emptyList() : Collections.unmodifiableList(options);
        this.slider = slider;
        this.maxLength = maxLength;
        this.defaultKey = defaultKey;
    }

    void captureDefault() {
        defaultValue = type.copy(holder.get());
    }

    public void setListener(SettingListener listener) {
        this.listener = listener;
    }

    /** An object attached to the setting, e.g. the {@link ListModel} behind a list setting. */
    public Object getExtra() {
        return extra;
    }

    public Setting<T> setExtra(Object extra) {
        this.extra = extra;
        return this;
    }

    public T get() {
        return holder.get();
    }

    /** A change made by the user or the mod: stores it, marks the config dirty and fires callbacks. */
    public void set(T value) {
        T old = holder.get();
        if (type.equal(old, value)) return;
        holder.set(value);
        if (listener != null) listener.changed(this, true);
    }

    /** A value applied from a config file or a reset: stores it and fires callbacks, but isn't a pending change. */
    public void load(T value) {
        T old = holder.get();
        if (type.equal(old, value)) return;
        holder.set(value);
        if (listener != null) listener.changed(this, false);
    }

    /** Notifies listeners that a mutable value (e.g. a {@link ListModel}'s items) changed in place. */
    public void touched() {
        if (listener != null) listener.changed(this, true);
    }

    public void resetToDefault() {
        load(type.copy(defaultValue));
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public GroupNode getGroup() {
        return group;
    }

    /** The Java field name; config files are keyed by it. */
    public String getName() {
        return name;
    }

    /** Display label: the name with underscores as spaces. */
    public String getLabel() {
        return label;
    }

    public SettingType<T> getType() {
        return type;
    }

    /** The backing field, or null for a virtual setting. */
    public Field getField() {
        return field;
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return field == null ? null : field.getAnnotation(annotation);
    }

    /** Whether the value is written to config files (buttons and the config UI's own controls are not). */
    public boolean isPersistent() {
        return persistent && type.isPersistent();
    }

    /** Dropdown options (Choice / Combo). */
    public List<String> getOptions() {
        return options;
    }

    /** Slider range, or null. */
    public SliderSpec getSlider() {
        return slider;
    }

    /** Text field length limit. */
    public int getMaxLength() {
        return maxLength;
    }

    /** Whether the setting can be bound to a key (has {@code @IKeybind}). */
    public boolean isBindable() {
        return defaultKey >= 0;
    }

    /** The {@link Keys} code from {@code @IKeybind}, {@link Keys#KEY_NONE} for none, or -1 if not bindable. */
    public int getDefaultKey() {
        return defaultKey;
    }

    @Override
    public String toString() {
        return group.getPath() + "/" + name;
    }

    /** Slider range and formatting. */
    public static final class SliderSpec {
        private final double min, max;
        private final boolean onlyInt;
        private final String units;

        public SliderSpec(double min, double max, boolean onlyInt, String units) {
            if (!(max > min)) throw new IllegalArgumentException("slider max (" + max + ") must be greater than min (" + min + ")");
            this.min = min;
            this.max = max;
            this.onlyInt = onlyInt;
            this.units = Objects.requireNonNull(units);
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public boolean isOnlyInt() {
            return onlyInt;
        }

        public String getUnits() {
            return units;
        }

        /** Clamps and rounds a value to what this slider can hold. */
        public double sanitize(double value) {
            if (Double.isNaN(value)) value = min;
            value = Math.max(min, Math.min(max, value));
            if (onlyInt) return Math.round(value);
            return java.math.BigDecimal.valueOf(value).setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();
        }

        public double fraction(double value) {
            return (Math.max(min, Math.min(max, value)) - min) / (max - min);
        }

        public double valueAt(double fraction) {
            return sanitize(min + (max - min) * Math.max(0, Math.min(1, fraction)));
        }

        public String format(double value) {
            String number = onlyInt ? Long.toString(Math.round(value)) : java.math.BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
            return number + units;
        }
    }
}
