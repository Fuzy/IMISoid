package imis.client.sync.eventssync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Service for synchronization of attendance events.
 */
public class SyncServiceEvents extends Service {
  private static final String TAG = SyncServiceEvents.class.getSimpleName();
  
    private static final Object sSyncAdapterLock = new Object();

    private static SyncAdapterEvents sSyncAdapter = null;

    @Override
    public void onCreate() {
      super.onCreate();
      Log.d(TAG, "onCreate()");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapterEvents(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
      Log.d(TAG, "onBind()");
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
