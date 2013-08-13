package imis.client.persistent;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import imis.client.model.Employee;
import imis.client.model.Event;
import imis.client.model.Record;

/**
 * Class for creating a database.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = MyDatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "imisoid.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_EVENTS = "events";
    public static final String TABLE_RECORDS = "records";
    public static final String TABLE_EMPLOYEES = "employees";

    // Table of events
    public static final String EV_COL_LOCAL_ID = Event.COL_ID;
    public static final String EV_COL_SERVER_ID = Event.COL_SERVER_ID;
    public static final String EV_COL_DIRTY = Event.COL_DIRTY;
    public static final String EV_COL_SYNCMANAGED = Event.COL_SYNC_MANAGED;
    public static final String EV_COL_DELETED = Event.COL_DELETED;
    public static final String EV_COL_ICP = Event.COL_ICP;
    public static final String EV_COL_DATUM = Event.COL_DATUM;
    public static final String EV_COL_KOD_PO = Event.COL_KOD_PO;
    public static final String EV_COL_DRUH = Event.COL_DRUH;
    public static final String EV_COL_CAS = Event.COL_CAS;
    public static final String EV_COL_IC_OBS = Event.COL_IC_OBS;
    public static final String EV_COL_TYP = Event.COL_TYP;
    public static final String EV_COL_DATUM_ZMENY = Event.COL_DATUM_ZMENY;
    public static final String EV_COL_POZNAMKA = Event.COL_POZNAMKA;
    public static final String EV_COL_ERROR = Event.COL_ERROR;
    public static final String EV_COL_MSG = Event.COL_MSG;

    // Table of records
    private static final String REC_COL_LOCAL_ID = Record.COL_ID; // client id
    private static final String REC_COL_POZN_UKOL = Record.COL_POZN_UKOL;
    private static final String REC_SERVER_ID = Record.COL_SERVER_ID;
    private static final String REC_COL_DATUM = Record.COL_DATUM;
    private static final String REC_COL_KODPRA = Record.COL_KODPRA;
    private static final String REC_COL_MNOZSTVI_ODVED = Record.COL_MNOZSTVI_ODVED;
    private static final String REC_COL_POZNAMKA = Record.COL_POZNAMKA;
    private static final String REC_COL_STAV_V = Record.COL_STAV_V;
    private static final String REC_COL_POZN_HL = Record.COL_POZN_HL;
    private static final String REC_COL_ZC = Record.COL_ZC;
    private static final String REC_COL_CPOLZAK = Record.COL_CPOLZAK;
    private static final String REC_COL_CPOZZAK = Record.COL_CPOZZAK;

    // Table of employees
    private static final String EMP_COL_LOCAL_ID = Employee.COL_ID; // client id
    private static final String EMP_COL_ICP = Employee.COL_ICP;
    private static final String EMP_COL_KODPRA = Employee.COL_KODPRA;
    private static final String EMP_COL_JMENO = Employee.COL_JMENO;
    private static final String EMP_COL_SUB = Employee.COL_SUB;
    public static final String EMP_COL_DRUH = Employee.COL_DRUH;
    public static final String EMP_COL_DATUM = Employee.COL_DATUM;
    public static final String EMP_COL_CAS = Employee.COL_CAS;
    public static final String EMP_COL_KOD_PO = Employee.COL_KOD_PO;
    public static final String EMP_COL_WIDGET_ID = Employee.COL_WIDGET_ID;
    public static final String EMP_COL_FAV = Employee.COL_FAV;
    public static final String EMP_COL_USER = Employee.COL_USER;


    private static final String CREATE_EVENTS_TABLE = new String()
            .concat("create table " + TABLE_EVENTS)
            .concat("(")
            .concat(EV_COL_LOCAL_ID + " integer primary key autoincrement, ")
            .concat(EV_COL_SERVER_ID + " text,")
            .concat(EV_COL_DIRTY + " integer not null, ")
            .concat(EV_COL_SYNCMANAGED + " integer not null, ")
            .concat(EV_COL_DELETED + " integer not null, ")
            .concat(EV_COL_ICP + " text not null, ")
            .concat(EV_COL_DATUM + " integer not null,")
            .concat(EV_COL_KOD_PO + " text not null,")
            .concat(EV_COL_DRUH + " text not null,")
            .concat(EV_COL_CAS + " text not null,")
            .concat(EV_COL_IC_OBS + " text,")
            .concat(EV_COL_TYP + " text not null,")
            .concat(EV_COL_DATUM_ZMENY + " text not null,")
            .concat(EV_COL_POZNAMKA + " text,")
            .concat(EV_COL_ERROR + " integer not null, ")
            .concat(EV_COL_MSG + " text")
            .concat(");");

    // Database creation sql statement
    private static final String CREATE_RECORDS_TABLE = new String()
            .concat("create table " + TABLE_RECORDS)
            .concat("(")
            .concat(REC_COL_LOCAL_ID + " integer primary key autoincrement, ")
            .concat(REC_SERVER_ID + " text not null unique, ")
            .concat(REC_COL_DATUM + " integer not null, ")
            .concat(REC_COL_KODPRA + " text not null, ")
            .concat(REC_COL_ZC + " text, ")
            .concat(REC_COL_STAV_V + " text, ")
            .concat(REC_COL_CPOLZAK + " integer, ")
            .concat(REC_COL_CPOZZAK + " integer, ")
            .concat(REC_COL_MNOZSTVI_ODVED + " integer not null, ")
            .concat(REC_COL_POZN_HL + " text, ")
            .concat(REC_COL_POZN_UKOL + " text, ")
            .concat(REC_COL_POZNAMKA + " text")
            .concat(");");

    // Database creation sql statement
    private static final String CREATE_EMPLOYEES_TABLE = new String()
            .concat("create table " + TABLE_EMPLOYEES)
            .concat("(")
            .concat(EMP_COL_LOCAL_ID + " integer primary key autoincrement, ")
            .concat(EMP_COL_ICP + " text not null unique, ")
            .concat(EMP_COL_KODPRA + " text unique, ")
            .concat(EMP_COL_JMENO + " text, ")
            .concat(EMP_COL_SUB + " integer not null,")
            .concat(EMP_COL_DRUH + " text,")
            .concat(EMP_COL_DATUM + " integer,")
            .concat(EMP_COL_CAS + " integer,")
            .concat(EMP_COL_KOD_PO + " text,")
            .concat(EMP_COL_WIDGET_ID + " integer,")
            .concat(EMP_COL_FAV + " integer not null,")
            .concat(EMP_COL_USER + " integer not null")
            .concat(");");


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
        // insertEventsTestData(database);
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

    /*private void insertEventsTestData(SQLiteDatabase database) {
        Log.d(TAG, "insertEventsTestData()");
        database
                .execSQL("insert into "
                        + TABLE_EVENTS
                        + " values (1,null,0,0,'700510',1364169600000,'00','P','28800000','KDA','O',1364169600000,null, 0, null)");
        database
                .execSQL("insert into "
                        + TABLE_EVENTS
                        + " values (2,null,0,0,'700510',1364169600000,'02','O','61200000','KDA','O',1364169600000,null,0, null)");//61200000
        database
                .execSQL("insert into "
                        + TABLE_EVENTS
                        + " values (3,null,0,0,'700510',1364169600000,'00','P','64800000','KDA','O',1364169600000,null, 0, null)");//64800000
        database
                .execSQL("insert into "
                        + TABLE_EVENTS
                        + " values (4,null,0,0,'700510',1364169600000L,'01','O','68400000','KDA','O',1364169600000,null, 0, null)");//68400000
        database
                .execSQL("insert into "
                        + TABLE_EVENTS
                        + " values (5,null,1,0,'700510',1364169600000,'00','P','72000000','KDA','O',1364169600000,null, 0, null)");//72000000
        database
                .execSQL("insert into "
                        + TABLE_EVENTS
                        + " values (6,null,1,0,'700510',1364169600000,'00','O','75600000','KDA','O',1364169600000,null, 0, null)");//75600000

    }*/

}
