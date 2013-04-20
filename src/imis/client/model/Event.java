package imis.client.model;

import android.content.ContentValues;
import android.database.Cursor;
import org.codehaus.jackson.annotate.JsonIgnore;

import static imis.client.AppUtil.formatDate;
import static imis.client.AppUtil.formatTime;

public class Event {
    // sync
    @JsonIgnore
    private int _id;
    private String server_id;
    @JsonIgnore
    private boolean dirty;
    @JsonIgnore
    private boolean deleted;
    // data
    private String icp;
    private long datum;//private Date datum;
    private String kod_po;
    private String druh;
    private long cas;
    private String ic_obs;
    private String typ;
    private long datum_zmeny;//private Date datum_zmeny;
    private String poznamka;

    // private static final String TAG = Event.class.getSimpleName();

    public Event() {
    }

    public boolean isDirty() {
        return dirty;
    }

    @JsonIgnore
    public boolean isDruhArrival() {
        return DRUH_ARRIVAL.equals(druh);
    }

    @JsonIgnore
    public boolean isDruhLeave() {
        return DRUH_LEAVE.equals(druh);
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

    public Event(boolean dirty, boolean deleted, String icp, long datum, String kod_po, String druh,
                 long cas, String ic_obs, String typ, long datum_zmeny, String poznamka) {
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

    public boolean hasServer_id() {
        if (server_id == null) return false;//TODO nebo prazdny retezec?
        else return true;
    }

    public String getIcp() {
        return icp;
    }

    public void setIcp(String icp) {
        this.icp = icp;
    }

    public long getDatum() {
        return datum;
    }

    public void setDatum(long datum) {
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

    public long getDatum_zmeny() {
        return datum_zmeny;
    }

    public void setDatum_zmeny(long datum_zmeny) {
        this.datum_zmeny = datum_zmeny;
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }

    //TODO formatovat datum
    @Override
    public String toString() {
        return "Event [_id=" + _id + ", server_id=" + server_id + ", dirty=" + dirty + ", deleted="
                + deleted + ", icp=" + icp + ", datum=" + formatDate(datum) + ", datum=" + datum
                + ", kod_po=" + kod_po
                + ", druh=" + druh + ", cas=" + formatTime(cas) + ", ic_obs=" + ic_obs + ", typ=" + typ
                + ", datum_zmeny=" + formatDate(datum_zmeny) + ", poznamka=" + poznamka + "]";
    }

    // "dd.MM.yyyy"
    public static Event cursorToEvent(Cursor c) {
        Event event = new Event();
        event.set_id(c.getInt(COL_NUM_ID));
        event.setServer_id(c.getString(COL_NUM_SERVER_ID));
        event.setDirty(c.getInt(COL_NUM_DIRTY) > 0);
        event.setDeleted(c.getInt(COL_NUM_DELETED) > 0);
        event.setIcp(c.getString(COL_NUM_ICP));
        event.setDatum((c.getLong(COL_NUM_DATUM)));//event.setDatum(stringToDate(c.getString(COL_NUM_DATUM)))
        event.setKod_po(c.getString(COL_NUM_KOD_PO));
        event.setDruh(c.getString(COL_NUM_DRUH));
        event.setCas(c.getLong(COL_NUM_CAS));
        event.setIc_obs(c.getString(COL_NUM_IC_OBS));
        event.setTyp(c.getString(COL_NUM_TYP));
        event.setDatum_zmeny(c.getLong(COL_NUM_DATUM_ZMENY));
        event.setPoznamka(c.getString(COL_NUM_POZNAMKA));
        return event;
    }

    public ContentValues asContentValues() {
        //TODO asi tam dat jen to spolecne pro add a update
        ContentValues values = new ContentValues();
        values.put(COL_DIRTY, dirty);
        values.put(COL_DELETED, deleted);
        if (server_id != null) {
            values.put(COL_SERVER_ID, server_id);
        }
        if (icp != null) {
            values.put(COL_ICP, icp);
        }
        values.put(COL_DATUM, (datum));
        if (kod_po != null) {
            values.put(COL_KOD_PO, kod_po);
        }
        if (druh != null) {
            values.put(COL_DRUH, druh);
        }
        values.put(COL_CAS, cas);
        if (ic_obs != null) {
            values.put(COL_IC_OBS, ic_obs);
        }
        if (typ != null) {
            values.put(COL_TYP, typ);
        }
        values.put(COL_DATUM_ZMENY, (datum_zmeny));
        if (poznamka != null) {
            values.put(COL_POZNAMKA, poznamka);
        }
        return values;
    }

    public static final String COL_ID = "_id";
    public static final String COL_SERVER_ID = "server_id";// rowid v oracle db
    public static final String COL_DIRTY = "dirty";
    public static final String COL_DELETED = "deleted";
    public static final String COL_ICP = "ICP";
    public static final String COL_DATUM = "DATUM";
    public static final String COL_KOD_PO = "KOD_PO";
    public static final String COL_DRUH = "DRUH";
    public static final String COL_CAS = "CAS";
    public static final String COL_IC_OBS = "IC_OBS";
    public static final String COL_TYP = "TYP";
    public static final String COL_DATUM_ZMENY = "DATUM_ZMENY";
    public static final String COL_POZNAMKA = "POZNAMKA";

    private static int COL_NUM_ID = 0;
    private static int COL_NUM_SERVER_ID = 1;
    private static int COL_NUM_DIRTY = 2;
    private static int COL_NUM_DELETED = 3;
    private static int COL_NUM_ICP = 4;
    private static int COL_NUM_DATUM = 5;
    private static int COL_NUM_KOD_PO = 6;
    private static int COL_NUM_DRUH = 7;
    private static int COL_NUM_CAS = 8;
    private static int COL_NUM_IC_OBS = 9;
    private static int COL_NUM_TYP = 10;
    private static int COL_NUM_DATUM_ZMENY = 11;
    private static int COL_NUM_POZNAMKA = 12;

    public static final String JSON_SERVER_ID = "si";
    public static final String JSON_DELETED = "de";
    public static final String JSON_SYNC = "sy";


    //TODO  EnumMap?
    /*public enum Codes {
        KOD_PO_ARRIVE_NORMAL("00", 0),
        KOD_PO_ARRIVE_PRIVATE("10", 1),
        KOD_PO_LEAVE_SERVICE("01", 2),
        KOD_PO_LEAVE_LUNCH("02", 3),
        KOD_PO_LEAVE_SUPPER("03", 4),
        KOD_PO_LEAVE_MEDIC("04", 5);

        private final String name;
        private final int index;

        private Codes(String s, int i) {
            name = s;
            index = i;
        }
    }*/

    /*public static final String[] KOD_PO_VALUES = {"00", "01", "02", "03", "04", "05",
            "06", "07", "08", "09", "10", "11"};*/

    public static final String[] KOD_PO_VALUES = {"00", "01", "02", "03", "04", "10", "XX"};

    public static final String DRUH_ARRIVAL = "P";
    public static final String DRUH_LEAVE = "O";
    public static final String KOD_PO_ARRIVE_NORMAL = "00";
    public static final String KOD_PO_LEAVE_SERVICE = "01";
    public static final String KOD_PO_LEAVE_LUNCH = "02";
    public static final String KOD_PO_LEAVE_SUPPER = "03";
    public static final String KOD_PO_LEAVE_MEDIC = "04";
    public static final String KOD_PO_ARRIVE_PRIVATE = "10";
    public static final String KOD_PO_OTHERS = "XX";

   /* public static final String KOD_PO_LEAVE_ILL = "05";
    public static final String KOD_PO_LEAVE_VAC = "06";
    public static final String KOD_PO_LEAVE_TREAT = "07";
    public static final String KOD_PO_LEAVE_STUDY = "08";
    public static final String KOD_PO_LEAVE_REFUND = "09";
    public static final String KOD_PO_ARRIVE_EMERGENCY = "11";*/

    public static final int IND_NORMAL = 0, IND_SERVICE = 1,
            IND_LUNCH = 2, IND_SUPPER = 3, IND_MEDIC = 4, IND_PRIVATE = 5, IND_OTHERS = 6;
   /*
    public static final int IND_NORMAL = 0, IND_SERVICE = 1,
            IND_LUNCH = 2, IND_SUPPER = 3, IND_MEDIC = 4, IND_ILL = 5, IND_VAC = 6, IND_TREAT = 7,
            IND_STUDY = 8, IND_REFUND = 9, IND_PRIVATE = 10, IND_EMERGENCY = 11;*/

    public static final String TYPE_ORIG = "O";

    public static final String KEY_DATE = "date";
    public static final String OTHERS = "Ostatni";

}
