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
import imis.client.authentication.AuthenticationConsts;
import imis.client.http.MyResponse;
import imis.client.model.Event;
import imis.client.network.EventsSync;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EventManager;
import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

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

        MyResponse response;
        int httpCode = -1;
        httpCode = NetworkUtilities.testWebServiceAndDBAvailability();
        if (httpCode != HttpStatus.SC_OK) {
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
                httpCode = EventsSync.deleteEvent(event.getServer_id());
                if (httpCode == HttpStatus.SC_OK) EventManager.deleteEvent(context, event.get_id());
            } else if (event.hasServer_id()) {
                // updating
                EventManager.markEventAsNoError(context, event.get_id());
                response = EventsSync.updateEvent(event);
                if (response.getCode() == HttpStatus.SC_ACCEPTED) {
                    EventManager.markEventAsSynced(context, event.get_id());
                } else {
                    EventManager.markEventAsError(context, event.get_id(), response.getMsg());
                }
            } else if (event.hasServer_id() == false) {
                // creating
                EventManager.markEventAsNoError(context, event.get_id());
                response = EventsSync.createEvent(event);
                if (response.getCode() == HttpStatus.SC_CREATED) {

                    EventManager.updateEventServerId(context, event.get_id(), event.getServer_id());
                    EventManager.markEventAsSynced(context, event.get_id());
                } else {
                    EventManager.markEventAsError(context, event.get_id(), response.getMsg());
                }
            }
        }

        //udela misto pro platne udaje
        //EventManager.deleteAllEvents(context);//TODO asi spatne

        // download all user events from server
        List<Event> events = new ArrayList<>();
        //"/0000001?from=29.7.2004&to=29.7.2004"
        long date = extras.getLong(Event.KEY_DATE, AppUtil.todayInLong());
        EventsSync.getUserEvents(events, icp, date, date);//TODO jak obdobi spravne?
        for (Event event : events) {
            if (EventManager.updateEvent(context, event) == 0) EventManager.addEvent(context, event);
        }
        Log.d(TAG, "onPerformSync() events length: " + events.size());
        // Log.d(TAG, "onPerformSync() dirtyEvents: " + dirtyEvents);
        Log.d(TAG, "onPerformSync() end");

    }


}
