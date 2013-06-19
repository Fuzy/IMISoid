package imis.client.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import imis.client.R;
import imis.client.model.Event;
import imis.client.model.Record;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 4.4.13
 * Time: 17:52
 */
public class ColorUtil {
    private static final String TAG = ColorUtil.class.getSimpleName();
    private Context context;

    private static final String PREFS_EVENTS_COLOR = "ImisoidPrefsColors";
    private static final Map<String, Integer> colors = new HashMap<>();
    private static boolean isLoaded = false;

    public Map<String, Integer> getColors() {
        return colors;
    }

    public ColorUtil(Context context) {
        this.context = context;
        Log.d(TAG, "ColorUtil() isLoaded " + isLoaded);
    }

    public void setColor(String key, int value) {
        List<String> eventCodes = Arrays.asList(Event.KOD_PO_VALUES);
        List<String> recordCodes = Arrays.asList(Record.TYPE_VALUES);
        if (eventCodes.indexOf(key) == -1 && recordCodes.indexOf(key) == -1) key = Event.KOD_PO_OTHERS;
        colors.put(key, value);
        if (isLoaded) {
            saveColors();
        }
    }

    public int getColor(String key) {
        if (!isLoaded) {
            loadColors();
        }
        if (key == null) return Color.GRAY;
        List<String> eventCodes = Arrays.asList(Event.KOD_PO_VALUES);
        List<String> recordCodes = Arrays.asList(Record.TYPE_VALUES);
        if (eventCodes.indexOf(key) == -1 && recordCodes.indexOf(key) == -1) key = Event.KOD_PO_OTHERS;
        Integer value = colors.get(key);
        if (value == null) {
            return Color.GRAY;
        }
        return value;
    }

    public void loadColors() {
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

    private void loadEventColors(SharedPreferences settings) {
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

    private void loadRecordColors(SharedPreferences settings) {
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
    }

}
