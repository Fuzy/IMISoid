package imis.client.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.gson.JsonObject;
import imis.client.json.Util;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Record {
    private String id;//BigDecimal
    private long datum;
    private String kodpra;
    private String zc;
    private String stav_v;
    private int cpolzak;
    private int cpozzak;
    private long mnozstvi_odved;
    private String pozn_hl;
    private String pozn_ukol;
    private String poznamka;

    public Record() {
    }

    public Record(String id, long datum, String kodpra, String zc, String stav_v, int cpolzak,
                  int cpozzak, long mnozstvi_odved, String pozn_hl, String pozn_ukol, String poznamka) {
        super();
        this.id = id;
        this.datum = datum;
        this.kodpra = kodpra;
        this.zc = zc;
        this.stav_v = stav_v;
        this.cpolzak = cpolzak;
        this.cpozzak = cpozzak;
        this.mnozstvi_odved = mnozstvi_odved;
        this.pozn_hl = pozn_hl;
        this.pozn_ukol = pozn_ukol;
        this.poznamka = poznamka;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZc() {
        return zc;
    }

    public void setZc(String zc) {
        this.zc = zc;
    }

    public String getStav_v() {
        return stav_v;
    }

    public void setStav_v(String stav_v) {
        this.stav_v = stav_v;
    }

    public int getCpolzak() {
        return cpolzak;
    }

    public void setCpolzak(int cpolzak) {
        this.cpolzak = cpolzak;
    }

    public int getCpozzak() {
        return cpozzak;
    }

    public void setCpozzak(int cpozzak) {
        this.cpozzak = cpozzak;
    }

    public long getMnozstvi_odved() {
        return mnozstvi_odved;
    }

    public void setMnozstvi_odved(long mnozstvi_odved) {
        this.mnozstvi_odved = mnozstvi_odved;
    }

    public String getPozn_hl() {
        return pozn_hl;
    }

    public void setPozn_hl(String pozn_hl) {
        this.pozn_hl = pozn_hl;
    }

    public String getPozn_ukol() {
        return pozn_ukol;
    }

    public void setPozn_ukol(String pozn_ukol) {
        this.pozn_ukol = pozn_ukol;
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }

    public long getDatum() {
        return datum;
    }

    public void setDatum(long datum) {
        this.datum = datum;
    }

    public String getKodpra() {
        return kodpra;
    }

    public void setKodpra(String kodpra) {
        this.kodpra = kodpra;
    }

    public static Record resultSetToRecord(ResultSet rsSet) throws SQLException {
        Record record = new Record();
        record.setId(rsSet.getString(REC_COL_SERVER_ID));
        record.setDatum(rsSet.getLong(REC_COL_DATUM));
        record.setMnozstvi_odved(rsSet.getLong(REC_COL_MNOZSTVI_ODVED));
        record.setKodpra(rsSet.getString(REC_COL_KODPRA));
        record.setZc(rsSet.getString(REC_COL_ZC));//TODO ciselne typy jsou ok?
        record.setCpolzak(rsSet.getInt(REC_COL_CPOLZAK));
        record.setCpozzak(rsSet.getInt(REC_COL_CPOZZAK));
        record.setStav_v(rsSet.getString(REC_COL_STAV_V));
        record.setPozn_hl(rsSet.getString(REC_COL_POZN_HL));
        record.setPozn_ukol(rsSet.getString(REC_COL_POZN_UKOL));
        record.setPoznamka(rsSet.getString(REC_COL_POZNAMKA));
        return record;
    }

    public ContentValues getAsContentValues() {
        //TODO asi tam dat jen to spolecne pro add a update
        ContentValues values = new ContentValues();
        if (id != null) {
            values.put(REC_COL_SERVER_ID, id);
        }
        values.put(REC_COL_DATUM, (datum));
        if (kodpra != null) {
            values.put(REC_COL_KODPRA, kodpra);
        }
        if (zc != null) {
            values.put(REC_COL_ZC, zc);
        }
        if (stav_v != null) {
            values.put(REC_COL_STAV_V, stav_v);
        }
        values.put(REC_COL_CPOLZAK, cpolzak);
        values.put(REC_COL_CPOZZAK, cpozzak);
        values.put(REC_COL_MNOZSTVI_ODVED, mnozstvi_odved);
        if (pozn_hl != null) {
            values.put(REC_COL_POZN_HL, pozn_hl);
        }
        if (pozn_ukol != null) {
            values.put(REC_COL_POZN_UKOL, pozn_ukol);
        }
        if (poznamka != null) {
            values.put(REC_COL_POZNAMKA, poznamka);
        }
        return values;
    }

    public static Record jsonToRecord(JsonObject object) {
        Record record = Util.gson.fromJson(object, Record.class);
        return record;
    }

    public static String getAsJson(Record record) {
        return Util.gson.toJson(record);
    }

    @Override
    public String toString() {
        return "Record [id=" + id + ", datum=" + datum + ", kodpra=" + kodpra + ", zc=" + zc
                + ", stav_v=" + stav_v + ", cpolzak=" + cpolzak + ", cpozzak=" + cpozzak
                + ", mnozstvi_odved=" + mnozstvi_odved + ", pozn_hl=" + pozn_hl + ", pozn_ukol="
                + pozn_ukol + ", poznamka=" + poznamka + "]";
    }

    public static final String REC_COL_POZN_UKOL = "POZN_UKOL";
    public static final String REC_COL_SERVER_ID = "ID";
    public static final String REC_COL_DATUM = "DATUM";
    public static final String REC_COL_KODPRA = "KODPRA";
    public static final String REC_COL_MNOZSTVI_ODVED = "MNOZSTVI_ODVED";
    public static final String REC_COL_POZNAMKA = "POZNAMKA";
    public static final String REC_COL_STAV_V = "STAV_V";
    public static final String REC_COL_POZN_HL = "POZN_HL";
    public static final String REC_COL_ZC = "ZC";
    public static final String REC_COL_CPOLZAK = "CPOLZAK";
    public static final String REC_COL_CPOZZAK = "CPOZZAK";

}
