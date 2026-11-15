package studio.dreamys.icarus.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import studio.dreamys.icarus.annotation.field.DropdownOptions;
import studio.dreamys.icarus.annotation.field.SliderOptions;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** The built-in setting types, in matching order. */
public final class SettingTypes {
    private SettingTypes() {
    }

    /** {@code Runnable} field: a button (not saved). */
    public static final SettingType<Runnable> BUTTON = new SettingType<Runnable>("button") {
        @Override
        public boolean matches(Field field) {
            return field.getType() == Runnable.class;
        }

        @Override
        public boolean isPersistent() {
            return false;
        }

        @Override
        public JsonElement toJson(Runnable value) {
            return JsonNull.INSTANCE;
        }

        @Override
        public Runnable fromJson(JsonElement json, Setting<Runnable> setting) {
            return null;
        }
    };

    /** {@code boolean} field: a checkbox. */
    public static final SettingType<Boolean> CHECKBOX = new SettingType<Boolean>("checkbox") {
        @Override
        public boolean matches(Field field) {
            return field.getType() == boolean.class || field.getType() == Boolean.class;
        }

        @Override
        public JsonElement toJson(Boolean value) {
            return new JsonPrimitive(value);
        }

        @Override
        public Boolean fromJson(JsonElement json, Setting<Boolean> setting) {
            return json.isJsonPrimitive() && json.getAsJsonPrimitive().isBoolean() ? json.getAsBoolean() : null;
        }
    };

    /** {@code String} field with {@link DropdownOptions}: a single-choice dropdown. */
    public static final SettingType<String> CHOICE = new SettingType<String>("choice") {
        @Override
        public boolean matches(Field field) {
            return field.getType() == String.class && field.isAnnotationPresent(DropdownOptions.class);
        }

        @Override
        public JsonElement toJson(String value) {
            return value == null ? JsonNull.INSTANCE : new JsonPrimitive(value);
        }

        @Override
        public String fromJson(JsonElement json, Setting<String> setting) {
            if (!json.isJsonPrimitive()) return null;
            String value = json.getAsString();
            return setting.getOptions().contains(value) ? value : null;
        }
    };

    /** {@code String[]} field with {@link DropdownOptions}: a multi-choice dropdown. */
    public static final SettingType<String[]> COMBO = new SettingType<String[]>("combo") {
        @Override
        public boolean matches(Field field) {
            return field.getType() == String[].class && field.isAnnotationPresent(DropdownOptions.class);
        }

        @Override
        public String[] copy(String[] value) {
            return value == null ? null : value.clone();
        }

        @Override
        public boolean equal(String[] a, String[] b) {
            return Arrays.equals(a, b);
        }

        @Override
        public JsonElement toJson(String[] value) {
            JsonArray array = new JsonArray();
            if (value != null) for (String s : value) array.add(new JsonPrimitive(s));
            return array;
        }

        @Override
        public String[] fromJson(JsonElement json, Setting<String[]> setting) {
            if (!json.isJsonArray()) return null;
            List<String> values = new ArrayList<>();
            for (JsonElement e : json.getAsJsonArray()) {
                if (e.isJsonPrimitive() && setting.getOptions().contains(e.getAsString()) && !values.contains(e.getAsString())) values.add(e.getAsString());
            }
            //keep the declared option order regardless of the order things were ticked in
            List<String> ordered = new ArrayList<>();
            for (String option : setting.getOptions()) if (values.contains(option)) ordered.add(option);
            return ordered.toArray(new String[0]);
        }
    };

    /** {@code String} field without options: a text field. */
    public static final SettingType<String> FIELD = new SettingType<String>("field") {
        @Override
        public boolean matches(Field field) {
            return field.getType() == String.class;
        }

        @Override
        public JsonElement toJson(String value) {
            return value == null ? JsonNull.INSTANCE : new JsonPrimitive(value);
        }

        @Override
        public String fromJson(JsonElement json, Setting<String> setting) {
            if (!json.isJsonPrimitive()) return null;
            String value = json.getAsString();
            return value.length() > setting.getMaxLength() ? value.substring(0, setting.getMaxLength()) : value;
        }
    };

