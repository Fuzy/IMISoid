package imis.client.persistent;

import imis.client.persistent.Consts.ColumnName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


public class MyEventContentProvider extends ContentProvider {
  private static final String TAG = "MyEventContentProvider";
  
  private EventDatabaseHelper database;
  private static final int EVENTS = 1;
  private static final int EVENT_ID = 2;
  private static final String AUTHORITY = "imis.client.events.contentprovider";
  private static final String TABLE_EVENTS = EventDatabaseHelper.TABLE_EVENTS;
  private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  
  static {
    sURIMatcher.addURI(AUTHORITY, TABLE_EVENTS, EVENTS);
    sURIMatcher.addURI(AUTHORITY, TABLE_EVENTS + "/#", EVENT_ID);
  }
  
  @Override
  public boolean onCreate() {
    database = new EventDatabaseHelper(getContext());
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
    case EVENT_ID:
      // smaze jednu polozku
      String id = uri.getLastPathSegment();
      rowsDeleted = sqlDB.delete(TABLE_EVENTS, EventDatabaseHelper.COLUMN_ID + "="
          + id, null);
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
  public Uri insert(Uri uri, ContentValues initialValues) {
    int uriType = sURIMatcher.match(uri);

    // Nastavi hodnoty values
    ContentValues values;
    if (initialValues != null) {
      values = new ContentValues(initialValues);
    }
    else {
      values = new ContentValues();
    }

    // Vsechna not null pole musi byt nastavena, vytvari se novy ukol,
    // insert se vola po pridani ukolu uzivatelem i po pridani pri synchronizaci
    if (values.containsKey(ColumnName.COLUMN_SERVER_ID) == false) {
      values.put(ColumnName.COLUMN_SERVER_ID, -1);
    }
    if (values.containsKey(ColumnName.COLUMN_DIRTY) == false) {
      values.put(ColumnName.COLUMN_DIRTY, 1);
    }
    if (values.containsKey(ColumnName.COLUMN_DELETED) == false) {
      values.put(ColumnName.COLUMN_DELETED, 0);
    }
    if (values.containsKey(ColumnName.COLUMN_DATUM_ZMENY) == false) {
      values.put(ColumnName.COLUMN_DATUM_ZMENY, "");//TODO default datum
    }
    if (values.containsKey(ColumnName.COLUMN_ICP) == false) {
      values.put(ColumnName.COLUMN_ICP, "");
    }
    if (values.containsKey(ColumnName.COLUMN_DATUM) == false) {
      values.put(ColumnName.COLUMN_DATUM, "");//TODO default datum
    }
    if (values.containsKey(ColumnName.COLUMN_KOD_PO) == false) {
      values.put(ColumnName.COLUMN_KOD_PO, "");
    }
    if (values.containsKey(ColumnName.COLUMN_DRUH) == false) {
      values.put(ColumnName.COLUMN_DRUH, "");
    }
    if (values.containsKey(ColumnName.COLUMN_CAS) == false) {
      values.put(ColumnName.COLUMN_CAS, "");
    }
    if (values.containsKey(ColumnName.COLUMN_TYP) == false) {
      values.put(ColumnName.COLUMN_TYP, "");
    }

    long id = 0;
    // ziska odkaz na databazi

    SQLiteDatabase sqlDB = database.getWritableDatabase();

    switch (uriType) {
    case EVENTS:
      // muze vlozit jen 1 zaznam
      id = sqlDB.insert(TABLE_EVENTS, null, values);
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
    // nastavi tabulku na kterou se bude dotazovat
    queryBuilder.setTables(TABLE_EVENTS);

    int uriType = sURIMatcher.match(uri);
    switch (uriType) {
    case EVENTS:
      // vsechny radky tabulky
      break;
    case EVENT_ID:
      // nastavi WHERE sekci dotazu
      queryBuilder.appendWhere(ColumnName.COLUMN_ID + "=" + uri.getLastPathSegment());
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
    int rowsUpdated = 0;

    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase sqlDB = database.getWritableDatabase();

    switch (uriType) {
    case EVENTS:
      rowsUpdated = sqlDB.update(TABLE_EVENTS, values, selection, selectionArgs);
      break;
    case EVENT_ID:
      String id = uri.getLastPathSegment();
      rowsUpdated = sqlDB.update(TABLE_EVENTS, values,
          ColumnName.COLUMN_ID + "=" + id, null);
      break;
    }
    getContext().getContentResolver().notifyChange(uri, null);

    return rowsUpdated;
  }

}
