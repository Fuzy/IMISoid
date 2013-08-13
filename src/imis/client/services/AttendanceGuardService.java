package imis.client.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import imis.client.AppConsts;

import static imis.client.AppConsts.PERIOD;

/**
 * Service checks if missing arrive or leave event.
 */
public class AttendanceGuardService extends IntentService {
    private static final String TAG = AttendanceGuardService.class.getSimpleName();

    public AttendanceGuardService() {
        super(AttendanceGuardService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent() ");
        startLocationService(intent.getExtras());
    }

    private void startLocationService(Bundle bundle) {
        Log.d(TAG, "startLocationService()");
        Intent intentLoc = new Intent(this, LocationService.class);
        intentLoc.putExtras(bundle);
        startService(intentLoc);
    }

    public static void startAttendanceCheck(Context context, boolean notifyArrive, boolean notifyLeave, int periodNotification) {
        Log.d(TAG, "startAttendanceCheck()" + "notifyArrive = [" + notifyArrive + "], " +
                "notifyLeave = [" + notifyLeave + "], periodNotification = [" + periodNotification + "]");
        if (notifyArrive || notifyLeave) {
            Intent intent = new Intent(context, AttendanceGuardService.class);
            intent.putExtra(AppConsts.ARRIVE, notifyArrive);
            intent.putExtra(AppConsts.LEAVE, notifyLeave);
            intent.putExtra(AppConsts.PERIOD, periodNotification);
            planNextIntent(context, intent);
        }
    }

    public static void planNextIntent(Context context, final Intent intent) {
        Log.d(TAG, "planNextIntent()" + "intent = [" + intent + "]");
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) (context.getSystemService(Context.ALARM_SERVICE));
        int periodNotification = intent.getIntExtra(PERIOD, 60);
        long delay = AppConsts.MS_IN_MIN * periodNotification / 2;
        long next = System.currentTimeMillis() + delay;
        am.set(AlarmManager.RTC, next, pi);
    }

    public static void cancelAllIntents(Context context) {
        Log.d(TAG, "cancelAllIntents()");
        Intent intent = new Intent(context, AttendanceGuardService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

}
