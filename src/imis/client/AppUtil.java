package imis.client;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import imis.client.ui.dialogs.AddAccountDialog;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 9.4.13
 * Time: 16:25
 */
public class AppUtil {
    private static final String TAG = AppUtil.class.getSimpleName();

    public static void showAccountNotExistsError(FragmentManager fragmentManager) {
        new AddAccountDialog().show(fragmentManager, "AddAccountDialog");
    }

    public static void showNotUserSelectedError(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showPeriodInputError(Context context) {
        Toast toast = Toast.makeText(context, R.string.error_perriod_set, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showNetworkAccessUnavailable(Context context) {
        Toast toast = Toast.makeText(context, R.string.connection_unavailable, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showWidgetAlreadyExists(Context context) {
        Toast toast = Toast.makeText(context, R.string.widget_allready_exists, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showError(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showInfo(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.show();
    }

}
