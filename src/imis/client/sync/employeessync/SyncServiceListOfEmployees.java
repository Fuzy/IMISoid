package imis.client.sync.employeessync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 24.5.13
 * Time: 21:07
 */
public class SyncServiceListOfEmployees extends Service {
    private static final String TAG = SyncServiceListOfEmployees.class.getSimpleName();

    private static final Object sSyncAdapterLock = new Object();

    private static SyncAdapterListOfEmployees sSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapterListOfEmployees(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
