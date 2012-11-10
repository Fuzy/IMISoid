package imis.client.model;

import imis.client.persistent.EventDatabaseHelper;
import static imis.client.model.Util.formatDate;

import java.sql.Date;
import java.text.ParseException;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.JsonObject;

public class Event {
  // sync
  private int _id;
  private String server_id;
  private boolean dirty;
  private boolean deleted;
  // data
  private String icp;
  private Date datum;
  private String kod_po;
  private String druh;
  private long cas;
  private String ic_obs;
  private String typ;
  private Date datum_zmeny;
  private String poznamka;

  //private static final String TAG = Event.class.getSimpleName();

  public Event() {
  }

  public boolean isDirty() {
    return dirty;
  }

  public void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public void set_id(int _id) {
    this._id = _id;
  }

  public int get_id() {
    return _id;
  }

  public Event(boolean dirty, boolean deleted, String icp, Date datum, String kod_po, String druh,
      long cas, String ic_obs, String typ, Date datum_zmeny, String poznamka) {
    super();
    this.dirty = dirty;
    this.deleted = deleted;
    this.icp = icp;
    this.datum = datum;
    this.kod_po = kod_po;
    this.druh = druh;
    this.cas = cas;
    this.ic_obs = ic_obs;
    this.typ = typ;
    this.datum_zmeny = datum_zmeny;
    this.poznamka = poznamka;
  }

  public String getServer_id() {
    return server_id;
  }

  public void setServer_id(String server_id) {
    this.server_id = server_id;
  }

  public String getIcp() {
    return icp;
  }

  public void setIcp(String icp) {
    this.icp = icp;
  }

  public Date getDatum() {
    return datum;
  }

  public void setDatum(Date datum) {
    this.datum = datum;
  }

  public String getKod_po() {
    return kod_po;
  }

  public void setKod_po(String kod_po) {
    this.kod_po = kod_po;
  }

  public String getDruh() {
    return druh;
  }

  public void setDruh(String druh) {
    this.druh = druh;
  }

  public long getCas() {
    return cas;
  }

  public void setCas(long cas) {
    this.cas = cas;
  }

  public String getIc_obs() {
    return ic_obs;
  }

  public void setIc_obs(String ic_obs) {
    this.ic_obs = ic_obs;
  }

  public String getTyp() {
    return typ;
  }

  public void setTyp(String typ) {
    this.typ = typ;
  }

  public Date getDatum_zmeny() {
    return datum_zmeny;
  }

  public void setDatum_zmeny(Date datum_zmeny) {
    this.datum_zmeny = datum_zmeny;
  }

  public String getPoznamka() {
    return poznamka;
  }

  public void setPoznamka(String poznamka) {
    this.poznamka = poznamka;
  }

  @Override
  public String toString() {
    return "Event [_id=" + _id + ", server_id=" + server_id + ", dirty=" + dirty + ", deleted="
        + deleted + ", icp=" + icp + ", datum=" + formatDate(datum) + ", kod_po=" + kod_po
        + ", druh=" + druh + ", cas=" + cas + ", ic_obs=" + ic_obs + ", typ=" + typ
        + ", datum_zmeny=" + formatDate(datum_zmeny) + ", poznamka=" + poznamka + "]";
  }

  public static Event jsonToEvent(JsonObject object) {
    Event event = Util.gson.fromJson(object, Event.class);
    return event;
  }

  // "dd.MM.yyyy"
  public static Event cursorToEvent(Cursor c) {
    Event event = new Event();
    event.set_id(c.getInt(0));
    // event.setIcp(c.getString(1));
    try {
      //Log.d(TAG, "cursorToEvent()" + " datum: " + c.getString(5));
      event.setDatum(new java.sql.Date((Util.df.parse(c.getString(5))).getTime()));
      //TODO na null to spadne
      //TODO metoda util
      //TODO indexy - konstanty
    }
    catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    /*
     * event.setKod_po(c.getString(3)); event.setDruh(c.getString(4));
      event.setIc_obs(c.getString(6));
     * event.setTyp(c.getString(7));
     */
    event.setCas(c.getLong(8));
    try {
      //Log.d(TAG, "cursorToEvent()" + " datum zmeny: " + c.getString(11));
      event.setDatum_zmeny(new java.sql.Date((Util.df.parse(c.getString(11))).getTime()));
    }
    catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // event.setPoznamka(c.getString(9))
    return event;
  }

  public ContentValues getAsContentValues() {
    ContentValues values = new ContentValues();
    values.put(COL_DIRTY, dirty);
    values.put(COL_DELETED, deleted);
    values.put(COL_SERVER_ID, server_id);
    values.put(COL_ICP, icp);
    values.put(COL_DATUM, Util.df.format(datum));
    values.put(COL_KOD_PO, kod_po);
    values.put(COL_DRUH, druh);
    values.put(COL_CAS, cas);
    values.put(COL_IC_OBS, ic_obs);
    values.put(COL_TYP, typ);
    values.put(COL_DATUM_ZMENY, Util.df.format(datum_zmeny));
    values.put(COL_POZNAMKA, poznamka);
    return values;
  }

  private static String COL_DIRTY = EventDatabaseHelper.COLUMN_DIRTY;
  private static String COL_SERVER_ID = EventDatabaseHelper.COLUMN_SERVER_ID;
  private static String COL_DELETED = EventDatabaseHelper.COLUMN_DELETED;

  private static String COL_ICP = EventDatabaseHelper.COLUMN_ICP;
  private static String COL_DATUM = EventDatabaseHelper.COLUMN_DATUM;
  private static String COL_KOD_PO = EventDatabaseHelper.COLUMN_KOD_PO;
  private static String COL_DRUH = EventDatabaseHelper.COLUMN_DRUH;
  private static String COL_CAS = EventDatabaseHelper.COLUMN_CAS;
  private static String COL_IC_OBS = EventDatabaseHelper.COLUMN_IC_OBS;
  private static String COL_TYP = EventDatabaseHelper.COLUMN_TYP;
  private static String COL_DATUM_ZMENY = EventDatabaseHelper.COLUMN_DATUM_ZMENY;
  private static String COL_POZNAMKA = EventDatabaseHelper.COLUMN_POZNAMKA;

  public static final String JSON_CLIENT_ID = "c";
  public static final String JSON_SERVER_ID = "si";
  public static final String JSON_DELETED = "de";
  public static final String JSON_UPDATED = "u";
  public static final String JSON_SUMMARY = "sm";
  public static final String JSON_DESCRIPTION = "ds";
  public static final String JSON_SYNC = "sy";

}
