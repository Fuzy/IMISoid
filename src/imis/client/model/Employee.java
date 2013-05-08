package imis.client.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 6.4.13
 * Time: 15:59
 */
public class Employee {
    private static final String TAG = Employee.class.getSimpleName();

    private int _id;
    private String icp;
    private String kodpra;
    private Boolean isSubordinate;
    private Long lastEventTime;
    private String kod_po;
    private String druh;
    private Integer widgetId;

    public Employee() {
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

    public Boolean getSubordinate() {
        return isSubordinate;
    }

    public void setSubordinate(Boolean subordinate) {
        isSubordinate = subordinate;
    }

    public Long getLastEventTime() {
        return lastEventTime;
    }

    public void setLastEventTime(Long lastEventTime) {
        this.lastEventTime = lastEventTime;
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

    public Integer getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(Integer widgetId) {
        this.widgetId = widgetId;
    }

    public ContentValues asContentValues() {
        ContentValues values = new ContentValues();
        values.put(COL_ICP, icp);
        if (kodpra != null) {
            values.put(COL_KODPRA, kodpra);
        }
        if (isSubordinate == null) {
            setSubordinate(false);
        }
        values.put(COL_SUB, false);
        if (lastEventTime != null) {
            values.put(COL_TIME, lastEventTime);
        }
        if (kod_po != null) {
            values.put(COL_KOD_PO, kod_po);
        }
        if (druh != null) {
            values.put(COL_DRUH, druh);
        }
       /* if (widgetId != null) {
            values.put(COL_WIDGET_ID, widgetId);
        }*/

        Log.d(TAG, "asContentValues() values " + values);
        return values;
    }

    public static Employee cursorToEmployee(Cursor c) {
        //Log.d(TAG, "cursorToEmployee()  " + Arrays.toString(c.getColumnNames()));
        Employee employee = new Employee();
        employee.set_id(c.getInt(IND_COL_ID));
        employee.setIcp(c.getString(IND_COL_ICP));
        if (!c.isNull(IND_COL_KODPRA)) employee.setKodpra(c.getString(IND_COL_KODPRA));
        employee.setSubordinate(c.getInt(IND_COL_SUB) > 0);
        if (!c.isNull(IND_COL_KOD_PO)) employee.setKod_po(c.getString(IND_COL_KOD_PO));
        if (!c.isNull(IND_COL_TIME)) employee.setLastEventTime(c.getLong(IND_COL_TIME));
        if (!c.isNull(IND_COL_DRUH)) employee.setDruh(c.getString(IND_COL_DRUH));
        if (!c.isNull(IND_COL_WIDGET_ID)) employee.setWidgetId(c.getInt(IND_COL_WIDGET_ID));
        Log.d(TAG, "cursorToEmployee() employee " + employee);
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
                ", druh='" + druh + '\'' +
                ", widgetId='" + widgetId + '\'' +
                '}';
    }

    public static final String COL_ID = "_id";
    public static final String COL_ICP = "ICP";
    public static final String COL_KODPRA = "KODPRA";
    public static final String COL_SUB = "SUB";
    public static final String COL_DRUH = "DRUH";
    public static final String COL_TIME = "TIME";
    public static final String COL_KOD_PO = "KOD";
    public static final String COL_WIDGET_ID = "WIDGET_ID";

    public static final int IND_COL_ID = 0;
    public static final int IND_COL_ICP = 1;
    public static final int IND_COL_KODPRA = 2;
    public static final int IND_COL_SUB = 3;
    public static final int IND_COL_DRUH = 4;
    public static final int IND_COL_TIME = 5;
    public static final int IND_COL_KOD_PO = 6;
    public static final int IND_COL_WIDGET_ID = 7;


}
