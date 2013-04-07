package imis.client.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.google.gson.JsonObject;
import imis.client.json.Util;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 6.4.13
 * Time: 15:59
 */
public class Employee {
    private int _id;
    private String icp;
    private String kodpra;
    private boolean isSubordinate;
    private long lastEventTime;
    private String kod_po;

    public Employee() {
    }

    public Employee(String icp, String kodpra, boolean subordinate, long lastEventTime, String kod_po) {
        this.icp = icp;
        this.kodpra = kodpra;
        isSubordinate = subordinate;
        this.lastEventTime = lastEventTime;
        this.kod_po = kod_po;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getIcp() {
        return icp;
    }

    public void setIcp(String icp) {
        this.icp = icp;
    }

    public String getKodpra() {
        return kodpra;
    }

    public void setKodpra(String kodpra) {
        this.kodpra = kodpra;
    }

    public boolean isSubordinate() {
        return isSubordinate;
    }

    public void setSubordinate(boolean isSubordinate) {
        this.isSubordinate = isSubordinate;
    }

    public long getLastEventTime() {
        return lastEventTime;
    }

    public void setLastEventTime(long lastEventTime) {
        this.lastEventTime = lastEventTime;
    }

    public String getKod_po() {
        return kod_po;
    }

    public void setKod_po(String kod_po) {
        this.kod_po = kod_po;
    }

    public static Employee jsonToEmployee(JsonObject object) {
        Employee employee = Util.gson.fromJson(object, Employee.class);
        return employee;
    }

    public static String getAsJson(Employee employee) {
        return Util.gson.toJson(employee);
    }

    public ContentValues getAsContentValues() {
        ContentValues values = new ContentValues();
        values.put(COL_ICP, icp);
        values.put(COL_KODPRA, kodpra);
        values.put(COL_SUB, isSubordinate);
        return values;
    }

    public static Employee cursorToEmployee(Cursor c) {
        Employee employee = new Employee();
        employee.set_id(c.getInt(IND_COL_ID));
        employee.setIcp(c.getString(IND_COL_ICP));
        employee.setKodpra(c.getString(IND_COL_KODPRA));
        employee.setSubordinate(c.getInt(IND_COL_SUB) > 0);
        return employee;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "_id=" + _id +
                ", icp='" + icp + '\'' +
                ", kodpra='" + kodpra + '\'' +
                ", isSubordinate=" + isSubordinate +
                ", lastEventTime=" + lastEventTime +
                ", kod_po='" + kod_po + '\'' +
                '}';
    }

    public static final String COL_ID = "_id";
    public static final String COL_ICP = "ICP";
    public static final String COL_KODPRA = "KODPRA";
    public static final String COL_SUB = "SUB";

    public static final int IND_COL_ID = 0;
    public static final int IND_COL_ICP = 1;
    public static final int IND_COL_KODPRA = 2;
    public static final int IND_COL_SUB = 3;


}
