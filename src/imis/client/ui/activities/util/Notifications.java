package imis.client.ui.activities.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import imis.client.R;
import imis.client.ui.activities.EventEditorActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 11.6.13
 * Time: 14:42
 */
public class Notifications {
    private static final String TAG = Notifications.class.getSimpleName();

    public static void showMissingArriveNotification(Context context) {
        Log.d(TAG, "showMissingArriveNotification()");
        String title = context.getString(R.string.missing_arrive_event);
        showNotification(context, title);
    }

    public static void showMissingLeaveNotification(Context context) {
        Log.d(TAG, "showMissingLeaveNotification()");
        String title = context.getString(R.string.missing_leave_event);
        showNotification(context, title);
    }

    private static void showNotification(Context context, String title) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(context.getString(R.string.missing_event_hint))
                        .setAutoCancel(true);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, EventEditorActivity.class);
        //TODO pro odchod zadat udaje

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(EventEditorActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.

        //TODO id
        //TODO stack - back na daytimeline
        mNotificationManager.notify(1, mBuilder.getNotification());
    }
}
