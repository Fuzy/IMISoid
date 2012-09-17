package imis.client.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
  private static final String TAG = "SyncAdapter";
  private static final String SYNC_MARKER_KEY = "mkz.sync.app.marker";
  private static final String AUTH_TOKEN = "qwerty";
  private String text = "Synchronizace nebyla provedena";

  private final AccountManager accountManager;

  private final Context context;

  public SyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
    Log.d(TAG, "SyncAdapter()");
    this.context = context;
    accountManager = AccountManager.get(context);
  }

  @Override
  public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {
    Log.d(TAG, "onPerformSync()");

   
  }

  /**
   * This helper function fetches the last known high-water-mark we received
   * from the server - or 0 if we've never synced.
   * 
   * @param account
   *          the account we're syncing
   * @return the change high-water-mark
   */
  private long getServerSyncMarker(Account account) {
    Log.d(TAG, "getServerSyncMarker()");
    String markerString = accountManager.getUserData(account, SYNC_MARKER_KEY);
    if (!TextUtils.isEmpty(markerString)) {
      return Long.parseLong(markerString);
    }
    return 0;
  }

  /**
   * Save off the high-water-mark we receive back from the server.
   * 
   * @param account
   *          The account we're syncing
   * @param marker
   *          The high-water-mark we want to save.
   */
  private void setServerSyncMarker(Account account, long marker) {
    Log.d(TAG, "setServerSyncMarker()");
    accountManager.setUserData(account, SYNC_MARKER_KEY, Long.toString(marker));
  }

}
