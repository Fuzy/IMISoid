package imis.client.persistent;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import imis.client.persistent.Consts.ColumnName;

public class MyContentProvider extends ContentProvider {
    private static final String TAG = "MyContentProvider";
    // TODO pouzivat ContentProviderClient
    // TODO rozsirit o praci s dalsi tabulkou?, asi jen nutne operace jako query

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

        // Nastavi hodnoty values
        /*ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        // Vsechna not null pole musi byt nastavena, vytvari se novy ukol,
        // insert se vola po pridani ukolu uzivatelem i po pridani pri synchronizaci
        Date now = new Date();
        if (values.containsKey(ColumnName.COLUMN_SERVER_ID) == false) {
            values.put(ColumnName.COLUMN_SERVER_ID, -1);
        }
    *//*if (values.containsKey(ColumnName.COLUMN_DIRTY) == false) {
      values.put(ColumnName.COLUMN_DIRTY, 1);
    }
    if (values.containsKey(ColumnName.COLUMN_DELETED) == false) {
      values.put(ColumnName.COLUMN_DELETED, 0);
    }*//*
        if (values.containsKey(ColumnName.COLUMN_DATUM_ZMENY) == false) {
            values.put(ColumnName.COLUMN_DATUM_ZMENY, df.format(now));
        }
        if (values.containsKey(ColumnName.COLUMN_ICP) == false) {
            values.put(ColumnName.COLUMN_ICP, "");
        }
        if (values.containsKey(ColumnName.COLUMN_DATUM) == false) {
            values.put(ColumnName.COLUMN_DATUM, df.format(now));
        }
        if (values.containsKey(ColumnName.COLUMN_KOD_PO) == false) {
            values.put(ColumnName.COLUMN_KOD_PO, "");
        }
        if (values.containsKey(ColumnName.COLUMN_DRUH) == false) {
            values.put(ColumnName.COLUMN_DRUH, "");
        }
    *//*if (values.containsKey(ColumnName.COLUMN_CAS) == false) {
      values.put(ColumnName.COLUMN_CAS, "");
    }*//*
        if (values.containsKey(ColumnName.COLUMN_TYP) == false) {
            values.put(ColumnName.COLUMN_TYP, "");
        }*/

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
            default:
                break;
        }
        // upozorni posluchace
        getContext().getContentResolver().notifyChange(uri, null);

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
                queryBuilder.appendWhere(ColumnName.COLUMN_ID + "=" + uri.getLastPathSegment());
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
        Log.d(TAG, "update");
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
            case EVENT_ID:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "update EVENT_ID id: " + id);
                rowsUpdated = sqlDB.update(TABLE_EVENTS, values, ColumnName.COLUMN_ID + "=" + id, null);
                break;
            default:
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

}
