package studio.dreamys.icarus.theme;

import studio.dreamys.icarus.theme.fatality.FatalityStyleTheme;
import studio.dreamys.icarus.theme.skeet.SkeetStyleTheme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The themes players can pick from. Register custom themes with {@link #register}. */
public final class Themes {
    /** Recreation of the skeet / gamesense menu: icon tabs on the left, multi-colour strip, two columns. */
    public static final Theme SKEET = new SkeetStyleTheme();
    /** Recreation of the fatality menu: indigo and pink, text tabs in the header, three columns. */
    public static final Theme FATALITY = new FatalityStyleTheme();

    private static final Map<String, Theme> themes = new LinkedHashMap<>();

    static {
        register(SKEET);
        register(FATALITY);
    }

    private Themes() {
    }

    public static void register(Theme theme) {
        themes.put(theme.getId(), theme);
    }

    public static Theme byId(String id) {
        return id == null ? null : themes.get(id);
    }

    public static Theme byDisplayName(String name) {
        for (Theme theme : themes.values()) if (theme.getDisplayName().equals(name)) return theme;
        return null;
    }

    public static List<Theme> all() {
        return Collections.unmodifiableList(new ArrayList<>(themes.values()));
    }

    public static List<String> displayNames() {
        List<String> names = new ArrayList<>();
        for (Theme theme : themes.values()) names.add(theme.getDisplayName());
        return names;
    }
}
