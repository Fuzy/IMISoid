package imis.client.persistent;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.model.Record;

import java.util.ArrayList;
import java.util.Arrays;
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
        Uri uri = resolver.insert(RecordQuery.CONTENT_URI, values);
        return Integer.valueOf(uri.getLastPathSegment());
    }

    public static void addRecords(Context context, Record[] records) {
        Log.d(TAG, "addRecords()");
        for (Record record : records) {
            if (updateRecordOnServerId(context, record) == 0)
                addRecord(context, record);
        }
    }

    public static int deleteRecordsOlderThan(Context context, long date) {
        Log.d(TAG, "deleteRecordsOlderThan()" + "date = [" + date + "]");
        return delete(context, RecordQuery.SELECTION_OLDER_THAN, new String[]{String.valueOf(date)});
    }

    public static int delete(Context context, String where, String[] selectionArgs){
        Log.d(TAG,"delete()" + "where = [" + where + "], selectionArgs = [" + Arrays.toString(selectionArgs) + "]");
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(RecordQuery.CONTENT_URI, where, selectionArgs);
    }

    private static int updateRecordOnServerId(Context context, Record record) {
        Record record1 = getRecord(context, record.getId());
        Log.d(TAG, "updateRecordOnServerId() record1 " + ((record1 != null) ? record1.get_id() : null));
        if (record1 == null) return 0;
        Uri uri = Uri.withAppendedPath(RecordQuery.CONTENT_URI, String.valueOf(record1.get_id()));
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = record.getAsContentValues();
        int updated = resolver.update(uri, values, null, null);
        Log.d(TAG, "updateRecord() updated " + updated);
        return updated;
    }

    public static Record getRecord(Context context, String serverId) {
        Log.d(TAG, "getRecord()" + "serverId = [" + serverId + "]");
        return getRecord(context, RecordQuery.SELECTION_SERVER_ID, new String[]{serverId});
    }

    public static Record getRecord(Context context, long id) {
        Log.d(TAG, "getRecord()" + "id = [" + id + "]");
        return getRecord(context, RecordQuery.SELECTION_ID, new String[]{String.valueOf(id)});
    }

    private static Record getRecord(Context context, String selection, String[] selectionArgs) {
        Log.d(TAG, "getRecord()" + "selection = [" + selection + "], selectionArgs = [" + Arrays.toString(selectionArgs) + "]");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(RecordQuery.CONTENT_URI, null,
                selection, selectionArgs, null);
        Record employee = null;
        while (cursor.moveToNext()) {
            employee = Record.cursorToRecord(cursor);
        }
        return employee;
    }

    public static List<Record> getAllRecords(Context context) {
        Log.d(TAG, "getAllEvents()");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(RecordQuery.CONTENT_URI, null, null, null, null);
        List<Record> records = new ArrayList<>();
        Record record;
        while (cursor.moveToNext()) {
            record = Record.cursorToRecord(cursor);
            records.add(record);
        }
        cursor.close();
        return records;
    }

    final public static class RecordQuery {

        public static final Uri CONTENT_URI = Uri.parse(Consts.SCHEME + AppConsts.AUTHORITY1 + "/"
                + MyDatabaseHelper.TABLE_RECORDS);

        public static final String SELECTION_ID = Record.COL_ID + "=?";
        public static final String SELECTION_SERVER_ID = Record.COL_SERVER_ID + " LIKE ? || '%' ";
        public static final String SELECTION_ZC = Record.COL_ZC + " LIKE ? || '%' ";
        public static final String SELECTION_OLDER_THAN = Record.COL_DATUM + "<?";
        public static final String SELECTION_KODPRA = Record.COL_KODPRA + " LIKE ? || '%' ";
        public static final String SELECTION_PERIOD = " ? <= " + Record.COL_DATUM + " and " + Record.COL_DATUM + " <= ? ";
        public static final String SELECTION_LIST = SELECTION_ZC + " and " + SELECTION_KODPRA + " and " + SELECTION_PERIOD;
        public static final String SELECTION_CHART = SELECTION_KODPRA + " and " + SELECTION_PERIOD;

    }
}