    /** {@code double}/{@code float}/{@code int}/{@code long} field with {@link SliderOptions}: a slider. */
    public static final SettingType<Double> SLIDER = new SettingType<Double>("slider") {
        @Override
        public boolean matches(Field field) {
            Class<?> t = field.getType();
            return field.isAnnotationPresent(SliderOptions.class)
                    && (t == double.class || t == float.class || t == int.class || t == long.class
                    || t == Double.class || t == Float.class || t == Integer.class || t == Long.class);
        }

        @Override
        public Double load(Field field) throws IllegalAccessException {
            Object value = field.get(null);
            return value == null ? 0 : ((Number) value).doubleValue();
        }

        @Override
        public void store(Field field, Double value) throws IllegalAccessException {
            Class<?> t = field.getType();
            if (t == double.class || t == Double.class) field.set(null, value);
            else if (t == float.class || t == Float.class) field.set(null, value.floatValue());
            else if (t == int.class || t == Integer.class) field.set(null, (int) Math.round(value));
            else field.set(null, Math.round(value));
        }

        @Override
        public JsonElement toJson(Double value) {
            return new JsonPrimitive(value);
        }

        @Override
        public Double fromJson(JsonElement json, Setting<Double> setting) {
            if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isNumber()) return null;
            return setting.getSlider().sanitize(json.getAsDouble());
        }
    };

    /** {@link Color} field: a colour picker. Stored as {@code "#AARRGGBB"}. */
    public static final SettingType<Color> COLOR = new SettingType<Color>("color") {
        @Override
        public boolean matches(Field field) {
            return field.getType() == Color.class;
        }

        @Override
        public JsonElement toJson(Color value) {
            return value == null ? JsonNull.INSTANCE : new JsonPrimitive(String.format("#%08X", value.getRGB()));
        }

        @Override
        public Color fromJson(JsonElement json, Setting<Color> setting) {
            if (!json.isJsonPrimitive()) return null;
            String s = json.getAsString();
            if (!s.matches("#[0-9a-fA-F]{8}")) return null;
            return new Color((int) Long.parseLong(s.substring(1), 16), true);
        }
    };

    /** {@link ListModel} field: a list box. The selected item is saved. */
    public static final SettingType<String> LIST = new SettingType<String>("list") {
        @Override
        public boolean matches(Field field) {
            return field.getType() == ListModel.class;
        }

        @Override
        public String load(Field field) throws IllegalAccessException {
            return model(field).getSelected();
        }

        @Override
        public void store(Field field, String value) throws IllegalAccessException {
            model(field).select(value);
        }

        private ListModel model(Field field) throws IllegalAccessException {
            ListModel model = (ListModel) field.get(null);
            if (model == null) throw new IllegalStateException("ListModel field " + field + " is null");
            return model;
        }

        @Override
        public JsonElement toJson(String value) {
            return value == null ? JsonNull.INSTANCE : new JsonPrimitive(value);
        }

        @Override
        public String fromJson(JsonElement json, Setting<String> setting) {
            return json.isJsonPrimitive() ? json.getAsString() : null;
        }
    };

    /** In matching order: the more specific types first. */
    /** {@link Label} field: a text-only row (optionally with a keybind). Not saved. */
    public static final SettingType<Label> LABEL = new SettingType<Label>("label") {
        @Override
        public boolean matches(Field field) {
            return field.getType() == Label.class;
        }

        @Override
        public boolean isPersistent() {
            return false;
        }

        @Override
        public JsonElement toJson(Label value) {
            return JsonNull.INSTANCE;
        }

        @Override
        public Label fromJson(JsonElement json, Setting<Label> setting) {
            return null;
        }
    };

    public static List<SettingType<?>> builtIn() {
        return Arrays.<SettingType<?>>asList(BUTTON, CHECKBOX, CHOICE, COMBO, FIELD, SLIDER, COLOR, LIST, LABEL);
    }
}
