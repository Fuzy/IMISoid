package imis.client.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;
import imis.client.AppUtil;
import imis.client.asynctasks.result.Result;
import imis.client.asynctasks.result.ResultData;
import imis.client.authentication.AuthenticationConsts;
import imis.client.model.Event;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EventManager;
import org.apache.http.HttpStatus;

import java.util.List;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();
    /*private static final String SYNC_MARKER_KEY = "mkz.sync.app.marker";
    private static final String AUTH_TOKEN = "qwerty";*/

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

        Result testResult = NetworkUtilities.testWebServiceAndDBAvailability();
        if (testResult.getStatusCode().value() != HttpStatus.SC_OK) {
            Log.d(TAG, "onPerformSync() connection unavailable");
            return;
        }

        String icp = accountManager.getUserData(account, AuthenticationConsts.KEY_ICP);
        Log.d(TAG, "onPerformSync() icp " + icp);
        // long lastSyncMarker = getServerSyncMarker(account);

        // get local changes
        List<Event> dirtyEvents = EventManager.getDirtyEvents(context);
        for (Event event : dirtyEvents) {

            if (event.isDeleted()) {
                // deleting
                Result deleteResult = EventsSync.deleteEvent(event.getServer_id());
                if (deleteResult.getStatusCode().value() == HttpStatus.SC_OK) {
                    EventManager.deleteEvent(context, event.get_id());
                }

            } else if (event.hasServer_id()) {
                // updating
                EventManager.markEventAsNoError(context, event.get_id());
                Result updateResult = EventsSync.updateEvent(event);
                if (updateResult.getStatusCode().value() == HttpStatus.SC_ACCEPTED) {
                    EventManager.markEventAsSynced(context, event.get_id());
                } else {
                    EventManager.markEventAsError(context, event.get_id(), updateResult.getMsg());
                }

            } else if (event.hasServer_id() == false) {
                // creating
                EventManager.markEventAsNoError(context, event.get_id());
                Result createResult = EventsSync.createEvent(event);
                if (createResult.getStatusCode().value() == HttpStatus.SC_CREATED) {

                    EventManager.updateEventServerId(context, event.get_id(), event.getServer_id());
                    EventManager.markEventAsSynced(context, event.get_id());
                } else {
                    EventManager.markEventAsError(context, event.get_id(), createResult.getMsg());
                }
            }

        }

        long date = extras.getLong(Event.KEY_DATE, AppUtil.todayInLong());
        ResultData getResult = EventsSync.getUserEvents(icp, date, date);
        if (!getResult.isErr()) {
            Log.d(TAG, "onPerformSync() GetEventsResult is OK");
            for (Event event : (Event[]) getResult.getArray()) {
                if (EventManager.updateEvent(context, event) == 0) EventManager.addEvent(context, event);
            }
            Log.d(TAG, "onPerformSync() events length: " + getResult.getArray().length);
        } else {
            Log.d(TAG, "onPerformSync() GetEventsResult isErr");
        }
        Log.d(TAG, "onPerformSync() end");

    }


}
