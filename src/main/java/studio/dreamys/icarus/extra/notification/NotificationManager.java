package studio.dreamys.icarus.extra.notification;

import java.util.ArrayList;

public class NotificationManager {
    public static double startY;

    public static ArrayList<Notification> notifications = new ArrayList<>();

    public static void send(Notification notification) {
        notifications.add(notification);
    }
}
