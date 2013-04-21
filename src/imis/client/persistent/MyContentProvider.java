package imis.client.persistent;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import imis.client.model.Employee;
import imis.client.model.Event;

public class MyContentProvider extends ContentProvider {
    private static final String TAG = "MyContentProvider";
    // TODO pouzivat ContentProviderClient?

    private MyDatabaseHelper database;
    private static final int EVENTS = 1;
    private static final int EVENT_ID = 2;
    private static final int RECORDS = 3;
    private static final int RECORD_ID = 4;
    private static final int EMPLOYEES = 5;
    private static final int EMPLOYEE_ID = 6;

    private static final String AUTHORITY = Consts.AUTHORITY;//TODO prejmenovat
    private static final String TABLE_EVENTS = MyDatabaseHelper.TABLE_EVENTS;
    private static final String TABLE_RECORDS = MyDatabaseHelper.TABLE_RECORDS;
    private static final String TABLE_EMPLOYEES = MyDatabaseHelper.TABLE_EMPLOYEES;
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, TABLE_EVENTS, EVENTS);
        sURIMatcher.addURI(AUTHORITY, TABLE_RECORDS, RECORDS);
        sURIMatcher.addURI(AUTHORITY, TABLE_EMPLOYEES, EMPLOYEES);
        sURIMatcher.addURI(AUTHORITY, TABLE_EVENTS + "/#", EVENT_ID);
        sURIMatcher.addURI(AUTHORITY, TABLE_RECORDS + "/#", RECORD_ID);
        sURIMatcher.addURI(AUTHORITY, TABLE_EMPLOYEES + "/#", EMPLOYEE_ID);
    }
    //TODO vykazy zapis,  dotaz

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate()");
        database = new MyDatabaseHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted = 0;

        SQLiteDatabase sqlDB = database.getWritableDatabase();

        switch (uriType) {
            case EVENTS:
                // smaze vice polozek podle WHERE
                rowsDeleted = sqlDB.delete(TABLE_EVENTS, selection, selectionArgs);
                break;
            case RECORDS:
                // smaze vice polozek podle WHERE
                rowsDeleted = sqlDB.delete(TABLE_RECORDS, selection, selectionArgs);
                break;
            case EMPLOYEES:
                // smaze vice polozek podle WHERE
                rowsDeleted = sqlDB.delete(TABLE_EMPLOYEES, selection, selectionArgs);
                break;
            case EVENT_ID:
                // smaze jednu polozku
                String id = uri.getLastPathSegment();
                rowsDeleted = sqlDB.delete(TABLE_EVENTS, MyDatabaseHelper.EV_COL_LOCAL_ID + "=" + id, null);
                break;
            default:
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);

        long id = 0;
        // ziska odkaz na databazi
        SQLiteDatabase sqlDB = database.getWritableDatabase();

        switch (uriType) {
            case EVENTS:
                // muze vlozit jen 1 zaznam
                id = sqlDB.insert(TABLE_EVENTS, null, values);
                break;
            case EMPLOYEES:
                id = sqlDB.insert(TABLE_EMPLOYEES, null, values);
                break;
            case RECORDS:
                Log.d(TAG, "insert() RECORDS");
                id = sqlDB.insert(TABLE_RECORDS, null, values);
            default:
                break;
        }
        // upozorni posluchace
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(TAG, "insert()" + Uri.parse(uri + "/" + id));
        // vrati uri na pridanou radku
        return Uri.parse(uri + "/" + id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case EVENTS:
                // vsechny radky tabulky
                queryBuilder.setTables(TABLE_EVENTS);
                break;
            case RECORDS:
                // vsechny radky tabulky
                queryBuilder.setTables(TABLE_RECORDS);
                break;
            case EVENT_ID:
                // nastavi WHERE sekci dotazu
                queryBuilder.appendWhere(Event.COL_ID + "=" + uri.getLastPathSegment());
                break;
            case EMPLOYEES:
                queryBuilder.setTables(TABLE_EMPLOYEES);
                break;
            default:
                break;
        }

        // ziska odkaz na databazi
        SQLiteDatabase db = database.getWritableDatabase();
        // provede dotaz k databazi
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null,
                sortOrder);

        // upozorni posluchace
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update uri " + uri);
        int rowsUpdated = 0;

        int uriType = sURIMatcher.match(uri);

        SQLiteDatabase sqlDB = database.getWritableDatabase();

        switch (uriType) {
            case EVENTS:
                rowsUpdated = sqlDB.update(TABLE_EVENTS, values, selection, selectionArgs);
                break;
            case RECORDS:
                rowsUpdated = sqlDB.update(TABLE_RECORDS, values, selection, selectionArgs);
                break;
            case EMPLOYEE_ID:
                String icp = uri.getLastPathSegment();
                rowsUpdated = sqlDB.update(TABLE_EMPLOYEES, values, Employee.COL_ICP + "=" + icp, null);
                Log.d(TAG, "update() EMPLOYEES rowsUpdated " + rowsUpdated);
                break;
            case EVENT_ID:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "update EVENT_ID id: " + id);
                rowsUpdated = sqlDB.update(TABLE_EVENTS, values, Event.COL_ID + "=" + id, null);
                break;
            default:
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

}
