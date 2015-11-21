package one.tribe.whatsnearme.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

import one.tribe.whatsnearme.R;
import one.tribe.whatsnearme.network.NetworkEvent;
import one.tribe.whatsnearme.ui.MainActivity;

/**
 * Manages the app notifications
 */
public class NotificationManager {
    private static final NotificationManager instance = new NotificationManager();

    private NotificationManager(){}

    public static NotificationManager getInstance() {
        return instance;
    }

    private int notificationId = 100000;

    public void notifyEvent(Context context, NetworkEvent event) {
        String title = NotificationFormatter.formatShortMessage(event);
        String text = NotificationFormatter.formatCompleteMessage(event);
        notifyEvent(context, title, text);
    }

    public void notifyEvent(Context context, String title, String text) {
        Notification.Builder mBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_whatsnearme_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true);

        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        android.app.NotificationManager mNotificationManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(getNotificationId(), mBuilder.build());
    }

    private synchronized int getNotificationId() {
        return notificationId++;
    }
}
