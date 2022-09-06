package studio.dreamys.icarus.extra.notification;

import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;

public class NotificationManager {
    public static ArrayList<Notification> notifications = new ArrayList<>();

    public static void send(Notification notification) {
        notifications.add(notification);
        MinecraftForge.EVENT_BUS.register(notification);
    }
}
