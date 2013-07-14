package imis.client.widget;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.model.Event;
import imis.client.persistent.EventManager;
import imis.client.ui.activities.EventEditorActivity;
import imis.client.ui.activities.util.ActivityConsts;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 27.5.13
 * Time: 20:15
 */
public class ShortcutWidgetReceiver extends BroadcastReceiver {
    private static final String TAG = ShortcutWidgetReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive() intent " + intent.getAction());

        Intent startIntent = new Intent(Intent.ACTION_INSERT);
        Event lastEvent = EventManager.getLastEvent(context);
        if (lastEvent != null && lastEvent.isDruhArrival()) {
            startIntent.putExtra(ActivityConsts.ID_ARRIVE, lastEvent.get_id());
            startIntent.putExtra(EventEditorActivity.KEY_ENABLE_ADD_LEAVE, true);
        } else {
            startIntent.putExtra(EventEditorActivity.KEY_ENABLE_ADD_ARRIVE, true);
        }
        startIntent.putExtra(AppConsts.KEY_WIDGET_IS_SOURCE, true);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startIntent.setType("vnd.android.cursor.dir/event.imisoid");
        context.startActivity(startIntent);

        // delete notification if exists
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

}
