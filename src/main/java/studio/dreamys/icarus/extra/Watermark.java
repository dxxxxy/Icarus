package studio.dreamys.icarus.extra;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.*;

@Getter
@Setter
public class Watermark {
    private String text;
    private double x, y, width, height;
    private Color textColor;

    public Watermark(String text) {
        this(text, Minecraft.getMinecraft().displayWidth / 2.0 - 5, 5);
    }

    public Watermark(String text, double x, double y) {
        this(text, x, y, Color.WHITE);
    }

    public Watermark(String text, double x, double y, Color textColor) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.textColor = textColor;

        width = RenderUtils.getStringWidth(text) + 5;
        height = RenderUtils.getFontHeight() + 7.5;
    }

    @SubscribeEvent
    public void onGameOverlay(RenderGameOverlayEvent e) {
        //update width in case text changes
        width = RenderUtils.getStringWidth(text) + 5;

        //check if it goes out of screen
        boolean fits = x + width <= Minecraft.getMinecraft().displayWidth / 2.0;

        //draw our very professional skeet watermark
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("icarus", "watermark.png"));
        RenderUtils.drawTexture((fits ? x : x - width), y, width, height);

        //draw text
        RenderUtils.drawString(text, (fits ? x : x - width) + 1, y + 3.5, textColor);
    }

    public void enable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void disable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
