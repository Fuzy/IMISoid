package imis.client.sync.employeessync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 24.5.13
 * Time: 21:09
 */
public class SyncAdapterListOfEmployees extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapterListOfEmployees.class.getSimpleName();
    private final Context context;

    public SyncAdapterListOfEmployees(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync()" + "account = [" + account + "], bundle = [" + bundle + "], " +
                "s = [" + s + "], contentProviderClient = [" + contentProviderClient + "], " +
                "syncResult = [" + syncResult + "]");
    }
}
