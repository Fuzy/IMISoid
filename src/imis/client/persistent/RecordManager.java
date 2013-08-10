package imis.client.persistent;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.AppUtil;
import imis.client.R;
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
    private static final String TAG = RecordManager.class.getSimpleName();

    private static ContentProviderOperation addRecordOp(Record record) {
        ContentValues values = record.asContentValues();
        return ContentProviderOperation.newInsert(RecordQuery.CONTENT_URI).withValues(values).build();
    }

    public static void addRecords(Context context, Record[] records) {
        Log.d(TAG, "addRecords()");
        ContentResolver resolver = context.getContentResolver();
        ContentProviderClient client = resolver.acquireContentProviderClient(RecordQuery.CONTENT_URI);

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        for (Record record : records) {
            ops.add(addRecordOp(record));
        }
        try {
            Log.d(TAG, "RecordManager() ops size " + ops.size());
            long start = System.currentTimeMillis();
            client.applyBatch(ops);
            long elapsedTimeMillis = System.currentTimeMillis() - start;
            float elapsedTimeSec = elapsedTimeMillis / 1000F;
            Log.d(TAG, "RecordManager() elapsedTimeSec " + elapsedTimeSec);
        } catch (Exception e) {
            AppUtil.showInfo(context, context.getString(R.string.act_fail));
        }
        client.release();
    }

    public static int deleteRecordsOlderThan(Context context, long date) {
        Log.d(TAG, "deleteRecordsOlderThan()" + "date = [" + date + "]");
        return delete(context, RecordQuery.SELECTION_OLDER_THAN, new String[]{String.valueOf(date)});
    }

    public static int deleteRecordsOnKodpra(Context context, String kodpra) {
        Log.d(TAG, "deleteRecordsOnKodpra()" + "kodpra = [" + kodpra + "]");
        return delete(context, RecordQuery.SELECTION_KODPRA, new String[]{kodpra});
    }

    private static int delete(Context context, String where, String[] selectionArgs) {
        Log.d(TAG, "delete()" + "where = [" + where + "], selectionArgs = [" + Arrays.toString(selectionArgs) + "]");
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(RecordQuery.CONTENT_URI, where, selectionArgs);
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
        public static final String SELECTION_ZC = Record.COL_ZC + " LIKE ? || '%' ";
        public static final String SELECTION_OLDER_THAN = Record.COL_DATUM + "<?";
        public static final String SELECTION_KODPRA = Record.COL_KODPRA + " LIKE ? || '%' ";
        public static final String SELECTION_PERIOD = " ? <= " + Record.COL_DATUM + " and " + Record.COL_DATUM + " <= ? ";
        public static final String SELECTION_LIST = SELECTION_ZC + " and " + SELECTION_KODPRA + " and " + SELECTION_PERIOD;
        public static final String SELECTION_CHART = SELECTION_KODPRA + " and " + SELECTION_PERIOD;

    }
}
