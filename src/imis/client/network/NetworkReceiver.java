package imis.client.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import imis.client.AppConsts;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 26.5.13
 * Time: 18:02
 */
public class NetworkReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO zmenit status pripojeni, registrovat v hlavni aktivite, bude slouzit ke hlidani spojeni pri bezici aplikaci
        Log.d(TAG, "onReceive()" + "intent = [" + intent.getAction() + "]");
        ConnectivityManager conn = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String networkType = sharedPref.getString(AppConsts.KEY_NETWORK_TYPE, "WIFI");
        Log.d(TAG, "onReceive() networkType " + networkType);
        if (networkType.equals(AppConsts.NETWORK_TYPE_WIFI)) {
            NetworkInfo networkInfo = conn.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            connected = networkInfo.isConnected();
            Log.d(TAG, "onReceive() NETWORK_TYPE_WIFI " + connected);
        } else  {
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();
            connected = (networkInfo != null);
            Log.d(TAG, "onReceive() NETWORK_TYPE_ANY " + connected);
        }


        /*Map<String, ?> all = sharedPref.getAll();
        Log.d(TAG, "onCreate() all " + all);*/
        /*Bundle extras = intent.getExtras();
        Set<String> set = extras.keySet();
        Log.d(TAG, "onReceive() extras " + extras.keySet());
        for (String s : set) {
            Log.d(TAG, "onReceive() s " + extras.get(s));
        }
        Log.d(TAG, "onReceive() extras size " + extras.size());*/

    }
}
