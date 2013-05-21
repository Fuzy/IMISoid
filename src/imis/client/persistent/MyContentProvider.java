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
import imis.client.model.Record;

import java.util.Arrays;

public class MyContentProvider extends ContentProvider {
    private static final String TAG = "MyContentProvider";

    private MyDatabaseHelper database;
    private static final int EVENTS = 1;
    private static final int EVENT_ID = 2;
    private static final int RECORDS = 3;
    private static final int RECORD_ID = 4;
    private static final int EMPLOYEES = 5;
    private static final int EMPLOYEE_ID = 6;

    private static final String AUTHORITY = Consts.AUTHORITY;
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

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate()");
        database = new MyDatabaseHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete() uri " + uri);
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted = 0;
        String id;
        SQLiteDatabase sqlDB = database.getWritableDatabase();

        switch (uriType) {
            case EVENTS:
                rowsDeleted = sqlDB.delete(TABLE_EVENTS, selection, selectionArgs);
                break;
            case RECORDS:
                rowsDeleted = sqlDB.delete(TABLE_RECORDS, selection, selectionArgs);
                break;
            case EMPLOYEES:
                rowsDeleted = sqlDB.delete(TABLE_EMPLOYEES, selection, selectionArgs);
                break;
            case EVENT_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = sqlDB.delete(TABLE_EVENTS, EventManager.EventQuery.SELECTION_ID, new String[]{id});
                break;
            case EMPLOYEE_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = sqlDB.delete(TABLE_EMPLOYEES, EventManager.EventQuery.SELECTION_ID, new String[]{id});
                break;
            default:
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(TAG, "delete() rowsDeleted " + rowsDeleted);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert()" + "uri = [" + uri + "], values = [" + values + "]");
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
                id = sqlDB.insert(TABLE_RECORDS, null, values);
            default:
                break;
        }

        uri = Uri.withAppendedPath(uri, String.valueOf(id));
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(TAG, "insert() uri " + uri);
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Log.d(TAG, "uri = [" + uri + "], projection = [" + projection + "], " +
                "selection = [" + selection + "], selectionArgs = [" + Arrays.toString(selectionArgs) + "], " +
                "sortOrder = [" + sortOrder + "]");
        Log.d(TAG, "query() uri " + uri);
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();


        int uriType = sURIMatcher.match(uri);
        Log.d(TAG, "query() uriType " + uriType);
        switch (uriType) {
            case EVENTS:
                // vsechny radky tabulky
                queryBuilder.setTables(TABLE_EVENTS);
                break;
            case RECORDS:
                // vsechny radky tabulky
                queryBuilder.setTables(TABLE_RECORDS);
                break;
            case EMPLOYEES:
                queryBuilder.setTables(TABLE_EMPLOYEES);
                break;
            default:
                break;
        }

        SQLiteDatabase db = database.getWritableDatabase();

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null,
                sortOrder);
        Log.d(TAG, "query() cursor size " + cursor.getCount());

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update uri " + uri);
        int rowsUpdated = 0;


        int uriType = sURIMatcher.match(uri);
        String id;
        SQLiteDatabase sqlDB = database.getWritableDatabase();

        switch (uriType) {
            case EVENTS:
                rowsUpdated = sqlDB.update(TABLE_EVENTS, values, selection, selectionArgs);
                break;
            case RECORDS:
                rowsUpdated = sqlDB.update(TABLE_RECORDS, values, selection, selectionArgs);
                break;
            case RECORD_ID:
                id = uri.getLastPathSegment();
                rowsUpdated = sqlDB.update(TABLE_RECORDS, values, Record.COL_ID + "=" + id, null);
                Log.d(TAG, "update() RECORDS_ID rowsUpdated " + rowsUpdated);
                break;
            case EMPLOYEE_ID:
                id = uri.getLastPathSegment();
                rowsUpdated = sqlDB.update(TABLE_EMPLOYEES, values, Employee.COL_ID + "=" + id, null);
                Log.d(TAG, "update() EMPLOYEE_ID rowsUpdated " + rowsUpdated);
                break;/*
            case EMPLOYEES:
                *//*rowsUpdated = sqlDB.update(TABLE_EMPLOYEES, values, selection, selectionArgs);
                if (rowsUpdated > 0) uri = Uri.withAppendedPath(uri, selectionArgs[0]);
                Log.d(TAG, "update() EMPLOYEES rowsUpdated " + rowsUpdated);*//*
                throw new NotImplementedException();*/
            //break;
            case EVENT_ID:
                id = uri.getLastPathSegment();
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
