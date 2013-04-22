package imis.client;

import android.content.Context;
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

    static {
        dfUTCTime.setTimeZone((TimeZone.getTimeZone("UTC")));
    }

    public static void showAccountNotExistsError(Context context) {
        Toast toast = Toast.makeText(context, R.string.no_account_set, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showNetworkAccessUnavailable(Context context) {
        Toast toast = Toast.makeText(context, R.string.network_unavailable, Toast.LENGTH_LONG);
        toast.show();
    }

    public static long getTodayInLong() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis(); //TODO test
    }

    public static boolean belongsNowToDate(long date) {
        Calendar now = Calendar.getInstance();
        Calendar theDate = Calendar.getInstance();
        theDate.setTimeInMillis(date);
        return now.get(Calendar.YEAR) == theDate.get(Calendar.YEAR)
                && now.get(Calendar.DAY_OF_YEAR) == theDate.get(Calendar.DAY_OF_YEAR);
    }

    /*public static String formatDate(java.sql.Date date) {
        String dateS = null;
        if (date != null) {
            dateS = df.format(date);
        }
        return dateS;
    }*/

    public static long todayInLong() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
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

    public static String formatTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(time);
        return dfUTCTime.format(cal.getTime());
    }

    public static long convertToTime(String s) {
        long date;
        try {
            date = dfAbbr.parse(s).getTime();
        } catch (ParseException e) {
            return todayInLong();
        }
        return date;
    }

    /*public static String KOD_PRA = "JSA";
    public static String ICP = "1429";*/

}
