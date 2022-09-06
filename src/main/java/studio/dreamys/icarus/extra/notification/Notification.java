package studio.dreamys.icarus.extra.notification;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import studio.dreamys.icarus.util.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

@Getter
@Setter
public class Notification {
    private String title, message;
    private int tick, ticksToStay;
    private double x, y, width, height, speed, maskWidth;
    private Color backgroundColor, titleColor, textColor, maskColor;
    private ArrayList<String> lines;

    public Notification(String title, String message) {
        this(title, message, 20, Color.BLACK, Color.WHITE, Color.WHITE, Color.PINK);
    }

    public Notification(String title, String message, int ticksToStay) {
        this(title, message, ticksToStay, Color.BLACK, Color.WHITE, Color.WHITE, Color.PINK);
    }

    public Notification(String title, String message, int ticksToStay, Color backgroundColor, Color titleColor, Color textColor, Color maskColor) {
        this.title = title;
        this.message = message;
        this.ticksToStay = ticksToStay;
        this.backgroundColor = backgroundColor;
        this.titleColor = titleColor;
        this.textColor = textColor;
        this.maskColor = maskColor;

        //setting defaults
        tick = -20;
        x = Minecraft.getMinecraft().displayWidth / 2.0;
        width = 150;
        maskWidth = x + width;

        //basic end of word separation
        if (RenderUtils.getStringWidth(message) + 20 > 150) {
            lines = new ArrayList<>();
            String[] words = message.split(" ");
            String line = "";
            for (String word : words) {
                if (RenderUtils.getStringWidth(line + word) + 20 > 150) {
                    lines.add(line);
                    line = word + " ";
                } else {
                    line += word + " ";
                }
            }
            lines.add(line);
            height = lines.size() * 10 + 20;
        } else {
            height = 30;
        }
    }

    @SubscribeEvent
    public void onGameOverlay(RenderGameOverlayEvent e) {
        y = 0;

        //notification dynamic stacking
        for (Notification notification : NotificationManager.notifications) {
            if (NotificationManager.notifications.indexOf(notification) < NotificationManager.notifications.indexOf(this)) {
                y += notification.height;
            }
        }

        //draw background
        RenderUtils.drawRect(x, y, x + width, y + height, backgroundColor);

        //draw title
        RenderUtils.drawTitle(title, x + 10, y + 5, titleColor);

        //draw message(s)
        if (lines != null) {
            for (String line : lines) {
                RenderUtils.drawString(line, x + 10, y + 20 + lines.indexOf(line) * 10, textColor);
            }
        } else {
            RenderUtils.drawString(message, x + 10, y + 20, textColor);
        }

        //draw mask
        RenderUtils.drawRect(x, y, maskWidth, y + height, maskColor);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            //if we cannot see it, kill it and liberate y height
            if (x > Minecraft.getMinecraft().displayWidth / 2.0) {
                MinecraftForge.EVENT_BUS.unregister(this);
                NotificationManager.notifications.remove(this);
            }

            //pause for x amount of ticks
            if (tick == 0 && ticksToStay > 0) {
                ticksToStay--;
                return;
            }

            //parabola easing (thank you math classes)
            speed = (((tick / width) / 2) * Math.pow(tick, 2));
            x += speed;
            maskWidth = x + 10 - speed * 4;
            tick++;
        }
    }
}
