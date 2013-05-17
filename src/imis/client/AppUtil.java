package imis.client;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 9.4.13
 * Time: 16:25
 */
public class AppUtil {
    public static final DateFormat df = new SimpleDateFormat("d.M.yyyy");
    public static final DateFormat dfAbbr = new SimpleDateFormat("d.M.yy");
    public static final DateFormat dfUTCTime = new SimpleDateFormat("HH:mm");
    public static final DateFormat dfEmpTime = new SimpleDateFormat("d.M. HH:mm");

    static {
        dfUTCTime.setTimeZone((TimeZone.getTimeZone("UTC")));
    }

    private static final String TAG = AppUtil.class.getSimpleName();

    public static void showAccountNotExistsError(Context context) {
        Toast toast = Toast.makeText(context, R.string.no_account_set, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showPeriodInputError(Context context) {
        Toast toast = Toast.makeText(context, R.string.error_perriod_set, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showNetworkAccessUnavailable(Context context) {
        Toast toast = Toast.makeText(context, R.string.network_unavailable, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showError(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showInfo(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public static long todayInLong() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static boolean belongsNowToDate(long date) {
        Calendar now = Calendar.getInstance();
        Calendar theDate = Calendar.getInstance();
        theDate.setTimeInMillis(date);
        return now.get(Calendar.YEAR) == theDate.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == theDate.get(Calendar.DAY_OF_YEAR);
    }

    public static String formatDate(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return df.format(cal.getTime());
    }

    public static String formatAbbrDate(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return dfAbbr.format(cal.getTime());
    }

    public static String formatEmpDate(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return dfEmpTime.format(cal.getTime());
    }

    public static String formatTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(time);
        return dfUTCTime.format(cal.getTime());
    }

    public static void validateDate(String date) throws ParseException {
        dfAbbr.parse(date);
    }

    /*public static long convertToTime(String s) {
        long date;
        try {
            date = dfAbbr.parse(s).getTime();
            Log.d(TAG, "convertToTime() date " + date);
        } catch (ParseException e) {
            return todayInLong();//TODO je to dobrz napad?
        }
        Log.d(TAG, "convertToTime() date " + date);
        return date;
    }*/

    public static long getFirstDateOfMonth(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Log.d(TAG, "getFirstDateOfMonth() cal.getTimeInMillis() " + cal.getTimeInMillis());
        return cal.getTimeInMillis();
    }

    public static long getLastDateOfMonth(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Log.d(TAG, "getLastDateOfMonth() cal.getTimeInMillis() " + cal.getTimeInMillis());
        return cal.getTimeInMillis();
    }

}
