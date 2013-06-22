package imis.client.sync.eventssync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;
import imis.client.TimeUtil;
import imis.client.asynctasks.result.Result;
import imis.client.asynctasks.result.ResultList;
import imis.client.authentication.AuthenticationConsts;
import imis.client.model.Event;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EventManager;
import imis.client.widget.ShortcutWidgetProvider;
import org.apache.http.HttpStatus;

import java.util.List;

public class SyncAdapterEvents extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapterEvents.class.getSimpleName();
    private final AccountManager accountManager;
    private final Context context;
    private final EventsSync sync;


    public SyncAdapterEvents(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        accountManager = AccountManager.get(context);
        sync = new EventsSync(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync()" + "account = [" + account + "], extras = [" + extras + "], " +
                "authority = [" + authority + "], provider = [" + provider + "], syncResult = [" + syncResult + "]");
        //TODO bundle rozlisit volani z aktivity, poslat zpravu po skonceni
        Result testResult = NetworkUtilities.testWebServiceAndDBAvailability(context);
        if (testResult.getStatusCode() == null || testResult.getStatusCode().value() != HttpStatus.SC_OK) {
            Log.d(TAG, "onPerformSync() connection unavailable");
            //AppUtil.showNetworkAccessUnavailable(context);//TODO spojeni vs nedostupny server, poslat zpravu po skonceni

            return;
        }

        // get local changes
        List<Event> dirtyEvents = EventManager.getDirtyEvents(context);
        for (Event event : dirtyEvents) {

            if (event.isDeleted()) {
                //deleting
                processDeleteEvent(event);
            } else if (event.hasServer_id()) {
                // updating
                EventManager.markEventAsNoError(context, event.get_id());
                processUpdateEvent(event);
            } else if (event.hasServer_id() == false) {
                // creating
                EventManager.markEventAsNoError(context, event.get_id());
                processCreateEvent(event);
            }

        }

        // download all events for period and user
        String icp = accountManager.getUserData(account, AuthenticationConsts.KEY_ICP);
        long date = extras.getLong(Event.KEY_DATE, TimeUtil.todayInLong());
        processDownloadEvents(icp, date);
        Log.d(TAG, "onPerformSync() end");

        // Refresh shortcut widgets
        new ShortcutWidgetProvider().updateAllWidgets(context);

    }

    private void processDeleteEvent(Event event) {
        Result deleteResult = sync.deleteEvent(event.getServer_id());
        Log.d(TAG, "processDeleteEvent()" + "event = [" + event + "]" + "deleteResult = [" + deleteResult + "]");
        if (deleteResult.isUnknownErr()) {
            showUnknownError(deleteResult);
        } else if (deleteResult.isServerError()) {
            showServerError(deleteResult);
        } else if (deleteResult.getStatusCode().value() == HttpStatus.SC_OK) {
            EventManager.deleteEventOnId(context, event.get_id());
        }
    }

    private void processUpdateEvent(Event event) {
        Result updateResult = sync.updateEvent(event);
        Log.d(TAG, "processUpdateEvent()" + "event = [" + event + "]" + "updateResult = [" + updateResult + "]");

        if (updateResult.isUnknownErr()) {
            showUnknownError(updateResult);
        } else if (updateResult.isServerError()) {
            showServerError(updateResult);
        } else if (updateResult.isClientError()) {
            EventManager.markEventAsError(context, event.get_id(), updateResult.getMsg());
        } else if (updateResult.getStatusCode().value() == HttpStatus.SC_ACCEPTED) {
            EventManager.markEventAsSynced(context, event.get_id());
        }
    }

    private void processCreateEvent(Event event) {
        Result createResult = sync.createEvent(event);
        Log.d(TAG, "processCreateEvent()" + "event = [" + event + "]" + "createResult = [" + createResult + "]");
        if (createResult.isUnknownErr()) {
            showUnknownError(createResult);
        } else if (createResult.isServerError()) {
            showServerError(createResult);
        } else if (createResult.isClientError()) {
            EventManager.markEventAsError(context, event.get_id(), createResult.getMsg());
        } else if (createResult.getStatusCode().value() == HttpStatus.SC_CREATED) {
            EventManager.updateEventServerId(context, event.get_id(), event.getServer_id());
            EventManager.markEventAsSynced(context, event.get_id());
        }
    }

    private void processDownloadEvents(final String icp, final long date) {
        ResultList getResult = sync.getUserEvents(icp, date, date);
        Log.d(TAG, "processDownloadEvents()" + "icp = [" + icp + "], date = [" + date + "]" + "getResult = [" + getResult + "]");

        if (getResult.isOk()) {
//TODO            AppUtil.showInfoMsgOutsideActivity(context, context.getString(R.string.act_ok));
        } else if (getResult.isUnknownErr()) {
            showUnknownError(getResult);
        } else if (getResult.isEmpty()) {
            Log.d(TAG, "onPerformSync() isEmpty");
        } else if (!getResult.isEmpty()) {
            for (Event event : (Event[]) getResult.getArray()) {
                if (EventManager.updateEventOnServerId(context, event) == 0) EventManager.addEvent(context, event);
            }
            Log.d(TAG, "onPerformSync() events length: " + getResult.getArray().length);
        }
    }

    private void showUnknownError(Result result) {
        Log.d(TAG, "showUnknownError() result " + result);
        //TODO
    }

    private void showServerError(Result result) {
        Log.d(TAG, "showServerError() result " + result);
        //TODO
    }

    private void showClientError(Result result) {
        Log.d(TAG, "showClientError() result " + result);
        //TODO
    }


}
