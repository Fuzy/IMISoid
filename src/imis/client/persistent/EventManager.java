package imis.client.persistent;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.model.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventManager {
    private static final String TAG = "EventManager";


    public static int addEvent(Context context, Event event) {
        ContentValues values = event.asContentValues();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(EventQuery.CONTENT_URI, values);
        Log.d(TAG, "addEvent() uri: " + uri);
        return Integer.valueOf(uri.getLastPathSegment());
    }

    public static int deleteEventOnId(Context context, long id) {
        Log.d(TAG, "delete()" + "id = [" + id + "]");
        return delete(context, EventQuery.SELECTION_ID, new String[]{String.valueOf(id)});
    }

    public static int deleteEventsOlderThan(Context context, long date) {
        Log.d(TAG, "delete()" + "date = [" + date + "]");
        return delete(context, EventQuery.SELECTION_OLDER_THAN, new String[]{String.valueOf(date)});
    }

    public static int delete(Context context, String where, String[] selectionArgs) {
        Log.d(TAG, "delete()" + "where = [" + where + "], selectionArgs = [" + Arrays.toString(selectionArgs) + "]");
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(EventQuery.CONTENT_URI, where, selectionArgs);
    }

    /*public static int deleteAllEvents(Context context) {
        Log.d(TAG, "deleteAllEvents()");
        Uri uri = EventQuery.CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(uri, null, null);
    }*/

    public static Event getEvent(Context context, long id) {
        Log.d(TAG, "getEvent()" + "id = [" + id + "]");
        return getEvent(context, EventQuery.SELECTION_ID, new String[]{String.valueOf(id)}, null);
    }

    public static Event getLastEvent(Context context) {
        Log.d(TAG, "getEvent()");
        /*ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EventQuery.CONTENT_URI, null,
                null, null, EventQuery.ORDER_BY_LAST);

        Log.d(TAG, "getLastEvent() count" + cursor.getCount());
        Event event = null;
        while (cursor.moveToNext()) {
            event = Event.cursorToEvent(cursor);
            Log.d(TAG, "getLastEvent() event " + event);
        }
        return event;*/
        return getEvent(context, EventQuery.SELECTION_LAST, null, EventQuery.ORDER_BY_LAST);
    }

    public static Event getEvent(Context context, String rowid) {
        Log.d(TAG, "getEvent()" + "rowid = [" + rowid + "]");
        return getEvent(context, EventQuery.SELECTION_SERVER_ID, new String[]{String.valueOf(rowid)}, null);
    }

    public static Event getEvent(Context context, String selection, String[] selectionArgs, String orderBy) {
        Log.d(TAG, "getEvent()" + "selection = [" + selection + "], selectionArgs = [" + Arrays.toString(selectionArgs) + "]");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EventQuery.CONTENT_URI, null,
                selection, selectionArgs, orderBy);
        Event event = null;
        while (cursor.moveToNext()) {
            event = Event.cursorToEvent(cursor);
        }
        cursor.close();
        return event;
    }


    public static List<Event> getDirtyEvents(Context context) {
        Log.d(TAG, "getDirtyEvents()");
        return getEvents(context, EventQuery.SELECTION_DIRTY);
    }

    public static List<Event> getAllEvents(Context context) {
        Log.d(TAG, "getAllEvents()");
        return getEvents(context, null);
    }

    private static List<Event> getEvents(Context context, String selection) {
        Log.d(TAG, "getEvents()" + "selection = [" + selection + "]");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EventQuery.CONTENT_URI, null, selection, null, null);
        List<Event> events = new ArrayList<>();
        Event event;
        while (cursor.moveToNext()) {
            event = Event.cursorToEvent(cursor);
            events.add(event);
        }
        cursor.close();
        Log.d(TAG, "getEvents() size " + events.size());
        return events;
    }

    private static int updateEvent(Context context, ContentValues values, long id) {
        Log.d(TAG, "updateEvent()" + "values = [" + values + "], id = [" + id + "]");
        Uri uri = Uri.withAppendedPath(EventQuery.CONTENT_URI, String.valueOf(id));
        ContentResolver resolver = context.getContentResolver();
        return resolver.update(uri, values, null, null);
    }

    public static int markEventAsDeleted(Context context, long id) {
        Log.d(TAG, "markEventAsDeleted() id: " + id);
        ContentValues values = new ContentValues();
        values.put(Event.COL_DELETED, true);
        values.put(Event.COL_DIRTY, true);
        return updateEvent(context, values, id);
    }

    public static int markEventAsSynced(Context context, long id) {
        Log.d(TAG, "markEventAsSynced() id: " + id);
        ContentValues values = new ContentValues();
        values.put(Event.COL_DIRTY, false);
        return updateEvent(context, values, id);
    }

    public static int markEventAsNoError(Context context, long id) {
        Log.d(TAG, "markEventAsNoError() id: " + id);
        ContentValues values = new ContentValues();
        values.put(Event.COL_ERROR, false);
        values.put(Event.COL_MSG, "");
        return updateEvent(context, values, id);
    }

    public static int markEventAsError(Context context, long id, String msg) {
        Log.d(TAG, "markEventAsError() id: " + id);
        ContentValues values = new ContentValues();
        values.put(Event.COL_ERROR, true);
        values.put(Event.COL_MSG, msg);
        return updateEvent(context, values, id);
    }

    public static int updateEvent(Context context, Event event) {
        Log.d(TAG, "updateEvent() " + event);
        ContentValues values = event.asContentValues();
        values.put(Event.COL_DIRTY, true);
        return updateEvent(context, values, event.get_id());
    }

    public static int updateEventOnServerId(Context context, Event event) {
        Log.d(TAG, "updateEventOnServerId()" + "event = [" + event + "]");
        Event event1 = getEvent(context, event.getServer_id());
        if (event1 == null) return 0;
        ContentValues values = event.asContentValues();
        return updateEvent(context, values, event1.get_id());
    }

    public static int updateEventServerId(Context context, long id, String rowid) {
        Log.d(TAG, "updateEventServerId() id " + id + " rowid " + rowid);
        ContentValues values = new ContentValues();
        values.put(Event.COL_SERVER_ID, rowid);
        return updateEvent(context, values, id);
    }

    final public static class EventQuery {

        public static final Uri CONTENT_URI = Uri.parse(Consts.SCHEME + AppConsts.AUTHORITY1 + "/"
                + MyDatabaseHelper.TABLE_EVENTS);

        public static final String SELECTION_ID = Event.COL_ID + "=?";
        public static final String SELECTION_DIRTY = Event.COL_DIRTY + "=1";
        public static final String SELECTION_UNDELETED = Event.COL_DELETED + "=0";
        public static final String SELECTION_DATUM = Event.COL_DATUM + "=?";
        public static final String SELECTION_OLDER_THAN = Event.COL_DATUM + "<?";
        public static final String SELECTION_DAY_UNDELETED = SELECTION_DATUM + " and " + SELECTION_UNDELETED;
        public static final String SELECTION_SERVER_ID = Event.COL_SERVER_ID + "=?";
        public static final String SELECTION_ICP = Event.COL_ICP + " LIKE ? || '%' ";
        public static final String SELECTION_PERIOD = " ? <= " + Event.COL_DATUM + " and " + Event.COL_DATUM + " <= ? ";
        public static final String SELECTION_CHART = SELECTION_ICP + " and " + SELECTION_PERIOD + " and " + SELECTION_UNDELETED;
        public static final String SELECTION_LAST_ARRIVE = Event.COL_DRUH + "=P";
        public static final String SELECTION_LAST = Event.COL_DATUM;
        public static final String ORDER_BY_DATE = Event.COL_DATUM + " DESC";
        public static final String ORDER_BY_TIME = Event.COL_CAS + " DESC";
        public static final String ORDER_BY_LAST = ORDER_BY_DATE + ", " + ORDER_BY_TIME + " LIMIT 1";
    }

}
