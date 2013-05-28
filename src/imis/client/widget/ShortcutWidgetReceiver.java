package imis.client.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.model.Event;
import imis.client.persistent.EventManager;
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
        int widgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
        Log.d(TAG, "onReceive() widgetID " + widgetID);

        Intent startIntent = new Intent(Intent.ACTION_INSERT);
        //TODO intent + show dialog
        Event lastEvent = EventManager.getLastEvent(context);
        if (lastEvent.isDruhArrival()) {
            startIntent.putExtra(ActivityConsts.ID_ARRIVE, lastEvent.get_id());
        }
        startIntent.putExtra(AppConsts.KEY_WIDGET_ID, widgetID);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startIntent.setType("vnd.android.cursor.dir/event.imisoid");
        context.startActivity(startIntent);
        /*Event lastEvent = EventManager.getLastEvent(context);
        Event newEvent;
        if (lastEvent.isDruhArrival()) {
            newEvent = createEvent(context, Event.DRUH_LEAVE);
        } else {
            newEvent = createEvent(context, Event.DRUH_ARRIVAL);

        }

        int i = EventManager.addEvent(context, newEvent);
        Log.d(TAG, "onReceive() i " + i);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ShortcutWidgetProvider.updateAppWidget(context, appWidgetManager, widgetID);*/
    }

    /*private void showDeleteDialog() {
        DialogFragment deleteEventDialog = new AddEventDialog();
        deleteEventDialog.show(getSupportFragmentManager(), "AddEventDialog");
    }*/

   /* private Event createEvent(Context context, String druh) {
        Log.d(TAG, "createEvent()");
        Event event = new Event();
        event.setDirty(true);
        event.setDatum_zmeny(AppUtil.todayInLong());
        event.setTyp(Event.TYPE_ORIG);
        event.setKod_po("00");
        event.setCas(AppUtil.currentTimeInLong());
        event.setDruh(druh);
        event.setDatum(AppUtil.todayInLong());
        try {
            String kod = AppUtil.getUserUsername(context);
            String icp = AppUtil.getUserICP(context);
            event.setIcp(icp);
            event.setIc_obs(kod); //TODO testovaci chyba: "12345"
        } catch (Exception e) {
            //e.printStackTrace(); //TODO err msg
            //AppUtil.showAccountNotExistsError(this);

        }
        return event;
    }*/
}
