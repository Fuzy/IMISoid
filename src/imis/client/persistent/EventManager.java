package imis.client.persistent;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import imis.client.model.Event;

import java.util.ArrayList;
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

    public static int deleteEvent(Context context, long id) {
        Log.d(TAG, "deleteEvent()" + "id = [" + id + "]");
        Uri uri = Uri.withAppendedPath(EventQuery.CONTENT_URI, String.valueOf(id));
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(uri, null, null);
    }

    public static int deleteAllEvents(Context context) {
        Log.d(TAG, "deleteAllEvents()");
        Uri uri = EventQuery.CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(uri, null, null);
    }

    public static Event getEvent(Context context, long id) {//TODO refaktor
        Log.d(TAG, "getEvent()" + "id = [" + id + "]");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EventQuery.CONTENT_URI, null,
                EventQuery.SELECTION_ID, new String[]{String.valueOf(id)}, null);
        Event event = null;
        while (cursor.moveToNext()) {
            event = Event.cursorToEvent(cursor);
        }
        cursor.close();
        return event;
    }

    public static Event getEvent(Context context, String rowid) {//TODO refaktor
        Log.d(TAG, "getEvent()" + "context = [" + context + "], rowid = [" + rowid + "]");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EventQuery.CONTENT_URI, null,
                EventQuery.SELECTION_SERVER_ID, new String[]{String.valueOf(rowid)}, null);
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

        public static final Uri CONTENT_URI = Uri.parse(Consts.SCHEME + Consts.AUTHORITY + "/"
                + MyDatabaseHelper.TABLE_EVENTS);

        public static final String SELECTION_ID = Event.COL_ID + "=?";
        public static final String SELECTION_DIRTY = Event.COL_DIRTY + "=1";
        public static final String SELECTION_UNDELETED = Event.COL_DELETED + "=0";
        public static final String SELECTION_DATUM = Event.COL_DATUM + "=?";
        public static final String SELECTION_DAY_UNDELETED = SELECTION_DATUM + " and " + SELECTION_UNDELETED;
        public static final String SELECTION_SERVER_ID = Event.COL_SERVER_ID + "=?";
        public static final String SELECTION_KODPRA = Event.COL_ICP + " LIKE ? || '%' ";//TODO kodpra->icp
        public static final String SELECTION_PERIOD = " ? <= " + Event.COL_DATUM + " and " + Event.COL_DATUM + " <= ? ";
        //TODO posledni den +1 den
        public static final String SELECTION_CHART = SELECTION_KODPRA + " and " + SELECTION_PERIOD;

    }

}
