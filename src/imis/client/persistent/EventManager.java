package imis.client.persistent;

import imis.client.model.Event;
import imis.client.persistent.Consts.ColumnName;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class EventManager {
  private static final String TAG = EventManager.class.getSimpleName();

  public static long updateEvents(Context context, List<JsonObject> serverEvents,
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
      addEvent(context, false, event);

    }

    return currentSyncMarker;
  }

  /**
   * @param context
   * @param dirty
   *          true - pokud pridano uzivatelem, false ziskano od serveru
   * @param event
   * @return
   */
  public static long addEvent(Context context, boolean dirty, Event event) {
    Log.d(TAG, "addTodo()");
    ContentValues values = event.getAsContentValues();
    values.put(ColumnName.COLUMN_DIRTY, dirty);
    ContentResolver resolver = context.getContentResolver();
    Uri uri = resolver.insert(DataQuery.CONTENT_URI, values);
    return Long.valueOf(uri.getLastPathSegment());
  }

  final public static class DataQuery {

    // uri zdroje dat
    public static final Uri CONTENT_URI = Uri.parse(Consts.SCHEME + Consts.AUTHORITY + "/"
        + EventDatabaseHelper.TABLE_EVENTS);

    // vybere vsechny sloupce
    public static final String[] PROJECTION = { ColumnName.COLUMN_ID, ColumnName.COLUMN_SERVER_ID,
        ColumnName.COLUMN_DIRTY, ColumnName.COLUMN_DELETED, ColumnName.COLUMN_ICP,
        ColumnName.COLUMN_DATUM, ColumnName.COLUMN_KOD_PO, ColumnName.COLUMN_DRUH,
        ColumnName.COLUMN_CAS, ColumnName.COLUMN_IC_OBS, ColumnName.COLUMN_TYP,
        ColumnName.COLUMN_DATUM_ZMENY, ColumnName.COLUMN_POZNAMKA };

    // vyber podle id ukolu public static final String SELECTION =
    // ColumnName.COLUMN_ID + "=?";

  }

}
