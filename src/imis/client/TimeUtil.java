package imis.client;

import android.util.Log;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Utility method used for work with date and time.
 */
public class TimeUtil {
    private static final String TAG = TimeUtil.class.getSimpleName();

    private static final DecimalFormat decf = new DecimalFormat("00");
    private static final DateFormat df = new SimpleDateFormat("d.M.yyyy");
    private static final DateFormat dfAbbr = new SimpleDateFormat("d.M.");

    public static long currentDayTimeInLong() {
        Calendar now = Calendar.getInstance();
        long timeInMs = now.get(Calendar.HOUR_OF_DAY) * AppConsts.MS_IN_HOUR +
                now.get(Calendar.MINUTE) * AppConsts.MS_IN_MIN;
        return timeInMs;
    }

    public static long todayDateInLong() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Log.d(TAG, "todayDateInLong() cal " + cal);
        return cal.getTimeInMillis();
    }

    public static boolean belongsNowToDate(long date) {
        Calendar now = Calendar.getInstance();
        Calendar theDate = Calendar.getInstance();
        theDate.setTimeInMillis(date);
        return now.get(Calendar.YEAR) == theDate.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == theDate.get(Calendar.DAY_OF_YEAR);
    }

    public static String formatDate(Long date) {
        if (date == null) return "";
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return df.format(cal.getTime());
    }

    public static String formatAbbrDate(Long date) {
        if (date == null) return "";
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return dfAbbr.format(cal.getTime());
    }

    public static String formatEmpDate(Long date) {
        if (date == null) return "";
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return dfAbbr.format(cal.getTime());
    }

    public static String formatTimeInNonLimitHour(Long time) {
        if (time == null) return "";
        long hours = time / AppConsts.MS_IN_HOUR;
        long mins = (time - hours * AppConsts.MS_IN_HOUR) / AppConsts.MS_IN_MIN;
        String res = "";
        if (time < 0) res = res.concat("-");
        res = res.concat(decf.format(Math.abs(hours)) + ":" + decf.format(Math.abs(mins)));
        return res;
    }

    public static void validateDate(String date) throws ParseException {
        dfAbbr.parse(date);
    }

    public static long getFirstDateOfMonth(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }

    public static long getLastDateOfMonth(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }

    public static long getStartDateOfPreviousMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }
}
