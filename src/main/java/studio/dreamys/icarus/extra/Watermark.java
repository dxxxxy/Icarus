package studio.dreamys.icarus.extra;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import studio.dreamys.icarus.util.position.Position;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.*;

@Getter
@Setter
public class Watermark {
    private String text;
    private Position position;
    private double x, y, width, height = RenderUtils.getFontHeight() + 7.5;
    private Color textColor;

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
    }

    @SubscribeEvent
    public void onGameOverlay(RenderGameOverlayEvent e) {
        if (e.type != RenderGameOverlayEvent.ElementType.TEXT) return;

        //update width in case text changes
        width = RenderUtils.getStringWidth(text) + 5;

        //position
        position.init(width, height);
        x = position.getX();
        y = position.getY();

        //draw our very professional skeet watermark
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("icarus", "watermark.png"));
        RenderUtils.drawTexture(x, y, width, height);

        //draw text
        RenderUtils.drawString(text, x + 1, y + 3.5, textColor);
    }

    public void enable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void disable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
