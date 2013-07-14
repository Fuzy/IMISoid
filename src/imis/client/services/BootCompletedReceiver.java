package imis.client.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import imis.client.R;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 14.7.13
 * Time: 16:48
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompletedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            final String KEY_PREF_NOTIFI_ARRIVE = context.getResources().getString(R.string.prefNotificationArrive);
            final String KEY_PREF_NOTIFI_LEAVE = context.getResources().getString(R.string.prefNotificationLeave);
            final String KEY_PREF_NOTIFI_FREQ = context.getResources().getString(R.string.prefNotificationFrequency);
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            boolean notifyArrive = settings.getBoolean(KEY_PREF_NOTIFI_ARRIVE, false);
            boolean notifyLeave = settings.getBoolean(KEY_PREF_NOTIFI_LEAVE, false);
            String value = settings.getString(KEY_PREF_NOTIFI_FREQ, "60");
            int periodNotification = Integer.valueOf(value);
            AttendanceGuardService.startAttendanceCheck(context, notifyArrive, notifyLeave, periodNotification);
        }
    }
}
