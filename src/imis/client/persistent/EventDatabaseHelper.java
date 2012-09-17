package imis.client.persistent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EventDatabaseHelper extends SQLiteOpenHelper {
  private static final String TAG = "EventDatabaseHelper";
  // TODO osetrit delky vstupu
  private static final String DATABASE_NAME = "events.db";
  private static final int DATABASE_VERSION = 1;

  // Tabulka events - povinne android polozky.
  public static final String TABLE_EVENTS = "events";
  public static final String COLUMN_ID = "_id"; // client id

  // Tabulka events - puvodni model (tabulka karta).
  public static final String COLUMN_ICP = "icp";
  public static final String COLUMN_DATUM = "datum";
  public static final String COLUMN_KOD_PO = "kod_po";
  public static final String COLUMN_DRUH = "druh";
  public static final String COLUMN_CAS = "cas";
  public static final String COLUMN_IC_OBS = "ic_obs";
  public static final String COLUMN_TYP = "typ";
  public static final String COLUMN_DATUM_ZMENY = "datum_zmeny";
  public static final String COLUMN_POZNAMKA = "poznamka";

  // Sloupce nutne pro synchronizaci.
  public static final String COLUMN_SERVER_ID = "server_id";
  public static final String COLUMN_DIRTY = "dirty";
  public static final String COLUMN_DELETED = "deleted";

  public static final String TABLE_EVENT_CODES = "kody_po";

  // Tabulka kody_po - puvodni model (tabulka kody_po).
  //TODO sloupes _id, kod_po jako unique
  public static final String COLUMN_KOD_PO_PRIM = "kod_po";
  public static final String COLUMN_POPIS = "popis";

  // Database creation sql statement
  private static final String CREATE_EVENTS_TABLE = "create table " + TABLE_EVENTS + "("
      + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_SERVER_ID
      + " integer not null," + COLUMN_DIRTY + " integer not null, " + COLUMN_DELETED
      + " integer not null, " + COLUMN_ICP + " text not null, " + COLUMN_DATUM
      + " integer not null," + COLUMN_KOD_PO + " text not null," + COLUMN_DRUH + " text not null,"
      + COLUMN_CAS + " text not null," + COLUMN_IC_OBS + " text," + COLUMN_TYP + " text not null,"
      + COLUMN_DATUM_ZMENY + " text not null," + COLUMN_POZNAMKA + " text," + " FOREIGN KEY ("
      + COLUMN_KOD_PO + ") REFERENCES " + TABLE_EVENT_CODES + " (" + COLUMN_KOD_PO_PRIM + ")"
      + ");";
  // TODO cizi klic, nalejt data

  private static final String CREATE_EVENT_CODES_TABLE = "create table " + TABLE_EVENT_CODES + "("
      + COLUMN_KOD_PO_PRIM + " text primary key, " + COLUMN_POPIS + " text not null" + ");";

  public EventDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  /**
   * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
   *      Is called by the framework, if the database does not exists.
   */
  @Override
  public void onCreate(SQLiteDatabase database) {
    Log.d(TAG, "onCreate()");
    database.execSQL(CREATE_EVENT_CODES_TABLE);
    database.execSQL(CREATE_EVENTS_TABLE);
    // TODO naplnit ciselnik vhodnejsim zpusobem
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('00', 'Normální')");
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('01', 'Služebně')");
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('02', 'Lékař')");
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('03', 'Soukromě (náhradní volno)')");
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('04', 'Nemoc')");
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('05', 'Dovolená')");
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('06', 'Ošetřování člena rodiny')");
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('08', 'Studijní volno')");
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('09', 'Oběd')");
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('10', 'Večeře')");
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('11', 'Přítomnost po pracovní době')");
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('12', 'xxx')");
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('13', 'xxx')");
    database.execSQL("insert into " + TABLE_EVENT_CODES + " values ('0', 'Základní')");
  }

  @Override
  public void onOpen(SQLiteDatabase db) {
    Log.d(TAG, "onOpen()");
    super.onOpen(db);
    if (!db.isReadOnly()) {
      // Enable foreign key constraints
      db.execSQL("PRAGMA foreign_keys=ON;");
    }
  }

  /**
   * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase,
   *      int, int) Is called, if the database version is increased in your
   *      application code. This method allows you to update the database
   *      schema.
   */
  @Override
  public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {
    database.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
    onCreate(database);

  }

}
