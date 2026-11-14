package studio.dreamys.icarus.input;

import java.util.HashMap;
import java.util.Map;

/**
 * Key codes used throughout Icarus ({@code @IKeybind}, keybind.json, {@code Window.key}, component input).
 * <p>
 * The values are LWJGL2's {@code org.lwjgl.input.Keyboard} codes, so configs and annotations written for
 * Icarus v2 (Forge 1.8.9) keep working. Platforms on LWJGL3 translate GLFW codes with {@link #fromGlfw(int)},
 * and Minecraft 26.x (SDL3) translates scancodes with {@link #fromSdl(int)}.
 */
@SuppressWarnings("unused")
public final class Keys {
    public static final int KEY_NONE = 0
;
    public static final int KEY_ESCAPE = 1
;
    public static final int KEY_1 = 2
;
    public static final int KEY_2 = 3
;
    public static final int KEY_3 = 4
;
    public static final int KEY_4 = 5
;
    public static final int KEY_5 = 6
;
    public static final int KEY_6 = 7
;
    public static final int KEY_7 = 8
;
    public static final int KEY_8 = 9
;
    public static final int KEY_9 = 10
;
    public static final int KEY_0 = 11
;
    public static final int KEY_MINUS = 12
;
    public static final int KEY_EQUALS = 13
;
    public static final int KEY_BACK = 14
;
    public static final int KEY_TAB = 15
;
    public static final int KEY_Q = 16
;
    public static final int KEY_W = 17
;
    public static final int KEY_E = 18
;
    public static final int KEY_R = 19
;
    public static final int KEY_T = 20
;
    public static final int KEY_Y = 21
;
    public static final int KEY_U = 22
;
    public static final int KEY_I = 23
;
    public static final int KEY_O = 24
;
    public static final int KEY_P = 25
;
    public static final int KEY_LBRACKET = 26
;
    public static final int KEY_RBRACKET = 27
;
    public static final int KEY_RETURN = 28
;
    public static final int KEY_LCONTROL = 29
;
    public static final int KEY_A = 30
;
    public static final int KEY_S = 31
;
    public static final int KEY_D = 32
;
    public static final int KEY_F = 33
;
    public static final int KEY_G = 34
;
    public static final int KEY_H = 35
;
    public static final int KEY_J = 36
;
    public static final int KEY_K = 37
;
    public static final int KEY_L = 38
;
    public static final int KEY_SEMICOLON = 39
;
    public static final int KEY_APOSTROPHE = 40
;
    public static final int KEY_GRAVE = 41
;
    public static final int KEY_LSHIFT = 42
;
    public static final int KEY_BACKSLASH = 43
;
    public static final int KEY_Z = 44
;
    public static final int KEY_X = 45
;
    public static final int KEY_C = 46
;
    public static final int KEY_V = 47
;
    public static final int KEY_B = 48
;
    public static final int KEY_N = 49
;
    public static final int KEY_M = 50
;
    public static final int KEY_COMMA = 51
;
    public static final int KEY_PERIOD = 52
;
    public static final int KEY_SLASH = 53
;
    public static final int KEY_RSHIFT = 54
;
    public static final int KEY_MULTIPLY = 55
;
    public static final int KEY_LMENU = 56
;
    public static final int KEY_SPACE = 57
;
    public static final int KEY_CAPITAL = 58
;
    public static final int KEY_F1 = 59
;
    public static final int KEY_F2 = 60
;
    public static final int KEY_F3 = 61
;
    public static final int KEY_F4 = 62
;
    public static final int KEY_F5 = 63
;
    public static final int KEY_F6 = 64
;
    public static final int KEY_F7 = 65
;
    public static final int KEY_F8 = 66
;
    public static final int KEY_F9 = 67
;
    public static final int KEY_F10 = 68
;
    public static final int KEY_NUMLOCK = 69
;
    public static final int KEY_SCROLL = 70
;
    public static final int KEY_NUMPAD7 = 71
;
    public static final int KEY_NUMPAD8 = 72
;
    public static final int KEY_NUMPAD9 = 73
;
    public static final int KEY_SUBTRACT = 74
;
    public static final int KEY_NUMPAD4 = 75
;
    public static final int KEY_NUMPAD5 = 76
;
    public static final int KEY_NUMPAD6 = 77
;
    public static final int KEY_ADD = 78
;
    public static final int KEY_NUMPAD1 = 79
;
    public static final int KEY_NUMPAD2 = 80
;
    public static final int KEY_NUMPAD3 = 81
;
    public static final int KEY_NUMPAD0 = 82
;
    public static final int KEY_DECIMAL = 83
;
    public static final int KEY_F11 = 87
;
    public static final int KEY_F12 = 88
;
    public static final int KEY_F13 = 100
;
    public static final int KEY_F14 = 101
;
    public static final int KEY_F15 = 102
;
    public static final int KEY_F16 = 103
;
    public static final int KEY_F17 = 104
;
    public static final int KEY_F18 = 105
;
    public static final int KEY_KANA = 112
;
    public static final int KEY_F19 = 113
;
    public static final int KEY_CONVERT = 121
;
    public static final int KEY_NOCONVERT = 123
;
    public static final int KEY_YEN = 125
;
    public static final int KEY_NUMPADEQUALS = 141
;
    public static final int KEY_CIRCUMFLEX = 144
;
    public static final int KEY_AT = 145
;
    public static final int KEY_COLON = 146
;
    public static final int KEY_UNDERLINE = 147
;
    public static final int KEY_KANJI = 148
;
    public static final int KEY_STOP = 149
;
    public static final int KEY_AX = 150
;
    public static final int KEY_UNLABELED = 151
;
    public static final int KEY_NUMPADENTER = 156
;
    public static final int KEY_RCONTROL = 157
;
    public static final int KEY_SECTION = 167
;
    public static final int KEY_NUMPADCOMMA = 179
;
    public static final int KEY_DIVIDE = 181
;
    public static final int KEY_SYSRQ = 183
;
    public static final int KEY_RMENU = 184
;
    public static final int KEY_FUNCTION = 196
;
    public static final int KEY_PAUSE = 197
;
    public static final int KEY_HOME = 199
;
    public static final int KEY_UP = 200
;
    public static final int KEY_PRIOR = 201
;
    public static final int KEY_LEFT = 203
;
    public static final int KEY_RIGHT = 205
;
    public static final int KEY_END = 207
;
    public static final int KEY_DOWN = 208
;
    public static final int KEY_NEXT = 209
;
    public static final int KEY_INSERT = 210
;
    public static final int KEY_DELETE = 211
;
    public static final int KEY_CLEAR = 218
;
    public static final int KEY_LMETA = 219
;
    public static final int KEY_LWIN = 219
;
    public static final int KEY_RMETA = 220
;
    public static final int KEY_RWIN = 220
;
    public static final int KEY_APPS = 221
;
    public static final int KEY_POWER = 222
;
    public static final int KEY_SLEEP = 223
;

