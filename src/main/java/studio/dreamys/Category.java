package studio.dreamys;

import org.apache.commons.lang3.text.WordUtils;

public enum Category {
    CHAT, COSMETICS, DUNGEONS, HUD, MINES, MISC, MOVEMENT, RENDER;

    public String get() {
        return WordUtils.capitalizeFully(toString());
    }
}
