package studio.dreamys.icarus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

/** UTF-8 JSON files: tolerant reads and atomic writes. */
public final class JsonFiles {
    private static final Logger LOG = Logger.getLogger("Icarus");
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private JsonFiles() {
    }

    /**
     * Reads a JSON object. Returns null if the file is missing or empty. A file that isn't a valid JSON object is
     * moved aside to {@code <name>.broken-<millis>} (so it's never overwritten) and null is returned.
     */
    public static JsonObject read(Path file) {
        if (!Files.isRegularFile(file)) return null;
        String text;
        try {
            text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "[Icarus] Couldn't read " + file, e);
            return null;
        }
        if (text.trim().isEmpty()) return null;

        JsonObject object = parse(text);
        if (object != null) return object;

        Path broken = file.resolveSibling(file.getFileName() + ".broken-" + System.currentTimeMillis());
        try {
            Files.move(file, broken);
            LOG.warning("[Icarus] " + file + " isn't valid JSON; kept it as " + broken.getFileName() + " and continued with defaults");
        } catch (IOException e) {
            LOG.log(Level.WARNING, "[Icarus] " + file + " isn't valid JSON and couldn't be moved aside", e);
        }
        return null;
    }

    /** Parses a JSON object, or returns null if the text isn't one. */
    public static JsonObject parse(String text) {
        try (Reader reader = new StringReader(text)) {
            JsonElement element = new JsonParser().parse(reader);
            return element.isJsonObject() ? element.getAsJsonObject() : null;
        } catch (JsonParseException | IOException | IllegalStateException e) {
            return null;
        }
    }

    public static String toString(JsonElement json) {
        return GSON.toJson(json);
    }

    /** Writes through a temp file and an atomic move, so a crash never leaves a half-written file. */
    public static void writeAtomic(Path file, JsonElement json) throws IOException {
        Files.createDirectories(file.getParent());
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            GSON.toJson(json, writer);
        }
        try {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
