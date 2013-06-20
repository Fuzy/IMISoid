package imis.client.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import imis.client.AppConsts;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 19.6.13
 * Time: 19:24
 */
public class NetworkConfig {
    private static final String TAG = NetworkConfig.class.getSimpleName();

    public static void setBaseURI(Context context, String baseURI) {
        Log.d(TAG, "setBaseURI()");
//        SharedPreferences settings = context.getSharedPreferences(AppConsts.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(AppConsts.KEY_BASE_URI, baseURI);
        editor.apply();
    }

    public static String getBaseURI(Context context) {
        Log.d(TAG, "getBaseURI()");
//        SharedPreferences settings = context.getSharedPreferences(AppConsts.PREFS_NAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(AppConsts.KEY_BASE_URI, NetworkConsts.BASE_URI_DEFAULT);
    }
}
