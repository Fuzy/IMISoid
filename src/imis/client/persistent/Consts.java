package imis.client.persistent;

import android.net.Uri;

public class Consts {
  public static final String SCHEME = "content://";
  public static final String AUTHORITY = "imis.client.events.contentprovider";
  public static final Uri URI = Uri.parse(SCHEME + AUTHORITY + "/"
      + EventDatabaseHelper.TABLE_EVENTS);

  final public static class ColumnName {
    public static final String COLUMN_ID = EventDatabaseHelper.COLUMN_ID; // client
                                                                          // id
    public static final String COLUMN_ICP = EventDatabaseHelper.COLUMN_ICP;
    public static final String COLUMN_DATUM = EventDatabaseHelper.COLUMN_DATUM;
    public static final String COLUMN_KOD_PO = EventDatabaseHelper.COLUMN_KOD_PO;
    public static final String COLUMN_DRUH = EventDatabaseHelper.COLUMN_DRUH;
    public static final String COLUMN_CAS = EventDatabaseHelper.COLUMN_CAS;
    public static final String COLUMN_IC_OBS = EventDatabaseHelper.COLUMN_IC_OBS;
    public static final String COLUMN_TYP = EventDatabaseHelper.COLUMN_TYP;
    public static final String COLUMN_DATUM_ZMENY = EventDatabaseHelper.COLUMN_DATUM_ZMENY;
    public static final String COLUMN_POZNAMKA = EventDatabaseHelper.COLUMN_POZNAMKA;
    public static final String COLUMN_SERVER_ID = EventDatabaseHelper.COLUMN_SERVER_ID;
    public static final String COLUMN_DIRTY = EventDatabaseHelper.COLUMN_DIRTY;
    public static final String COLUMN_DELETED = EventDatabaseHelper.COLUMN_DELETED;
  }

}
