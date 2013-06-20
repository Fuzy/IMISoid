package imis.client.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import imis.client.AppConsts;
import imis.client.R;
import imis.client.model.Event;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 4.4.13
 * Time: 17:52
 */
public class ColorConfig {
    /*private static final String TAG = ColorConfig.class.getSimpleName();
    private static Context context;

    private static final String PREFS_EVENTS_COLOR = "ImisoidPrefsColors";
    private static final Map<String, Integer> colors = new HashMap<>();
    private static boolean isLoaded = false;

    public static synchronized Map<String, Integer> getColors() {
        return colors;
    }

    public ColorConfig(Context context) {
        this.context = context;
        Log.d(TAG, "ColorConfig() isLoaded " + isLoaded);
    }

    public void setColor(String key, int value) {
        List<String> eventCodes = Arrays.asList(Event.KOD_PO_VALUES);
        List<String> recordCodes = Arrays.asList(Record.TYPE_VALUES);
        if (eventCodes.indexOf(key) == -1 && recordCodes.indexOf(key) == -1) key = Event.KOD_PO_OTHERS;
        internalSetColor(key, value);
        if (isLoaded) {
            saveColors();
        }
    }

    private static synchronized void internalSetColor(String key, int value) {
        colors.put(key, value);
    }

    public static int getColor(String key) {
        if (!isLoaded) {
            loadColors();
        }
        if (key == null) return Color.GRAY;
        List<String> eventCodes = Arrays.asList(Event.KOD_PO_VALUES);
        List<String> recordCodes = Arrays.asList(Record.TYPE_VALUES);
        if (eventCodes.indexOf(key) == -1 && recordCodes.indexOf(key) == -1) key = Event.KOD_PO_OTHERS;
        Integer value = internalGetColor(key);
        if (value == null) {
            return Color.GRAY;
        }
        return value;
    }

    private static synchronized int internalGetColor(String key) {
        return colors.get(key);
    }

    public static void loadColors() {
        Log.d(TAG, "loadColors()");
        SharedPreferences settings = context.getSharedPreferences(PREFS_EVENTS_COLOR, Context.MODE_PRIVATE);
        loadEventColors(settings);
        loadRecordColors(settings);
        isLoaded = true;
    }

    public void saveColors() {
        Log.d(TAG, "saveColors()");
        SharedPreferences settings = context.getSharedPreferences(PREFS_EVENTS_COLOR, Context.MODE_PRIVATE);
        saveColorSharedPreferences(settings);
    }

    private static void loadEventColors(SharedPreferences settings) {
        setColor(Event.KOD_PO_ARRIVE_NORMAL, settings.getInt(Event.KOD_PO_ARRIVE_NORMAL,
                context.getResources().getColor(R.color.COLOR_PRESENT_NORMAL_DEFAULT)));
        setColor(Event.KOD_PO_ARRIVE_PRIVATE, settings.getInt(Event.KOD_PO_ARRIVE_PRIVATE,
                context.getResources().getColor(R.color.COLOR_PRESENT_PRIVATE_DEFAULT)));
        setColor(Event.KOD_PO_OTHERS, settings.getInt(Event.KOD_PO_OTHERS,
                context.getResources().getColor(R.color.COLOR_PRESENT_OTHERS_DEFAULT)));
        setColor(Event.KOD_PO_LEAVE_SERVICE, settings.getInt(Event.KOD_PO_LEAVE_SERVICE,
                context.getResources().getColor(R.color.COLOR_ABSENCE_SERVICE_DEFAULT)));
        setColor(Event.KOD_PO_LEAVE_LUNCH, settings.getInt(Event.KOD_PO_LEAVE_LUNCH,
                context.getResources().getColor(R.color.COLOR_ABSENCE_LUNCH_DEFAULT)));
        setColor(Event.KOD_PO_LEAVE_SUPPER, settings.getInt(Event.KOD_PO_LEAVE_SUPPER,
                context.getResources().getColor(R.color.COLOR_ABSENCE_SUPPER_DEFAULT)));
        setColor(Event.KOD_PO_LEAVE_MEDIC, settings.getInt(Event.KOD_PO_LEAVE_MEDIC,
                context.getResources().getColor(R.color.COLOR_ABSENCE_MEDIC_DEFAULT)));
    }

    private static void loadRecordColors(SharedPreferences settings) {
        setColor(Record.TYPE_A, settings.getInt(Record.TYPE_A,
                context.getResources().getColor(R.color.COLOR_RECORD_A)));
        setColor(Record.TYPE_I, settings.getInt(Record.TYPE_I,
                context.getResources().getColor(R.color.COLOR_RECORD_I)));
        setColor(Record.TYPE_J, settings.getInt(Record.TYPE_J,
                context.getResources().getColor(R.color.COLOR_RECORD_J)));
        setColor(Record.TYPE_K, settings.getInt(Record.TYPE_K,
                context.getResources().getColor(R.color.COLOR_RECORD_K)));
        setColor(Record.TYPE_O, settings.getInt(Record.TYPE_O,
                context.getResources().getColor(R.color.COLOR_RECORD_O)));
        setColor(Record.TYPE_R, settings.getInt(Record.TYPE_R,
                context.getResources().getColor(R.color.COLOR_RECORD_R)));
        setColor(Record.TYPE_S, settings.getInt(Record.TYPE_S,
                context.getResources().getColor(R.color.COLOR_RECORD_S)));
        setColor(Record.TYPE_V, settings.getInt(Record.TYPE_V,
                context.getResources().getColor(R.color.COLOR_RECORD_V)));
        setColor(Record.TYPE_W, settings.getInt(Record.TYPE_W,
                context.getResources().getColor(R.color.COLOR_RECORD_W)));
    }

    private void saveColorSharedPreferences(SharedPreferences settings) {
        Log.d(TAG, "saveColorSharedPreferences()");
        SharedPreferences.Editor editor = settings.edit();
        Map<String, Integer> colorsEvents = getColors();
        for (Map.Entry<String, Integer> entry : colorsEvents.entrySet()) {
            editor.putInt(entry.getKey(), entry.getValue().intValue());
        }
        editor.commit();
    }*/

    public static int getDefault(Context context, String key) {
        //TODO dodelat
        if (key.equals(Event.KOD_PO_ARRIVE_NORMAL)) {
            return context.getResources().getColor(R.color.COLOR_PRESENT_NORMAL_DEFAULT);
        } else {
            return Color.GRAY;
        }
    }

    public static int getColor(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(AppConsts.PREFS_EVENTS_COLOR, Context.MODE_PRIVATE);
        int color = settings.getInt(key, ColorConfig.getDefault(context, key));
        return color;
    }

    public static void setColor(Context context, String key, int color) {
        SharedPreferences settings = context.getSharedPreferences(AppConsts.PREFS_EVENTS_COLOR, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, color);
        editor.apply();
    }

    public static Map<String, Integer> getColors(Context context) {
        SharedPreferences settings = context.getSharedPreferences(AppConsts.PREFS_EVENTS_COLOR, Context.MODE_PRIVATE);
        return (Map<String, Integer>) settings.getAll();
    }

}
