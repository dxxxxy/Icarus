package studio.dreamys.icarus.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/** Per-mod UI preferences ({@code icarus.json}): always auto-saved, independent of config profiles. */
public final class UiPrefs {
    private String theme;
    private double x = Double.NaN, y = Double.NaN;
    private String page, subPage;
    private String profile;

    public String getTheme() {
        return theme;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean hasPosition() {
        return !Double.isNaN(x) && !Double.isNaN(y);
    }

    public String getPage() {
        return page;
    }

    public String getSubPage() {
        return subPage;
    }

    public String getProfile() {
        return profile;
    }

    void setTheme(String theme) {
        this.theme = theme;
    }

    void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    void setPage(String page, String subPage) {
        this.page = page;
        this.subPage = subPage;
    }

    void setProfile(String profile) {
        this.profile = profile;
    }

    JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("version", 1);
        if (theme != null) json.addProperty("theme", theme);
        if (hasPosition()) {
            json.addProperty("x", x);
            json.addProperty("y", y);
        }
        if (page != null) json.addProperty("page", page);
        if (subPage != null) json.addProperty("subPage", subPage);
        if (profile != null) json.addProperty("profile", profile);
        return json;
    }

    void read(JsonObject json) {
        theme = string(json, "theme");
        page = string(json, "page");
        subPage = string(json, "subPage");
        profile = string(json, "profile");
        Double px = number(json, "x"), py = number(json, "y");
        if (px != null && py != null) setPosition(px, py);
    }

    private static String string(JsonObject json, String key) {
        JsonElement e = json.get(key);
        return e != null && e.isJsonPrimitive() ? e.getAsString() : null;
    }

    private static Double number(JsonObject json, String key) {
        JsonElement e = json.get(key);
        return e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber() ? e.getAsDouble() : null;
    }
}
