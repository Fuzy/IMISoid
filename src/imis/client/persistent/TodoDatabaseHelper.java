package imis.client.persistent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDatabaseHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "events.db";
  public static final String TABLE_TODOS = "events";
  public static final String COLUMN_ID = "_id"; // client id
  public static final String COLUMN_SERVER_ID = "server_id";
  public static final String COLUMN_DIRTY = "dirty";
  public static final String COLUMN_DELETED = "deleted";
  public static final String COLUMN_UPDATED = "updated"; // cas posledni zmeny
  
  public static final String COLUMN_SUMMARY = "summary";
  public static final String COLUMN_DESCRIPTION = "description";
  private static final int DATABASE_VERSION = 1;

  // Database creation sql statement
  private static final String DATABASE_CREATE = "create table " + TABLE_TODOS
      + "( " + COLUMN_ID + " integer primary key autoincrement, "
      + COLUMN_SERVER_ID + " integer not null," + COLUMN_DIRTY
      + " integer not null, " + COLUMN_DELETED + " integer not null, "
      + COLUMN_UPDATED + " integer not null, " + COLUMN_SUMMARY
      + " text not null," + COLUMN_DESCRIPTION + " text);";

  public TodoDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  /**
   * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
   *      Is called by the framework, if the database does not exists.
   */
  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);

  }

  /**
   * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase,
   *      int, int) Is called, if the database version is increased in your
   *      application code. This method allows you to update the database
   *      schema.
   */
  @Override
  public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {
    database.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
    onCreate(database);

  }

}
