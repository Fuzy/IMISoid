package imis.client.persistent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import imis.client.model.Employee;
import imis.client.model.Record;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "MyDatabaseHelper";
    // TODO osetrit delky vstupu
    private static final String DATABASE_NAME = "events.db";//TODO novy nazev imisoid
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_EVENTS = "events";
    public static final String TABLE_RECORDS = "records";
    public static final String TABLE_EMPLOYEES = "employees";

    // Tabulka events - puvodni model (tabulka karta).
    public static final String EV_COL_LOCAL_ID = "_id"; // client id
    public static final String COLUMN_ICP = "icp";
    public static final String COLUMN_DATUM = "datum";
    public static final String COLUMN_KOD_PO = "kod_po";
    public static final String COLUMN_DRUH = "druh";
    public static final String COLUMN_CAS = "cas";
    public static final String COLUMN_IC_OBS = "ic_obs";
    public static final String COLUMN_TYP = "typ";
    public static final String COLUMN_DATUM_ZMENY = "datum_zmeny";
    public static final String COLUMN_POZNAMKA = "poznamka";

    // Tabulka events - sloupce nutne pro synchronizaci.
    public static final String COLUMN_SERVER_ID = "server_id";// rowid v oracle db
    public static final String COLUMN_DIRTY = "dirty";
    public static final String COLUMN_DELETED = "deleted";

    // Tabulka records
    private static final String REC_COL_LOCAL_ID = "_id"; // client id
    private static final String REC_COL_POZN_UKOL = Record.REC_COL_POZN_UKOL;
    private static final String REC_SERVER_ID = Record.REC_COL_SERVER_ID;
    private static final String REC_COL_DATUM = Record.REC_COL_DATUM;
    private static final String REC_COL_KODPRA = Record.REC_COL_KODPRA;
    private static final String REC_COL_MNOZSTVI_ODVED = Record.REC_COL_MNOZSTVI_ODVED;
    private static final String REC_COL_POZNAMKA = Record.REC_COL_POZNAMKA;
    private static final String REC_COL_STAV_V = Record.REC_COL_STAV_V;
    private static final String REC_COL_POZN_HL = Record.REC_COL_POZN_HL;
    private static final String REC_COL_ZC = Record.REC_COL_ZC;
    private static final String REC_COL_CPOLZAK = Record.REC_COL_CPOLZAK;
    private static final String REC_COL_CPOZZAK = Record.REC_COL_CPOZZAK;

    // Table of employees
    private static final String EMP_COL_LOCAL_ID = "_id"; // client id
    private static final String EMP_COL_ICP = Employee.COL_ICP;
    private static final String EMP_COL_KODPRA = Employee.COL_KODPRA;
    private static final String EMP_COL_SUB = Employee.COL_SUB;


    private static final String CREATE_EVENTS_TABLE = new String()
            .concat("create table " + TABLE_EVENTS)
            .concat("(")
            .concat(EV_COL_LOCAL_ID + " integer primary key autoincrement, ")
            .concat(COLUMN_SERVER_ID + " text,")
            .concat(COLUMN_DIRTY + " integer not null, ")
            .concat(COLUMN_DELETED + " integer not null, ")
            .concat(COLUMN_ICP + " text not null, ")
            .concat(COLUMN_DATUM + " integer not null,")
            .concat(COLUMN_KOD_PO + " text not null,")
            .concat(COLUMN_DRUH + " text not null,")
            .concat(COLUMN_CAS + " text not null,")
            .concat(COLUMN_IC_OBS + " text,")
            .concat(COLUMN_TYP + " text not null,")
            .concat(COLUMN_DATUM_ZMENY + " text not null,")
            .concat(COLUMN_POZNAMKA + " text")
            .concat(");");

    // Database creation sql statement
    private static final String CREATE_RECORDS_TABLE = new String()
            .concat("create table " + TABLE_RECORDS)
            .concat("(")
            .concat(REC_COL_LOCAL_ID + " integer primary key autoincrement, ")
            .concat(REC_SERVER_ID + " integer not null, ")
            .concat(REC_COL_DATUM + " integer not null, ")
            .concat(REC_COL_KODPRA + " text not null, ")
            .concat(REC_COL_ZC + " text not null, ")
            .concat(REC_COL_STAV_V + " text not null, ")
            .concat(REC_COL_CPOLZAK + " integer not null, ")
            .concat(REC_COL_CPOZZAK + " integer not null, ")
            .concat(REC_COL_MNOZSTVI_ODVED + " integer not null, ")
            .concat(REC_COL_POZN_HL + " text not null, ")
            .concat(REC_COL_POZN_UKOL + " text not null, ")
            .concat(REC_COL_POZNAMKA + " text not null")
            .concat(");");  //TODO null not null?

    // Database creation sql statement
    private static final String CREATE_EMPLOYEES_TABLE = new String()
            .concat("create table " + TABLE_EMPLOYEES)
            .concat("(")
            .concat(EMP_COL_LOCAL_ID + " integer primary key autoincrement, ")
            .concat(EMP_COL_ICP + " text not null, ")//TODO unique?
            .concat(EMP_COL_KODPRA + " text, ")
            .concat(EMP_COL_SUB + " integer not null")
            .concat(");");


    //TODO datum jako YYYY-MM-DD HH:MM:SS ne long
    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d(TAG, "onCreate()");
        Log.d(TAG, "CREATE_EVENTS_TABLE: " + CREATE_EVENTS_TABLE);
        Log.d(TAG, "CREATE_RECORDS_TABLE: " + CREATE_RECORDS_TABLE);
        Log.d(TAG, "CREATE_EMPLOYEES_TABLE: " + CREATE_EMPLOYEES_TABLE);
        database.execSQL(CREATE_EVENTS_TABLE);
        database.execSQL(CREATE_RECORDS_TABLE);
        database.execSQL(CREATE_EMPLOYEES_TABLE);
        insertEventsTestData(database);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.d(TAG, "onOpen()");
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(database);

    }

    private void insertEventsTestData(SQLiteDatabase database) {
        Log.d(TAG, "insertEventsTestData()");
        database
                .execSQL("insert into "
                        + TABLE_EVENTS
                        + " values (1,null,1,0,'700510',1364169600000,'00','P','57600000','KDA','O',1364169600000,null)");
        database
                .execSQL("insert into "
                        + TABLE_EVENTS
                        + " values (2,null,1,0,'700510',1364169600000,'02','O','61200000','KDA','O',1364169600000,null)");//61200000
        database
                .execSQL("insert into "
                        + TABLE_EVENTS
                        + " values (3,null,1,0,'700510',1364169600000,'00','P','64800000','KDA','O',1364169600000,null)");//64800000
        database
                .execSQL("insert into "
                        + TABLE_EVENTS
                        + " values (4,null,1,0,'700510',1364169600000,'01','O','68400000','KDA','O',1364169600000,null)");//68400000
        database
                .execSQL("insert into "
                        + TABLE_EVENTS
                        + " values (5,null,1,0,'700510',1364169600000,'00','P','72000000','KDA','O',1364169600000,null)");//72000000
        database
                .execSQL("insert into "
                        + TABLE_EVENTS
                        + " values (6,null,1,0,'700510',1364169600000,'00','O','75600000','KDA','O',1364169600000,null)");//75600000

    }

}
