package imis.client.persistent;

import android.net.Uri;

//TODO refaktor
public class Consts {
  public static final String SCHEME = "content://";
  public static final String AUTHORITY = "imis.client.events.contentprovider";
  public static final Uri URI = Uri.parse(SCHEME + AUTHORITY + "/"
      + MyDatabaseHelper.TABLE_EVENTS);

  final public static class ColumnName {
    public static final String COLUMN_ID = MyDatabaseHelper.EV_COL_LOCAL_ID; // client
                                                                          // id
    public static final String COLUMN_ICP = MyDatabaseHelper.COLUMN_ICP;
    public static final String COLUMN_DATUM = MyDatabaseHelper.COLUMN_DATUM;
    public static final String COLUMN_KOD_PO = MyDatabaseHelper.COLUMN_KOD_PO;
    public static final String COLUMN_DRUH = MyDatabaseHelper.COLUMN_DRUH;
    public static final String COLUMN_CAS = MyDatabaseHelper.COLUMN_CAS;
    public static final String COLUMN_IC_OBS = MyDatabaseHelper.COLUMN_IC_OBS;
    public static final String COLUMN_TYP = MyDatabaseHelper.COLUMN_TYP;
    public static final String COLUMN_DATUM_ZMENY = MyDatabaseHelper.COLUMN_DATUM_ZMENY;
    public static final String COLUMN_POZNAMKA = MyDatabaseHelper.COLUMN_POZNAMKA;
    public static final String COLUMN_SERVER_ID = MyDatabaseHelper.COLUMN_SERVER_ID;
    public static final String COLUMN_DIRTY = MyDatabaseHelper.COLUMN_DIRTY;
    public static final String COLUMN_DELETED = MyDatabaseHelper.COLUMN_DELETED;
  }

}
