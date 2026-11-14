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
    /** Dark grey, icon tabs on the left, multi-colour accent strip. */
    public static final Theme CLASSIC = new SkeetStyleTheme();
    /** Indigo and pink, text tabs on top, three columns. */
    public static final Theme NEON = new FatalityStyleTheme();

    private static final Map<String, Theme> themes = new LinkedHashMap<>();

    static {
        register(CLASSIC);
        register(NEON);
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
