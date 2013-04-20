package imis.client.ui;

import android.graphics.Color;

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
        colors.put(key, value);
    }

    public static int getColor(String key) {
        Integer color = colors.get(key);
        return color == null ? Color.GRAY : color;

    }


}
