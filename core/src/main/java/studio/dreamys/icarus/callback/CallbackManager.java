package studio.dreamys.icarus.callback;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CallbackManager {
    private static Map<Field, Runnable> callbacks = new HashMap<>();

    public static void register(Field field, Runnable callback) {
        callbacks.put(field, callback);
    }

    public static void fire(Field field) {
        if (callbacks.containsKey(field)) {
            callbacks.entrySet().stream()
                .filter(entry -> entry.getKey().equals(field))
                .forEach(entry -> entry.getValue().run());
        }
    }
}
