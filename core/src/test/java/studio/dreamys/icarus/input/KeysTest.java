package studio.dreamys.icarus.input;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class KeysTest {
    @Test
    void namesMatchLwjgl2() {
        assertEquals("RSHIFT", Keys.getKeyName(Keys.KEY_RSHIFT));
        assertEquals("6", Keys.getKeyName(Keys.KEY_6));
        assertEquals("LMETA", Keys.getKeyName(Keys.KEY_LWIN)); //LWJGL2 skips the *WIN aliases
        assertNull(Keys.getKeyName(84));
    }

    @Test
    void translatesGlfwCodes() {
        assertEquals(Keys.KEY_RSHIFT, Keys.fromGlfw(344));
        assertEquals(Keys.KEY_A, Keys.fromGlfw(65));
        assertEquals(Keys.KEY_0, Keys.fromGlfw(48));
        assertEquals(Keys.KEY_6, Keys.fromGlfw(54));
        assertEquals(Keys.KEY_BACK, Keys.fromGlfw(259));
        assertEquals(Keys.KEY_ESCAPE, Keys.fromGlfw(256));
        assertEquals(Keys.KEY_F12, Keys.fromGlfw(301));
        assertEquals(Keys.KEY_NONE, Keys.fromGlfw(-1));
    }

    @Test
    void translatesSdlScancodes() {
        assertEquals(Keys.KEY_RSHIFT, Keys.fromSdl(229));
        assertEquals(Keys.KEY_A, Keys.fromSdl(4));
        assertEquals(Keys.KEY_0, Keys.fromSdl(39));
        assertEquals(Keys.KEY_6, Keys.fromSdl(35));
        assertEquals(Keys.KEY_BACK, Keys.fromSdl(42));
        assertEquals(Keys.KEY_ESCAPE, Keys.fromSdl(41));
        assertEquals(Keys.KEY_F12, Keys.fromSdl(69));
        assertEquals(Keys.KEY_PRIOR, Keys.fromSdl(75));
        assertEquals(Keys.KEY_NONE, Keys.fromSdl(-1));
    }
}
