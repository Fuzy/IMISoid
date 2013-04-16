package imis.client.persistent;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import imis.client.model.Record;

import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 7.4.13
 * Time: 18:38
 */
public class RecordManager {
    private static final String TAG = "RecordManager";

    public static int addRecord(Context context, Record record) {
        Log.d(TAG, "addRecord() " + record);
        ContentValues values = record.getAsContentValues();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(DataQuery.CONTENT_URI, values);
        return Integer.valueOf(uri.getLastPathSegment());
    }

    public static void addRecords(Context context, List<Record> records) {
        Log.d(TAG, "addRecords()");
        for (Record record : records) {
            addRecord(context, record);
        }
    }

    public static List<Record> getAllRecords(Context context) {
        Log.d(TAG, "getAllEvents()");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(DataQuery.CONTENT_URI, DataQuery.PROJECTION_ALL, null, null, null);
        List<Record> records = new ArrayList<>();
        Record record;
        while (cursor.moveToNext()) {
            record = Record.cursorToRecord(cursor);
            records.add(record);
        }
        cursor.close();
        return records;
    }

    /*public static List<Record> jsonToList(String json) {
        Log.d(TAG, "jsonToList()");
        Type type = new TypeToken<Collection<Record>>() {
        }.getType();
        return gson.fromJson(json, type);
    }*/

    final public static class DataQuery {

        public static final Uri CONTENT_URI = Uri.parse(Consts.SCHEME + Consts.AUTHORITY + "/"
                + MyDatabaseHelper.TABLE_RECORDS);

        public static final String[] PROJECTION_ALL = {Record.COL_ID, Record.COL_ZC};

    }
}
