package imis.client.ui.activities.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.R;
import imis.client.model.Event;
import imis.client.persistent.EventManager;
import imis.client.ui.activities.EventEditorActivity;
import imis.client.ui.activities.LocationSettingsActivity;

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
        Intent nextIntent = new Intent(context, EventEditorActivity.class);
        nextIntent.putExtra(EventEditorActivity.KEY_ENABLE_ADD_ARRIVE, true);
        //TODO typ posledni
        showNotification(context, title, context.getString(R.string.missing_event_hint), nextIntent);
    }

    public static void showMissingLeaveNotification(Context context) {
        Log.d(TAG, "showMissingLeaveNotification()");
        String title = context.getString(R.string.missing_leave_event);
        Intent nextIntent = new Intent(context, EventEditorActivity.class);
        nextIntent.putExtra(EventEditorActivity.KEY_ENABLE_ADD_LEAVE, true);
        Event lastEvent = EventManager.getLastEvent(context);
        if (lastEvent != null && lastEvent.isDruhArrival()) {
            nextIntent.putExtra(AppConsts.ID_ARRIVE, lastEvent.get_id());
        }
        showNotification(context, title, context.getString(R.string.missing_event_hint), nextIntent);
    }

    public static void showPositionNotSetNotification(Context context) {
        String title = context.getString(R.string.location_settings);
        Intent nextIntent = new Intent(context, LocationSettingsActivity.class);
        showNotification(context, title, context.getString(R.string.no_position_set), nextIntent);
    }

    private static void showNotification(Context context, String title, String contentText, Intent nextIntent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(contentText)
                        .setAutoCancel(true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(EventEditorActivity.class);
        stackBuilder.addNextIntent(nextIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.getNotification());
    }
}
