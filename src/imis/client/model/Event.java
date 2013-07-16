package imis.client.model;

import android.content.ContentValues;
import android.database.Cursor;
import imis.client.TimeUtil;
import org.codehaus.jackson.annotate.JsonIgnore;

public class Event {
    // private static final String TAG = Event.class.getSimpleName();
    // sync
    @JsonIgnore
    private int _id;
    private String server_id;
    @JsonIgnore
    private boolean dirty;
    @JsonIgnore
    private boolean deleted;
    @JsonIgnore
    private boolean error;
    @JsonIgnore
    private String msg;

    // data
    private String icp;
    private long datum;
    private String kod_po;
    private String druh;
    private long cas;
    private String ic_obs;
    private String typ;
    private long datum_zmeny;
    private String poznamka;


    public Event() {
        this.setCas(TimeUtil.currentDayTimeInLong());
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

    public Event(Event other) {
        this._id = other._id;
        this.server_id = other.server_id;
        this.dirty = other.dirty;
        this.deleted = other.deleted;
        this.error = other.error;
        this.msg = other.msg;
        this.icp = other.icp;
        this.datum = other.datum;
        this.kod_po = other.kod_po;
        this.druh = other.druh;
        this.cas = other.cas;
        this.ic_obs = other.ic_obs;
        this.typ = other.typ;
        this.datum_zmeny = other.datum_zmeny;
        this.poznamka = other.poznamka;
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

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public boolean hasServer_id() {
        if (server_id == null) return false;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (_id != event._id) return false;
        if (cas != event.cas) return false;
        if (datum != event.datum) return false;
        if (datum_zmeny != event.datum_zmeny) return false;
        if (deleted != event.deleted) return false;
        if (dirty != event.dirty) return false;
        if (error != event.error) return false;
        if (druh != null ? !druh.equals(event.druh) : event.druh != null) return false;
        if (ic_obs != null ? !ic_obs.equals(event.ic_obs) : event.ic_obs != null) return false;
        if (icp != null ? !icp.equals(event.icp) : event.icp != null) return false;
        if (kod_po != null ? !kod_po.equals(event.kod_po) : event.kod_po != null) return false;
        if (msg != null ? !msg.equals(event.msg) : event.msg != null) return false;
        if (poznamka != null ? !poznamka.equals(event.poznamka) : event.poznamka != null) return false;
        if (server_id != null ? !server_id.equals(event.server_id) : event.server_id != null) return false;
        if (typ != null ? !typ.equals(event.typ) : event.typ != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _id;
        result = 31 * result + (int) (datum_zmeny ^ (datum_zmeny >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "_id=" + _id +
                ", server_id='" + server_id + '\'' +
                ", dirty=" + dirty +
                ", deleted=" + deleted +
                ", icp='" + icp + '\'' +
                ", datum=" + TimeUtil.formatDate(datum) +
                ", datum=" + datum +
                ", kod_po='" + kod_po + '\'' +
                ", druh='" + druh + '\'' +
                ", cas=" + TimeUtil.formatTimeInNonLimitHour(cas) +
                ", cas=" + cas +
                ", ic_obs='" + ic_obs + '\'' +
                ", typ='" + typ + '\'' +
                ", datum_zmeny=" + TimeUtil.formatDate(datum_zmeny) +
                ", poznamka='" + poznamka + '\'' +
                ", error=" + error +
                ", msg='" + msg + '\'' +
                '}';
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
        event.setError(c.getInt(COL_NUM_ERROR) > 0);
        event.setMsg(c.getString(COL_NUM_MSG));
        return event;
    }

    public ContentValues asContentValues() {
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
        values.put(COL_ERROR, error);
        if (msg != null) {
            values.put(COL_MSG, msg);
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
    public static final String COL_ERROR = "ERROR";
    public static final String COL_MSG = "MSG";

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
    private static int COL_NUM_ERROR = 13;
    private static int COL_NUM_MSG = 14;

    public static final String JSON_SERVER_ID = "si";
    public static final String JSON_DELETED = "de";
    public static final String JSON_SYNC = "sy";


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


    public static final String TYPE_ORIG = "O";

    public static final String KEY_DATE = "date";

}
