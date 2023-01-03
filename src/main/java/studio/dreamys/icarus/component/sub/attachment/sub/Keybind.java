package studio.dreamys.icarus.component.sub.attachment.sub;

import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import studio.dreamys.icarus.component.sub.attachment.Attachment;
import studio.dreamys.icarus.config.Config;
import studio.dreamys.icarus.event.ComponentEvent;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.Color;

public class Keybind extends Attachment {
    private String keybind = "[NONE]";
    @Getter private int key;
    private boolean capturing;

    public Keybind(int key) {
        setKey(key);
    }

    public void setKey(int key) {
        this.key = key;
        keybind = "["+ Keyboard.getKeyName(key) +"]";
    }

    @Override
    public void render(int mouseX, int mouseY) {
        x = child.x + child.group.width - 50;
        y = child.y + child.height / 8;

        RenderUtils.drawString(keybind, x, y, Color.WHITE);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hovered(mouseX, mouseY) && mouseButton == 0) {
            System.out.println("Capturing keybind");
            keybind = "[...]";
            capturing = true;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (capturing) {
            setKey(keyCode);
            capturing = false;

            //set and save
            Config.saveAttachments();
            MinecraftForge.EVENT_BUS.post(new ComponentEvent.KeybindEvent(this));
        }
    }

    @Override
    public boolean hovered(double x, double y) {
        return x > this.x && x < this.x + RenderUtils.getStringWidth(keybind) && y > this.y && y < this.y + RenderUtils.getFontHeight();
    }
}
