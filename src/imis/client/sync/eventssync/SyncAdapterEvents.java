package imis.client.sync.eventssync;

import android.accounts.Account;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.R;
import imis.client.TimeUtil;
import imis.client.asynctasks.result.Result;
import imis.client.asynctasks.result.ResultList;
import imis.client.model.Event;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EventManager;
import imis.client.widget.ShortcutWidgetProvider;
import org.apache.http.HttpStatus;

import java.util.List;

public class SyncAdapterEvents extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapterEvents.class.getSimpleName();
    private final Context context;
    private final EventsSync sync;

    public SyncAdapterEvents(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        sync = new EventsSync(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync()" + "account = [" + account + "], extras = [" + extras + "], " +
                "authority = [" + authority + "], provider = [" + provider + "], syncResult = [" + syncResult + "]");
        SyncStats stats = new SyncStats();

        boolean isManual = false;
        if (extras.containsKey(Event.KEY_DATE)) {
            Log.d(TAG, "onPerformSync() manual");
            isManual = true;
        }

        Result testResult = NetworkUtilities.testWebServiceAndDBAvailability(context);
        if (testResult.getStatusCode() == null || testResult.getStatusCode().value() != HttpStatus.SC_OK) {
            Log.d(TAG, "onPerformSync() connection unavailable");
            if (isManual) sendMessageToActivity(context.getString(R.string.connection_unavailable));
            syncResult.delayUntil = (System.currentTimeMillis() + AppConsts.MS_IN_MIN) / 1000L;
            ContentResolver.requestSync(account, authority, extras);
            return;
        }


        String icp = account.name;
        // get local changes
        List<Event> dirtyEvents = EventManager.getUserDirtyEvents(context, icp);
        Log.d(TAG, "onPerformSync() dirtyEvents " + dirtyEvents.size());
        for (Event event : dirtyEvents) {

            if (event.isDeleted()) {
                //deleting
                processDeleteEvent(event, stats);
            } else if (event.hasServer_id()) {
                // updating
                EventManager.markEventAsNoError(context, event.get_id());
                processUpdateEvent(event, stats);
            } else if (event.hasServer_id() == false) {
                // creating
                EventManager.markEventAsNoError(context, event.get_id());
                processCreateEvent(event, stats);
            }

        }

        //Delete all events already synchronized
        EventManager.deleteUserNotDirtyEvents(context, icp);

        // Download all events for period and user
        long date = extras.getLong(Event.KEY_DATE, TimeUtil.todayDateInLong());
        processDownloadEvents(icp, date, stats);

        if (isManual) {
            // Send statistics to calling actvitity
            sendMessageToActivity(countStatistics(stats));
        }

        // Refresh shortcut widgets
        new ShortcutWidgetProvider().updateAllWidgets(context);

        Log.d(TAG, "onPerformSync() end");
    }

    private void sendMessageToActivity(String msg) {
        Intent i = new Intent(AppConsts.SYNC_RESULT_ACTION).putExtra(EventsSync.KEY_SYNC_RESULT, msg);
        context.sendBroadcast(i);
    }

    private String countStatistics(SyncStats stats) {
        StringBuilder st = new StringBuilder();
        st.append("Statistiky:\n");
        st.append("Smazáno: " + stats.getDeleted() + "/" + stats.getDeleteAttempt() + "\n");
        st.append("Vytvořeno: " + stats.getCreated() + "/" + stats.getCreatedAttempt() + "\n");
        st.append("Upraveno: " + stats.getUpdated() + "/" + stats.getUpdateAttempt() + "\n");
        if (stats.isDownloadErr()) {
            st.append("Staženo: Chyba" + stats.getDownErrMsg() + "\n");
        } else {
            st.append("Staženo: " + stats.getDownloaded() + "\n");
        }
        return st.toString();
    }

    private void processDeleteEvent(Event event, SyncStats stats) {
        Result deleteResult = sync.deleteEvent(event.getServer_id());
        Log.d(TAG, "processDeleteEvent()" + "event = [" + event + "]" + "deleteResult = [" + deleteResult + "]");
        stats.incDeletedAttempt();
        if (deleteResult.getStatusCode().value() == HttpStatus.SC_OK) {
            EventManager.deleteEventOnId(context, event.get_id());
            stats.incDeleted();
        }
    }

    private void processUpdateEvent(Event event, SyncStats stats) {
        Result updateResult = sync.updateEvent(event);
        Log.d(TAG, "processUpdateEvent()" + "event = [" + event + "]" + "updateResult = [" + updateResult + "]");
        stats.incUpdatedAttempt();
        if (updateResult.isClientError()) {
            EventManager.markEventAsError(context, event.get_id(), updateResult.getMsg());
        } else if (updateResult.getStatusCode().value() == HttpStatus.SC_ACCEPTED) {
            EventManager.markEventAsSynced(context, event.get_id());
            stats.incUpdated();
        }
    }

    private void processCreateEvent(Event event, SyncStats stats) {
        Result createResult = sync.createEvent(event);
        Log.d(TAG, "processCreateEvent()" + "event = [" + event + "]" + "createResult = [" + createResult + "]");
        stats.incCreatedAttempt();
        if (createResult.isClientError()) {
            EventManager.markEventAsError(context, event.get_id(), createResult.getMsg());
        } else if (createResult.getStatusCode().value() == HttpStatus.SC_CREATED) {
            EventManager.updateEventServerId(context, event.get_id(), event.getServer_id());
            EventManager.markEventAsSynced(context, event.get_id());
            stats.incCreated();
        }
    }

    private void processDownloadEvents(final String icp, final long date, SyncStats stats) {
        ResultList<Event> getResult = sync.getUserEvents(icp, date, date);
        Log.d(TAG, "processDownloadEvents()" + "icp = [" + icp + "], date = [" + date + "]" + "getResult = [" + getResult + "]");

        if (getResult.isOk()) {
            if (getResult.isEmpty()) {
                Log.d(TAG, "onPerformSync() isEmpty");
            } else if (!getResult.isEmpty()) {
                for (Event event : getResult.getArray()) {
                    if (EventManager.getEvent(context, event.getServer_id()) == null) {
                        EventManager.addEvent(context, event);
                    }
                }
                stats.setDownloaded(getResult.getArray().length);
            }
        } else {
            stats.setDownloadErr(true);
            stats.setDownErrMsg(getResult.getMsg());
        }
    }


}