    private static final String[] NAMES = new String[256];
    private static final Map<Integer, Integer> GLFW_TO_KEYS = new HashMap<>();
    private static final Map<Integer, Integer> SDL_TO_KEYS = new HashMap<>();

    static {
        NAMES[0] = "NONE";
        NAMES[1] = "ESCAPE";
        NAMES[2] = "1";
        NAMES[3] = "2";
        NAMES[4] = "3";
        NAMES[5] = "4";
        NAMES[6] = "5";
        NAMES[7] = "6";
        NAMES[8] = "7";
        NAMES[9] = "8";
        NAMES[10] = "9";
        NAMES[11] = "0";
        NAMES[12] = "MINUS";
        NAMES[13] = "EQUALS";
        NAMES[14] = "BACK";
        NAMES[15] = "TAB";
        NAMES[16] = "Q";
        NAMES[17] = "W";
        NAMES[18] = "E";
        NAMES[19] = "R";
        NAMES[20] = "T";
        NAMES[21] = "Y";
        NAMES[22] = "U";
        NAMES[23] = "I";
        NAMES[24] = "O";
        NAMES[25] = "P";
        NAMES[26] = "LBRACKET";
        NAMES[27] = "RBRACKET";
        NAMES[28] = "RETURN";
        NAMES[29] = "LCONTROL";
        NAMES[30] = "A";
        NAMES[31] = "S";
        NAMES[32] = "D";
        NAMES[33] = "F";
        NAMES[34] = "G";
        NAMES[35] = "H";
        NAMES[36] = "J";
        NAMES[37] = "K";
        NAMES[38] = "L";
        NAMES[39] = "SEMICOLON";
        NAMES[40] = "APOSTROPHE";
        NAMES[41] = "GRAVE";
        NAMES[42] = "LSHIFT";
        NAMES[43] = "BACKSLASH";
        NAMES[44] = "Z";
        NAMES[45] = "X";
        NAMES[46] = "C";
        NAMES[47] = "V";
        NAMES[48] = "B";
        NAMES[49] = "N";
        NAMES[50] = "M";
        NAMES[51] = "COMMA";
        NAMES[52] = "PERIOD";
        NAMES[53] = "SLASH";
        NAMES[54] = "RSHIFT";
        NAMES[55] = "MULTIPLY";
        NAMES[56] = "LMENU";
        NAMES[57] = "SPACE";
        NAMES[58] = "CAPITAL";
        NAMES[59] = "F1";
        NAMES[60] = "F2";
        NAMES[61] = "F3";
        NAMES[62] = "F4";
        NAMES[63] = "F5";
        NAMES[64] = "F6";
        NAMES[65] = "F7";
        NAMES[66] = "F8";
        NAMES[67] = "F9";
        NAMES[68] = "F10";
        NAMES[69] = "NUMLOCK";
        NAMES[70] = "SCROLL";
        NAMES[71] = "NUMPAD7";
        NAMES[72] = "NUMPAD8";
        NAMES[73] = "NUMPAD9";
        NAMES[74] = "SUBTRACT";
        NAMES[75] = "NUMPAD4";
        NAMES[76] = "NUMPAD5";
        NAMES[77] = "NUMPAD6";
        NAMES[78] = "ADD";
        NAMES[79] = "NUMPAD1";
        NAMES[80] = "NUMPAD2";
        NAMES[81] = "NUMPAD3";
        NAMES[82] = "NUMPAD0";
        NAMES[83] = "DECIMAL";
        NAMES[87] = "F11";
        NAMES[88] = "F12";
        NAMES[100] = "F13";
        NAMES[101] = "F14";
        NAMES[102] = "F15";
        NAMES[103] = "F16";
        NAMES[104] = "F17";
        NAMES[105] = "F18";
        NAMES[112] = "KANA";
        NAMES[113] = "F19";
        NAMES[121] = "CONVERT";
        NAMES[123] = "NOCONVERT";
        NAMES[125] = "YEN";
        NAMES[141] = "NUMPADEQUALS";
        NAMES[144] = "CIRCUMFLEX";
        NAMES[145] = "AT";
        NAMES[146] = "COLON";
        NAMES[147] = "UNDERLINE";
        NAMES[148] = "KANJI";
        NAMES[149] = "STOP";
        NAMES[150] = "AX";
        NAMES[151] = "UNLABELED";
        NAMES[156] = "NUMPADENTER";
        NAMES[157] = "RCONTROL";
        NAMES[167] = "SECTION";
        NAMES[179] = "NUMPADCOMMA";
        NAMES[181] = "DIVIDE";
        NAMES[183] = "SYSRQ";
        NAMES[184] = "RMENU";
        NAMES[196] = "FUNCTION";
        NAMES[197] = "PAUSE";
        NAMES[199] = "HOME";
        NAMES[200] = "UP";
        NAMES[201] = "PRIOR";
        NAMES[203] = "LEFT";
        NAMES[205] = "RIGHT";
        NAMES[207] = "END";
        NAMES[208] = "DOWN";
        NAMES[209] = "NEXT";
        NAMES[210] = "INSERT";
        NAMES[211] = "DELETE";
        NAMES[218] = "CLEAR";
        NAMES[219] = "LMETA";
        NAMES[220] = "RMETA";
        NAMES[221] = "APPS";
        NAMES[222] = "POWER";
        NAMES[223] = "SLEEP";

        // GLFW key codes (LWJGL3) -> LWJGL2 key codes
        glfw(32, KEY_SPACE);
        glfw(39, KEY_APOSTROPHE);
        glfw(44, KEY_COMMA);
        glfw(45, KEY_MINUS);
        glfw(46, KEY_PERIOD);
        glfw(47, KEY_SLASH);
        glfw(48, KEY_0);
        for (int i = 1; i <= 9; i++) glfw(48 + i, KEY_1 + i - 1);
        glfw(59, KEY_SEMICOLON);
        glfw(61, KEY_EQUALS);
        int[] letters = {KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, KEY_G, KEY_H, KEY_I, KEY_J, KEY_K, KEY_L, KEY_M,
                KEY_N, KEY_O, KEY_P, KEY_Q, KEY_R, KEY_S, KEY_T, KEY_U, KEY_V, KEY_W, KEY_X, KEY_Y, KEY_Z};
        for (int i = 0; i < letters.length; i++) glfw(65 + i, letters[i]);
        glfw(91, KEY_LBRACKET);
        glfw(92, KEY_BACKSLASH);
        glfw(93, KEY_RBRACKET);
        glfw(96, KEY_GRAVE);
        glfw(256, KEY_ESCAPE);
        glfw(257, KEY_RETURN);
        glfw(258, KEY_TAB);
        glfw(259, KEY_BACK);
        glfw(260, KEY_INSERT);
        glfw(261, KEY_DELETE);
        glfw(262, KEY_RIGHT);
        glfw(263, KEY_LEFT);
        glfw(264, KEY_DOWN);
        glfw(265, KEY_UP);
        glfw(266, KEY_PRIOR);
        glfw(267, KEY_NEXT);
        glfw(268, KEY_HOME);
        glfw(269, KEY_END);
        glfw(280, KEY_CAPITAL);
        glfw(281, KEY_SCROLL);
        glfw(282, KEY_NUMLOCK);
        glfw(283, KEY_SYSRQ);
        glfw(284, KEY_PAUSE);
        int[] functions = {KEY_F1, KEY_F2, KEY_F3, KEY_F4, KEY_F5, KEY_F6, KEY_F7, KEY_F8, KEY_F9, KEY_F10,
                KEY_F11, KEY_F12, KEY_F13, KEY_F14, KEY_F15, KEY_F16, KEY_F17, KEY_F18, KEY_F19};
        for (int i = 0; i < functions.length; i++) glfw(290 + i, functions[i]);
        int[] numpad = {KEY_NUMPAD0, KEY_NUMPAD1, KEY_NUMPAD2, KEY_NUMPAD3, KEY_NUMPAD4,
                KEY_NUMPAD5, KEY_NUMPAD6, KEY_NUMPAD7, KEY_NUMPAD8, KEY_NUMPAD9};
        for (int i = 0; i < numpad.length; i++) glfw(320 + i, numpad[i]);
        glfw(330, KEY_DECIMAL);
        glfw(331, KEY_DIVIDE);
        glfw(332, KEY_MULTIPLY);
        glfw(333, KEY_SUBTRACT);
        glfw(334, KEY_ADD);
        glfw(335, KEY_NUMPADENTER);
        glfw(336, KEY_NUMPADEQUALS);
        glfw(340, KEY_LSHIFT);
        glfw(341, KEY_LCONTROL);
        glfw(342, KEY_LMENU);
        glfw(343, KEY_LMETA);
        glfw(344, KEY_RSHIFT);
        glfw(345, KEY_RCONTROL);
        glfw(346, KEY_RMENU);
        glfw(347, KEY_RMETA);
        glfw(348, KEY_APPS);

        //SDL3 scancodes (USB HID usage ids), used by Minecraft 26.x
        sdl(4, KEY_A);
        sdl(5, KEY_B);
        sdl(6, KEY_C);
        sdl(7, KEY_D);
        sdl(8, KEY_E);
        sdl(9, KEY_F);
        sdl(10, KEY_G);
        sdl(11, KEY_H);
        sdl(12, KEY_I);
        sdl(13, KEY_J);
        sdl(14, KEY_K);
        sdl(15, KEY_L);
        sdl(16, KEY_M);
        sdl(17, KEY_N);
        sdl(18, KEY_O);
        sdl(19, KEY_P);
        sdl(20, KEY_Q);
        sdl(21, KEY_R);
        sdl(22, KEY_S);
        sdl(23, KEY_T);
        sdl(24, KEY_U);
        sdl(25, KEY_V);
        sdl(26, KEY_W);
        sdl(27, KEY_X);
        sdl(28, KEY_Y);
        sdl(29, KEY_Z);
        sdl(30, KEY_1);
        sdl(31, KEY_2);
        sdl(32, KEY_3);
        sdl(33, KEY_4);
        sdl(34, KEY_5);
        sdl(35, KEY_6);
        sdl(36, KEY_7);
        sdl(37, KEY_8);
        sdl(38, KEY_9);
        sdl(39, KEY_0);
        sdl(40, KEY_RETURN);
        sdl(41, KEY_ESCAPE);
        sdl(42, KEY_BACK);
        sdl(43, KEY_TAB);
        sdl(44, KEY_SPACE);
        sdl(45, KEY_MINUS);
        sdl(46, KEY_EQUALS);
        sdl(47, KEY_LBRACKET);
        sdl(48, KEY_RBRACKET);
        sdl(49, KEY_BACKSLASH);
        sdl(51, KEY_SEMICOLON);
        sdl(52, KEY_APOSTROPHE);
        sdl(53, KEY_GRAVE);
        sdl(54, KEY_COMMA);
        sdl(55, KEY_PERIOD);
        sdl(56, KEY_SLASH);
        sdl(57, KEY_CAPITAL);
        sdl(58, KEY_F1);
        sdl(59, KEY_F2);
        sdl(60, KEY_F3);
        sdl(61, KEY_F4);
        sdl(62, KEY_F5);
        sdl(63, KEY_F6);
        sdl(64, KEY_F7);
        sdl(65, KEY_F8);
        sdl(66, KEY_F9);
        sdl(67, KEY_F10);
        sdl(68, KEY_F11);
        sdl(69, KEY_F12);
        sdl(70, KEY_SYSRQ);
        sdl(71, KEY_SCROLL);
        sdl(72, KEY_PAUSE);
        sdl(73, KEY_INSERT);
        sdl(74, KEY_HOME);
        sdl(75, KEY_PRIOR);
        sdl(76, KEY_DELETE);
        sdl(77, KEY_END);
        sdl(78, KEY_NEXT);
        sdl(79, KEY_RIGHT);
        sdl(80, KEY_LEFT);
        sdl(81, KEY_DOWN);
        sdl(82, KEY_UP);
        sdl(83, KEY_NUMLOCK);
        sdl(84, KEY_DIVIDE);
        sdl(85, KEY_MULTIPLY);
        sdl(86, KEY_SUBTRACT);
        sdl(87, KEY_ADD);
        sdl(88, KEY_NUMPADENTER);
        sdl(89, KEY_NUMPAD1);
        sdl(90, KEY_NUMPAD2);
        sdl(91, KEY_NUMPAD3);
        sdl(92, KEY_NUMPAD4);
        sdl(93, KEY_NUMPAD5);
        sdl(94, KEY_NUMPAD6);
        sdl(95, KEY_NUMPAD7);
        sdl(96, KEY_NUMPAD8);
        sdl(97, KEY_NUMPAD9);
        sdl(98, KEY_NUMPAD0);
        sdl(99, KEY_DECIMAL);
        sdl(101, KEY_APPS);
        sdl(103, KEY_NUMPADEQUALS);
        sdl(104, KEY_F13);
        sdl(105, KEY_F14);
        sdl(106, KEY_F15);
        sdl(107, KEY_F16);
        sdl(108, KEY_F17);
        sdl(109, KEY_F18);
        sdl(110, KEY_F19);
        sdl(220, KEY_NUMPADCOMMA);
        sdl(224, KEY_LCONTROL);
        sdl(225, KEY_LSHIFT);
        sdl(226, KEY_LMENU);
        sdl(227, KEY_LMETA);
        sdl(228, KEY_RCONTROL);
        sdl(229, KEY_RSHIFT);
        sdl(230, KEY_RMENU);
        sdl(231, KEY_RMETA);
    }

    private Keys() {
    }

    private static void glfw(int glfw, int key) {
        GLFW_TO_KEYS.put(glfw, key);
    }

    private static void sdl(int scancode, int key) {
        SDL_TO_KEYS.put(scancode, key);
    }

    /** Same as LWJGL2's {@code Keyboard.getKeyName}: e.g. "RSHIFT", "6", or null for unknown codes. */
    public static String getKeyName(int key) {
        return key >= 0 && key < NAMES.length ? NAMES[key] : null;
    }

    /** Translates a GLFW key code to the matching code in this class, or {@link #KEY_NONE}. */
    public static int fromGlfw(int glfwKey) {
        Integer key = GLFW_TO_KEYS.get(glfwKey);
        return key == null ? KEY_NONE : key;
    }

    /** Translates an SDL3 scancode (Minecraft 26.x key codes) to the matching code in this class, or {@link #KEY_NONE}. */
    public static int fromSdl(int scancode) {
        Integer key = SDL_TO_KEYS.get(scancode);
        return key == null ? KEY_NONE : key;
    }
}
