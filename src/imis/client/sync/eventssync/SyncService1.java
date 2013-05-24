package imis.client.sync.eventssync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Sluzba provadejici synchronizaci uctu. 
 *  
 */
public class SyncService1 extends Service {
  private static final String TAG = SyncService1.class.getSimpleName();
  
    private static final Object sSyncAdapterLock = new Object();

    private static SyncAdapter1 sSyncAdapter = null;

    @Override
    public void onCreate() {
      super.onCreate();
      Log.d(TAG, "onCreate()");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter1(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
      Log.d(TAG, "onBind()");
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
