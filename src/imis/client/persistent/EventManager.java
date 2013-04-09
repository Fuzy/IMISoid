package imis.client.persistent;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import imis.client.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private static final String TAG = "EventManager";

    //TODO serverEvents jako Event
    private static long updateEvents(Context context, List<JsonObject> serverEvents,
                                     long lastSyncMarker) {
        Log.d(TAG, "updateEvents()");
        long currentSyncMarker = lastSyncMarker;

        for (JsonObject eventJson : serverEvents) {

            JsonElement syncEl = eventJson.get(Event.JSON_SYNC);
            long sync = (syncEl == null) ? -1 : syncEl.getAsLong();

            if (sync > currentSyncMarker) {
                // Pamatuje si cas nejnovejsi zmeny
                currentSyncMarker = sync;
            }

            Event event = Event.jsonToEvent(eventJson);
            event.setDirty(false);
            addEvent(context, event);

        }

        return currentSyncMarker;
    }

    /**
     * @param context
     * @param event
     * @return
     */
    public static int addEvent(Context context, Event event) {
        Log.d(TAG, "addEvent()");
        ContentValues values = event.getAsContentValues();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(DataQuery.CONTENT_URI, values);
        Log.d(TAG, "addEvent() uri: " + uri);
        return Integer.valueOf(uri.getLastPathSegment());
    }

    public static int deleteEvent(Context context, long id) {
        Log.d(TAG, "deleteEvent()");
        Uri uri = Uri.withAppendedPath(DataQuery.CONTENT_URI, String.valueOf(id));
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(uri, null, null);
    }

    public static int deleteAllEvents(Context context) {
        Log.d(TAG, "deleteAllEvents()");
        Uri uri = DataQuery.CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(uri, null, null);
    }

    public static Event getEvent(Context context, long id) {
        Log.d(TAG, "getEvent()");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(DataQuery.CONTENT_URI, DataQuery.PROJECTION_ALL,
                DataQuery.SELECTION_ID, new String[]{String.valueOf(id)}, null);
        Event event = null;
        while (cursor.moveToNext()) {
            event = Event.cursorToEvent(cursor);
        }
        cursor.close();
        return event;
    }

    //TODO test
    public static List<Event> getDirtyEvents(Context context) {
        Log.d(TAG, "getDirtyEvents()");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(DataQuery.CONTENT_URI, DataQuery.PROJECTION_ALL, DataQuery.SELECTION_DIRTY, null, null);
        List<Event> events = new ArrayList<Event>();
        Event event = null;
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
        Cursor cursor = resolver.query(DataQuery.CONTENT_URI, DataQuery.PROJECTION_ALL, null, null, null);
        List<Event> events = new ArrayList<Event>();
        Event event = null;
        while (cursor.moveToNext()) {
            event = Event.cursorToEvent(cursor);
            events.add(event);
        }
        cursor.close();
        return events;
    }

    public static int markEventAsDeleted(Context context, long id) {
        Log.d(TAG, "markEventAsDeleted() id: " + id);
        Uri uri = Uri.withAppendedPath(DataQuery.CONTENT_URI, String.valueOf(id));
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Event.COL_DELETED, true);
        values.put(Event.COL_DIRTY, true);
        return resolver.update(uri, values, null, null);
    }

    public static int updateEvent(Context context, Event event) {
        Log.d(TAG, "updateEvent()");
        Uri uri = Uri.withAppendedPath(DataQuery.CONTENT_URI, String.valueOf(event.get_id()));
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = event.getAsContentValues();// TODO pozor co vse
        // aktual.
        values.put(Event.COL_DIRTY, true);
        return resolver.update(uri, values, null, null);
    }

    public static void updateEventServerId(Context context, long id, String rowid) {
        Log.d(TAG, "updateEventServerId()");
        // Uri ukolu
        Uri uri = Uri.withAppendedPath(DataQuery.CONTENT_URI, String.valueOf(id));
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Event.COL_SERVER_ID, rowid);

        // Updatuje ukol - deleted = true
        resolver.update(uri, values, null, null);
    }

    final public static class DataQuery {

        // uri zdroje dat
        public static final Uri CONTENT_URI = Uri.parse(Consts.SCHEME + Consts.AUTHORITY + "/"
                + MyDatabaseHelper.TABLE_EVENTS);

        //TODO refaktor
        // vybere vsechny sloupce
        public static final String[] PROJECTION_ALL = {Event.COL_ID, Event.COL_SERVER_ID,
                Event.COL_DIRTY, Event.COL_DELETED, Event.COL_ICP,
                Event.COL_DATUM, Event.COL_KOD_PO, Event.COL_DRUH,
                Event.COL_CAS, Event.COL_IC_OBS, Event.COL_TYP,
                Event.COL_DATUM_ZMENY, Event.COL_POZNAMKA};

        // vyber podle id ukolu
        public static final String SELECTION_ID = Event.COL_ID + "=?";
        // vyber urcenych k sync
        public static final String SELECTION_DIRTY = Event.COL_DIRTY + "=1";
        // vyber nesmazanych
        public static final String SELECTION_UNDELETED = Event.COL_DELETED + "=0";
        // vyber podle data
        public static final String SELECTION_DATUM = Event.COL_DATUM + "=?";

    }

}
