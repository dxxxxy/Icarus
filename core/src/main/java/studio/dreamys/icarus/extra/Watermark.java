package studio.dreamys.icarus.extra;

import lombok.Getter;
import lombok.Setter;
import studio.dreamys.icarus.Icarus;
import studio.dreamys.icarus.util.RenderUtils;
import studio.dreamys.icarus.util.Textures;
import studio.dreamys.icarus.util.position.Position;

import java.awt.*;

@Getter
@Setter
public class Watermark implements HudElement {
    private String text;
    private Position position;
    private double x, y, width, height = RenderUtils.getFontHeight() + 7.5;
    private Color textColor;
    private boolean enabled;

    public Watermark(String text) {
        this(text, Position.TOP_RIGHT);
    }

    public Watermark(String text, Position position) {
        this(text, position, Color.WHITE);
    }

    public Watermark(String text, Position position, Color textColor) {
        this.text = text;
        this.position = position;
        this.textColor = textColor;

        Icarus.registerHud(this);
    }

    @Override
    public void renderHud() {
        if (!enabled) return;

        //update width in case text changes
        width = RenderUtils.getStringWidth(text) + 5;

        //position
        position.init(width, height);
        x = position.getX();
        y = position.getY();

        //draw our very professional skeet watermark
        RenderUtils.drawTexture(Textures.WATERMARK.get(), x, y, width, height);

        //draw text
        RenderUtils.drawString(text, x + 1, y + 3.5, textColor);
    }
}
