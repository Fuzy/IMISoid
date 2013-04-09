package imis.client;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 9.4.13
 * Time: 16:25
 */
public class AppUtil {
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
}
