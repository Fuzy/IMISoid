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
        Log.d(TAG, "addEvent()");
        ContentValues values = event.asContentValues();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(EventQuery.CONTENT_URI, values);
        Log.d(TAG, "addEvent() uri: " + uri);
        return Integer.valueOf(uri.getLastPathSegment());
    }

    public static int deleteEvent(Context context, long id) {
        Log.d(TAG, "deleteEvent()");
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

    public static Event getEvent(Context context, long id) {
        Log.d(TAG, "getEvent()");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EventQuery.CONTENT_URI, EventQuery.PROJECTION_ALL,
                EventQuery.SELECTION_ID, new String[]{String.valueOf(id)}, null);
        Event event = null;
        while (cursor.moveToNext()) {
            event = Event.cursorToEvent(cursor);
        }
        cursor.close();
        return event;
    }


    public static List<Event> getDirtyEvents(Context context) {
        Log.d(TAG, "getDirtyEvents()");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EventQuery.CONTENT_URI, EventQuery.PROJECTION_ALL, EventQuery.SELECTION_DIRTY, null, null);
        List<Event> events = new ArrayList<>();
        Event event;
        while (cursor.moveToNext()) {
            event = Event.cursorToEvent(cursor);
            events.add(event);
        }
        cursor.close();
        return events;
    }

    public static List<Event> getAllEvents(Context context) {
        Log.d(TAG, "getAllEvents()");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EventQuery.CONTENT_URI, EventQuery.PROJECTION_ALL, null, null, null);
        List<Event> events = new ArrayList<>();
        Event event;
        while (cursor.moveToNext()) {
            event = Event.cursorToEvent(cursor);
            events.add(event);
        }
        cursor.close();
        return events;
    }

    public static int markEventAsDeleted(Context context, long id) {
        Log.d(TAG, "markEventAsDeleted() id: " + id);
        Uri uri = Uri.withAppendedPath(EventQuery.CONTENT_URI, String.valueOf(id));
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Event.COL_DELETED, true);
        values.put(Event.COL_DIRTY, true);
        return resolver.update(uri, values, null, null);
    }

    public static int markEventAsSynced(Context context, long id) {
        Log.d(TAG, "markEventAsSynced() id: " + id);
        Uri uri = Uri.withAppendedPath(EventQuery.CONTENT_URI, String.valueOf(id));
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Event.COL_DIRTY, false);
        return resolver.update(uri, values, null, null);
    }

    public static int markEventAsNoError(Context context, long id) {
        Log.d(TAG, "markEventAsNoError() id: " + id);
        Uri uri = Uri.withAppendedPath(EventQuery.CONTENT_URI, String.valueOf(id));
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Event.COL_ERROR, false);
        values.put(Event.COL_MSG, "");
        return resolver.update(uri, values, null, null);
    }

    public static int markEventAsError(Context context, long id, String msg) {
        Log.d(TAG, "markEventAsError() id: " + id);
        Uri uri = Uri.withAppendedPath(EventQuery.CONTENT_URI, String.valueOf(id));
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Event.COL_ERROR, true);
        values.put(Event.COL_MSG, msg);
        return resolver.update(uri, values, null, null);
    }

    public static int updateEvent(Context context, Event event) {
        Log.d(TAG, "updateEvent() " + event);
        Uri uri = Uri.withAppendedPath(EventQuery.CONTENT_URI, String.valueOf(event.get_id()));
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = event.asContentValues();
        values.put(Event.COL_DIRTY, true);
        return resolver.update(uri, values, null, null);
    }

    public static void updateEventServerId(Context context, long id, String rowid) {
        Log.d(TAG, "updateEventServerId() id " + id + " rowid " + rowid);
        // Uri ukolu
        Uri uri = Uri.withAppendedPath(EventQuery.CONTENT_URI, String.valueOf(id));
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Event.COL_SERVER_ID, rowid);

        // Updatuje ukol - deleted = true
        resolver.update(uri, values, null, null);
    }

    final public static class EventQuery {

        // uri zdroje dat
        public static final Uri CONTENT_URI = Uri.parse(Consts.SCHEME + Consts.AUTHORITY + "/"
                + MyDatabaseHelper.TABLE_EVENTS);

        //TODO refaktor
        // vybere vsechny sloupce
        public static final String[] PROJECTION_ALL = {Event.COL_ID, Event.COL_SERVER_ID,
                Event.COL_DIRTY, Event.COL_DELETED, Event.COL_ICP,
                Event.COL_DATUM, Event.COL_KOD_PO, Event.COL_DRUH,
                Event.COL_CAS, Event.COL_IC_OBS, Event.COL_TYP,
                Event.COL_DATUM_ZMENY, Event.COL_POZNAMKA, Event.COL_ERROR, Event.COL_MSG};

        // vyber podle id ukolu
        public static final String SELECTION_ID = Event.COL_ID + "=?";
        // vyber urcenych k sync
        public static final String SELECTION_DIRTY = Event.COL_DIRTY + "=1";
        // vyber nesmazanych
        public static final String SELECTION_UNDELETED = Event.COL_DELETED + "=0";
        // vyber podle data
        public static final String SELECTION_DATUM = Event.COL_DATUM + "=?";

        public static final String SELECTION_DAY_UNDELETED = SELECTION_DATUM + " and " + SELECTION_UNDELETED;

        public static final String SELECTION_ICP = Event.COL_ICP + "=?";

    }

}
