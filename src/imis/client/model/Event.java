package imis.client.model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Event {
  private String icp;
  private Date datum;
  private String kod_po;
  private String druh;
  private float cas;
  private String ic_obs;
  private String typ;
  private Date datum_zmeny;
  private String poznamka;
  
  public Event() {
  }

  public Event(String icp, Date datum, String kod_po, String druh, float cas,
      String ic_obs, String typ, Date datum_zmeny, String poznamka) {
    super();
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

  public float getCas() {
    return cas;
  }

  public void setCas(float cas) {
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
    return "Event [icp=" + icp + ", datum=" + datum + ", kod_po=" + kod_po
        + ", druh=" + druh + ", cas=" + cas + ", ic_obs=" + ic_obs
        + ", typ=" + typ + ", datum_zmeny=" + datum_zmeny
        + ", poznamka=" + poznamka + "]";
  }
  
  public static Event resultSetToEvent(ResultSet rsSet) throws SQLException {
    Event event = new Event();
    event.setIcp(rsSet.getString(COL_ICP));
    event.setDatum(rsSet.getDate(COL_DATUM));
    event.setKod_po(rsSet.getString(COL_KOD_PO));
    event.setDruh(rsSet.getString(COL_DRUH));
    event.setCas(rsSet.getFloat(COL_CAS));
    event.setIc_obs(rsSet.getString(COL_IC_OBS));
    event.setTyp(rsSet.getString(COL_TYP));
    event.setDatum_zmeny(rsSet.getDate(COL_DATUM_ZMENY));
    event.setPoznamka(rsSet.getString(COL_POZNAMKA));
    return event;
  }
  
  private static String COL_ICP = "ICP";
  private static String COL_DATUM = "DATUM";
  private static String COL_KOD_PO = "KOD_PO";
  private static String COL_DRUH = "DRUH";
  private static String COL_CAS = "CAS";
  private static String COL_IC_OBS = "IC_OBS";
  private static String COL_TYP = "TYP";
  private static String COL_DATUM_ZMENY = "DATUM_ZMENY";
  private static String COL_POZNAMKA = "POZNAMKA";
  
  

}
