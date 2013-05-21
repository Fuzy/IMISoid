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
    private String name;
    private Boolean isSubordinate;
    private Long lastEventTime;
    private String kod_po;
    private String druh;
    private Integer widgetId;
    private Boolean isFav;
    private Boolean isUser;

    public Employee() {
    }

    public Employee(String icp, String kodpra, Boolean subordinate, Boolean fav, Boolean user) {
        this.icp = icp;
        this.kodpra = kodpra;
        isSubordinate = subordinate;
        isFav = fav;
        isUser = user;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isSubordinate() {
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

    public Boolean isFav() {
        return isFav;
    }

    public void setFav(Boolean fav) {
        isFav = fav;
    }

    public Boolean getUser() {
        return isUser;
    }

    public void setUser(Boolean user) {
        isUser = user;
    }

    public ContentValues asContentValues() {
        ContentValues values = new ContentValues();
        values.put(COL_ICP, icp);
        if (kodpra != null) {
            values.put(COL_KODPRA, kodpra);
        }
        if (name != null) {
            values.put(COL_JMENO, name);
        }
        if (isSubordinate == null) {
            setSubordinate(false);
        }
        values.put(COL_SUB, isSubordinate);
        if (lastEventTime != null) {
            values.put(COL_TIME, lastEventTime);
        }
        if (kod_po != null) {
            values.put(COL_KOD_PO, kod_po);
        }
        if (druh != null) {
            values.put(COL_DRUH, druh);
        }
        if (isFav == null) {
            setFav(false);
        }
        values.put(COL_FAV, isFav);
        if (isUser == null) {
            setUser(false);
        }
        values.put(COL_USER, isUser);

        Log.d(TAG, "asContentValues() values " + values);
        return values;
    }

    public static Employee cursorToEmployee(Cursor c) {
        //Log.d(TAG, "cursorToEmployee()  " + Arrays.toString(c.getColumnNames()));
        Employee employee = new Employee();
        employee.set_id(c.getInt(IND_COL_ID));
        employee.setIcp(c.getString(IND_COL_ICP));
        if (!c.isNull(IND_COL_KODPRA)) employee.setKodpra(c.getString(IND_COL_KODPRA));
        if (!c.isNull(IND_COL_JMENO)) employee.setName(c.getString(IND_COL_JMENO));
        employee.setSubordinate(c.getInt(IND_COL_SUB) > 0);
        if (!c.isNull(IND_COL_KOD_PO)) employee.setKod_po(c.getString(IND_COL_KOD_PO));
        if (!c.isNull(IND_COL_TIME)) employee.setLastEventTime(c.getLong(IND_COL_TIME));
        if (!c.isNull(IND_COL_DRUH)) employee.setDruh(c.getString(IND_COL_DRUH));
        if (!c.isNull(IND_COL_WIDGET_ID)) employee.setWidgetId(c.getInt(IND_COL_WIDGET_ID));
        employee.setFav(c.getInt(IND_COL_FAV) > 0);
        employee.setUser(c.getInt(IND_COL_USER) > 0);

        Log.d(TAG, "cursorToEmployee() employee " + employee);
        return employee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employee employee = (Employee) o;

        if (!icp.equals(employee.icp)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return icp.hashCode();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "_id=" + _id +
                ", icp='" + icp + '\'' +
                ", kodpra='" + kodpra + '\'' +
                ", name='" + name + '\'' +
                ", isSubordinate=" + isSubordinate +
                ", lastEventTime=" + lastEventTime +
                ", kod_po='" + kod_po + '\'' +
                ", druh='" + druh + '\'' +
                ", widgetId=" + widgetId +
                ", isFav=" + isFav +
                ", isUser=" + isUser +
                '}';
    }

    public static final String COL_ID = "_id";
    public static final String COL_ICP = "ICP";
    public static final String COL_KODPRA = "KODPRA";
    public static final String COL_JMENO = "JMENO";
    public static final String COL_SUB = "SUB";
    public static final String COL_DRUH = "DRUH";
    public static final String COL_TIME = "TIME";
    public static final String COL_KOD_PO = "KOD";
    public static final String COL_WIDGET_ID = "WIDGET_ID";
    public static final String COL_FAV = "FAV";
    public static final String COL_USER = "USER";

    public static final int IND_COL_ID = 0;
    public static final int IND_COL_ICP = 1;
    public static final int IND_COL_KODPRA = 2;
    public static final int IND_COL_JMENO = 3;
    public static final int IND_COL_SUB = 4;
    public static final int IND_COL_DRUH = 5;
    public static final int IND_COL_TIME = 6;
    public static final int IND_COL_KOD_PO = 7;
    public static final int IND_COL_WIDGET_ID = 8;
    public static final int IND_COL_FAV = 9;
    public static final int IND_COL_USER = 10;


}
