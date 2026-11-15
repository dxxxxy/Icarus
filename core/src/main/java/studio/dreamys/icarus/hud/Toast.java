package studio.dreamys.icarus.hud;

import studio.dreamys.icarus.render.Canvas;
import studio.dreamys.icarus.render.Font;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A toast that slides in, stays for a while and slides out. Send it with {@code IcarusMod#toasts()}.
 * Timing is in client ticks; drawing interpolates between ticks.
 */
public final class Toast {
    public static final int SLIDE_TICKS = 6;

    private final String title;
    private final String message;
    private final int stayTicks;
    int age;

    private List<String> lines;
    private double linesWidth = -1;
    private Font linesFont;

    public Toast(String title, String message) {
        this(title, message, 40);
    }

    public Toast(String title, String message, int stayTicks) {
        this.title = title == null ? "" : title;
        this.message = message == null ? "" : message;
        this.stayTicks = stayTicks;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public int getStayTicks() {
        return stayTicks;
    }

    boolean isFinished() {
        return age >= SLIDE_TICKS * 2 + stayTicks;
    }

    /** Slide progress at a point in time: 0 hidden → 1 shown → 0 hidden, eased. */
    public double visibility(float partialTick) {
        double t = age + partialTick;
        double v;
        if (t < SLIDE_TICKS) v = t / SLIDE_TICKS;
        else if (t < SLIDE_TICKS + stayTicks) v = 1;
        else v = 1 - (t - SLIDE_TICKS - stayTicks) / SLIDE_TICKS;
        v = Math.max(0, Math.min(1, v));
        return 1 - Math.pow(1 - v, 3); //ease out
    }

    /** The message word-wrapped to {@code width} design units (cached until the width or font changes). */
    public List<String> lines(Canvas canvas, Font font, double width) {
        if (lines == null || width != linesWidth || font != linesFont) {
            linesWidth = width;
            linesFont = font;
            List<String> out = new ArrayList<>();
            StringBuilder line = new StringBuilder();
            for (String word : message.split(" ")) {
                String candidate = line.length() == 0 ? word : line + " " + word;
                if (line.length() > 0 && canvas.measure(font, candidate) > width) {
                    out.add(line.toString());
                    line.setLength(0);
                    line.append(word);
                } else {
                    line.setLength(0);
                    line.append(candidate);
                }
            }
            if (line.length() > 0) out.add(line.toString());
            lines = Collections.unmodifiableList(out);
        }
        return lines;
    }
}
