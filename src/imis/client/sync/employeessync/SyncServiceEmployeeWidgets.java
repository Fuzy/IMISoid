package imis.client.sync.employeessync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Service for actualization other employees last event used in widgets.
 */
public class SyncServiceEmployeeWidgets extends Service {
    private static final String TAG = SyncServiceEmployeeWidgets.class.getSimpleName();

    private static final Object sSyncAdapterLock = new Object();

    private static SyncAdapterEmployeeWidgets sSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapterEmployeeWidgets(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind()");
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
