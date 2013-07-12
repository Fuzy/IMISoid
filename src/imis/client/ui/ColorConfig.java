package imis.client.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import imis.client.R;
import imis.client.model.Event;
import imis.client.model.Record;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 4.4.13
 * Time: 17:52
 */
public class ColorConfig {

    private static int getDefault(Context context, String key) {
        if (key == null) return Color.GRAY;
        if (key.equals(Event.KOD_PO_ARRIVE_NORMAL)) {
            return context.getResources().getColor(R.color.COLOR_PRESENT_NORMAL_DEFAULT);
        } else if (key.equals(Event.KOD_PO_ARRIVE_PRIVATE)) {
            return context.getResources().getColor(R.color.COLOR_PRESENT_PRIVATE_DEFAULT);
        } else if (key.equals(Event.KOD_PO_OTHERS)) {
            return context.getResources().getColor(R.color.COLOR_PRESENT_OTHERS_DEFAULT);
        } else if (key.equals(Event.KOD_PO_LEAVE_LUNCH)) {
            return context.getResources().getColor(R.color.COLOR_ABSENCE_LUNCH_DEFAULT);
        } else if (key.equals(Event.KOD_PO_LEAVE_SERVICE)) {
            return context.getResources().getColor(R.color.COLOR_ABSENCE_SERVICE_DEFAULT);
        } else if (key.equals(Event.KOD_PO_LEAVE_SUPPER)) {
            return context.getResources().getColor(R.color.COLOR_ABSENCE_SUPPER_DEFAULT);
        } else if (key.equals(Event.KOD_PO_LEAVE_MEDIC)) {
            return context.getResources().getColor(R.color.COLOR_ABSENCE_MEDIC_DEFAULT);
        } else if (key.equals(Record.TYPE_A)) {
            return context.getResources().getColor(R.color.COLOR_RECORD_A);
        } else if (key.equals(Record.TYPE_I)) {
            return context.getResources().getColor(R.color.COLOR_RECORD_I);
        } else if (key.equals(Record.TYPE_J)) {
            return context.getResources().getColor(R.color.COLOR_RECORD_J);
        } else if (key.equals(Record.TYPE_K)) {
            return context.getResources().getColor(R.color.COLOR_RECORD_K);
        } else if (key.equals(Record.TYPE_O)) {
            return context.getResources().getColor(R.color.COLOR_RECORD_O);
        } else if (key.equals(Record.TYPE_R)) {
            return context.getResources().getColor(R.color.COLOR_RECORD_R);
        } else if (key.equals(Record.TYPE_S)) {
            return context.getResources().getColor(R.color.COLOR_RECORD_S);
        } else if (key.equals(Record.TYPE_V)) {
            return context.getResources().getColor(R.color.COLOR_RECORD_V);
        } else if (key.equals(Record.TYPE_W)) {
            return context.getResources().getColor(R.color.COLOR_RECORD_W);
        } else {
            return Color.GRAY;
        }
    }

    public static int getColor(Context context, String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        //context.getSharedPreferences(AppConsts.PREFS_EVENTS_COLOR, Context.MODE_PRIVATE);
        int color = settings.getInt(key, ColorConfig.getDefault(context, key));
        return color;
    }

    public static void setColor(Context context, String key, int color) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        //context.getSharedPreferences(AppConsts.PREFS_EVENTS_COLOR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, color);
        editor.apply();
    }

}
