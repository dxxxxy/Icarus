package studio.dreamys.icarus.render;

import java.util.Arrays;

/** Open-addressing int → object map, so glyph lookups don't box code points. */
final class IntMap<V> {
    private static final int EMPTY = Integer.MIN_VALUE;

    private int[] keys = new int[256];
    private Object[] values = new Object[256];
    private int size;

    IntMap() {
        Arrays.fill(keys, EMPTY);
    }

    @SuppressWarnings("unchecked")
    V get(int key) {
        int mask = keys.length - 1;
        for (int i = mix(key) & mask; ; i = (i + 1) & mask) {
            int k = keys[i];
            if (k == key) return (V) values[i];
            if (k == EMPTY) return null;
        }
    }

    void put(int key, V value) {
        if ((size + 1) * 2 > keys.length) rehash(keys.length * 2);
        int mask = keys.length - 1;
        for (int i = mix(key) & mask; ; i = (i + 1) & mask) {
            if (keys[i] == key) {
                values[i] = value;
                return;
            }
            if (keys[i] == EMPTY) {
                keys[i] = key;
                values[i] = value;
                size++;
                return;
            }
        }
    }

    void clear() {
        Arrays.fill(keys, EMPTY);
        Arrays.fill(values, null);
        size = 0;
    }

    @SuppressWarnings("unchecked")
    private void rehash(int capacity) {
        int[] oldKeys = keys;
        Object[] oldValues = values;
        keys = new int[capacity];
        values = new Object[capacity];
        Arrays.fill(keys, EMPTY);
        size = 0;
        for (int i = 0; i < oldKeys.length; i++) if (oldKeys[i] != EMPTY) put(oldKeys[i], (V) oldValues[i]);
    }

    private static int mix(int key) {
        int h = key * 0x9E3779B9;
        return h ^ (h >>> 16);
    }
}
