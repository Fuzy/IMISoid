package imis.client.ui;

import android.graphics.Color;
import android.util.Log;
import imis.client.model.Event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 4.4.13
 * Time: 17:52
 */
public class ColorUtil {
    private static final String TAG = "ColorUtil";

    private static Map<String, Integer> colors = new HashMap<>();


    public static Map<String, Integer> getColors() {
        return colors;
    }

    public static void setColor(String key, int value) {
        if (Arrays.asList(Event.KOD_PO_VALUES).indexOf(key) == -1) key = Event.KOD_PO_OTHERS;
        Log.d(TAG, "getColor() key " + key);
        colors.put(key, value);
    }

    public static int getColor(String key) {
        if (Arrays.asList(Event.KOD_PO_VALUES).indexOf(key) == -1) key = Event.KOD_PO_OTHERS;
        Log.d(TAG, "getColor() key " + key);
        Integer color = colors.get(key);
        return color == null ? Color.GRAY : color;

    }


}
