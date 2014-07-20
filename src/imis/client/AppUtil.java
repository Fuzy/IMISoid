package imis.client;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;
import imis.client.ui.dialogs.AddAccountDialog;

import java.util.HashMap;
import java.util.Map;


/**
 * Utility methods for showing info toasts and getting list of event tzpe codes.
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
/*
    public static void showNetworkAccessUnavailable(Context context) {
        Toast toast = Toast.makeText(context, R.string.connection_unavailable, Toast.LENGTH_SHORT);
        toast.show();
    }*/

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

    public static void vibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
    }

    public static Map<String, String> getCodes(Context context) {
        final Map<String, String> kody_po = new HashMap<>();
        String[] arr_kody_po_values = context.getResources().getStringArray(R.array.arr_kody_po_values);
        String[] arr_kody_po_desc = context.getResources().getStringArray(R.array.arr_kody_po_desc);
        for (int i = 0; i < arr_kody_po_values.length; i++) {
            kody_po.put(arr_kody_po_values[i], arr_kody_po_desc[i]);
        }

        String[] lea_kody_po_values = context.getResources().getStringArray(R.array.lea_kody_po_values);
        String[] lea_kody_po_desc = context.getResources().getStringArray(R.array.lea_kody_po_desc);
        for (int i = 0; i < lea_kody_po_values.length; i++) {
            kody_po.put(lea_kody_po_values[i], lea_kody_po_desc[i]);
        }
        return kody_po;
    }

}
