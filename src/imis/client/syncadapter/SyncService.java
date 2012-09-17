package imis.client.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Sluzba provadejici synchronizaci uctu. 
 *  
 */
public class SyncService extends Service {
  private static final String TAG = "SyncService";
  
    private static final Object sSyncAdapterLock = new Object();

    private static SyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
      super.onCreate();
      Log.d(TAG, "onCreate()");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
      Log.d(TAG, "onBind()");
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
