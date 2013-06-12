package imis.client;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import imis.client.authentication.AuthenticationConsts;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 9.4.13
 * Time: 16:25
 */
public class AppUtil {
    public static final DateFormat df = new SimpleDateFormat("d.M.yyyy");
    public static final DateFormat dfAbbr = new SimpleDateFormat("d.M.");//"d.M.yy"
    public static final DateFormat dfTime = new SimpleDateFormat("HH:mm");

    private static final String TAG = AppUtil.class.getSimpleName();

    public static void showAccountNotExistsError(Context context) {
        Toast toast = Toast.makeText(context, R.string.no_account_set, Toast.LENGTH_LONG);
        Intent intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
        intent.putExtra(Settings.EXTRA_AUTHORITIES, new String[]{AppConsts.AUTHORITY1, AppConsts.AUTHORITY2});
        context.startActivity(intent);
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

    public static void showWidgetAlreadyExists(Context context) {
        Toast toast = Toast.makeText(context, R.string.widget_allready_exists, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showAccountAlreadyExists(final Context context) {
       /* Toast toast = Toast.makeText(context, R.string.account_allready_exists, Toast.LENGTH_LONG);
        toast.show();*/
        Looper.prepare();
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast toast = Toast.makeText(context, R.string.account_allready_exists, Toast.LENGTH_LONG);
                toast.show();
            }
        };
        handler.sendEmptyMessage(0);//TODO je toto bezpecny kod
        Looper.loop();
    }

    public static void showError(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showInfo(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public static String getUserPassword(Context context) throws Exception {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(AuthenticationConsts.ACCOUNT_TYPE);
        return accountManager.getPassword(accounts[0]);
    }

    public static String getUserUsername(Context context) throws Exception {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(AuthenticationConsts.ACCOUNT_TYPE);
        return accounts[0].name;
    }

    public static String getUserICP(Context context) throws Exception {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(AuthenticationConsts.ACCOUNT_TYPE);
        return accountManager.getUserData(accounts[0], AuthenticationConsts.KEY_ICP);
    }

    public static Account getUserAccount(Context context) throws Exception {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(AuthenticationConsts.ACCOUNT_TYPE);
        return accounts[0];
    }

    public static long currentTimeInLong() {
        Calendar rightNow = Calendar.getInstance();
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(0);
        time.set(Calendar.HOUR_OF_DAY, rightNow.get(Calendar.HOUR_OF_DAY));
        time.set(Calendar.MINUTE, rightNow.get(Calendar.MINUTE));
        return time.getTimeInMillis();
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
        return dfAbbr.format(cal.getTime());
    }

    public static String formatTime(long time) {
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
        Log.d(TAG, "getFirstDateOfMonth() cal.getTimeInMillis() " + formatAbbrDate(cal.getTimeInMillis()));
        return cal.getTimeInMillis();
    }

    public static long getLastDateOfMonth(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Log.d(TAG, "getLastDateOfMonth() cal.getTimeInMillis() " + formatAbbrDate(cal.getTimeInMillis()));
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
        Log.d(TAG, "getStartDateOfPreviousMonth() cal.getTimeInMillis() " + formatAbbrDate(cal.getTimeInMillis()));
        return cal.getTimeInMillis();
    }

}
