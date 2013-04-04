package imis.client.ui;

import imis.client.model.Event;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 4.4.13
 * Time: 17:52
 */
public class ColorUtil {
    public static String KEY_COLOR_PRESENT_NORMAL = "color_present_normal";
    public static String KEY_COLOR_PRESENT_PRIVATE = "color_present_private";
    public static String KEY_COLOR_PRESENT_OTHERS = "color_present_others";
    public static String KEY_COLOR_ABSENCE_SERVICE = "color_absence_service";
    public static String KEY_COLOR_ABSENCE_MEAL = "color_absence_meal";

    private static int color_present_normal = 0;
    private static int color_present_private = 0;
    private static int color_present_others = 0;
    private static int color_absence_service = 0;
    private static int color_absence_meal = 0;

    public static int getColor_present_normal() {
        return color_present_normal;
    }

    public static void setColor_present_normal(int color_present_normal) {
        ColorUtil.color_present_normal = color_present_normal;
    }

    public static int getColor_present_private() {
        return color_present_private;
    }

    public static void setColor_present_private(int color_present_private) {
        ColorUtil.color_present_private = color_present_private;
    }

    public static int getColor_present_others() {
        return color_present_others;
    }

    public static void setColor_present_others(int color_present_others) {
        ColorUtil.color_present_others = color_present_others;
    }

    public static int getColor_absence_service() {
        return color_absence_service;
    }

    public static void setColor_absence_service(int color_absence_service) {
        ColorUtil.color_absence_service = color_absence_service;
    }

    public static int getColor_absence_meal() {
        return color_absence_meal;
    }

    public static void setColor_absence_meal(int color_absence_meal) {
        ColorUtil.color_absence_meal = color_absence_meal;
    }

    public static int getColorForType(String type) {
        if (type.equals(Event.KOD_PO_ARRIVE_NORMAL)) {
              return color_present_normal;
        }
        else if (type.equals(Event.KOD_PO_ARRIVE_PRIVATE)) {
            return color_present_private;
        }
        else if (type.equals(Event.KOD_PO_LEAVE_LUNCH) || type.equals(Event.KOD_PO_LEAVE_SUPPER)) {
            return color_absence_meal;
        }
            return 0;
    }

    /*public static final int COLOR_PRESENT_NORMAL_DEFAULT = 15078679;
    public static final int COLOR_PRESENT_PRIVATE_DEFAULT = 12634111;
    public static final int COLOR_PRESENT_OTHERS_DEFAULT = 14015950;
    public static final int COLOR_ABSENCE_SERVICE_DEFAULT = 12853515;
    public static final int COLOR_ABSENCE_MEAL_DEFAULT = 5929984;*/


}
