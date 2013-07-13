package imis.client;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 22.6.13
 * Time: 14:27
 */
public class TimeUtil {
    private static final String TAG = TimeUtil.class.getSimpleName();

    private static final DateFormat df = new SimpleDateFormat("d.M.yyyy");
    private static final DateFormat dfAbbr = new SimpleDateFormat("d.M.");//"d.M.yy"
    private static final DateFormat dfTime;

    static {
        dfTime = new SimpleDateFormat("HH:mm");
        dfTime.setTimeZone(TimeZone.getTimeZone("Etc/GMT"));
    }

    public static long currentDayTimeInLong() {
        Calendar now = Calendar.getInstance();
        long timeInMs = now.get(Calendar.HOUR_OF_DAY) * AppConsts.MS_IN_HOUR + now.get(Calendar.MINUTE) * AppConsts.MS_IN_MIN;
        return timeInMs;
    }

    public static long todayDateInLong() {//TODO BUG!!! casovy posun, datum jako long
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

    public static String formatTime(Long time) {
        if (time == null) return "";
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return dfTime.format(cal.getTime());
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
