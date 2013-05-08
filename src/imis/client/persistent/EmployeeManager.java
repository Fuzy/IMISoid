package imis.client.persistent;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import imis.client.model.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 6.4.13
 * Time: 18:03
 */
public class EmployeeManager {
    private static final String TAG = "EmployeeManager";


    public static int addEmployee(Context context, Employee employee) {
        Log.d(TAG, "addEmployee() " + employee);
        ContentValues values = employee.asContentValues();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(EmployeeQuery.CONTENT_URI, values);
        return Integer.valueOf(uri.getLastPathSegment());
    }

   /* public static int updateEmployee(Context context, Employee employee) {
        Log.d(TAG, "updateEmployee()");
        ContentValues values = employee.asContentValues();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(EmployeeQuery.CONTENT_URI, String.valueOf(employee.get_id()));
        int updated = resolver.update(uri, values, null, null);
        Log.d(TAG, "updateEmployee() updated " + updated);
        return updated;
    }*/

    public static int updateEmployeeOnIcp(Context context, Employee employee) {
        Log.d(TAG, "updateEmployeeOnIcp()" + "context = [" + context + "], employee = [" + employee + "]");
        ContentValues values = employee.asContentValues();
        ContentResolver resolver = context.getContentResolver();
        //TODO nejdriv query a ulozit id
        Employee employee1 = EmployeeManager.getEmployee(context, employee.getIcp());
        if (employee1 == null) return 0;
        long id = employee1.get_id();
        Uri uri = Uri.withAppendedPath(EmployeeQuery.CONTENT_URI, String.valueOf(id));
        int updated = resolver.update(uri, values, null, null);
        Log.d(TAG, "updateEmployee() updated " + updated);
        return updated;
    }

    public static int updateEmployeeWidgetId(Context context, int empId, int widgetId) {
        Log.d(TAG, "updateEmployeeWidgetId()" +
                "context = [" + context + "], empId = [" + empId + "], widgetId = [" + widgetId + "]");
        Uri uri = Uri.withAppendedPath(EmployeeQuery.CONTENT_URI, String.valueOf(empId));
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Employee.COL_WIDGET_ID, widgetId);

        int updated = resolver.update(uri, values, null, null);
        return updated;
    }

    public static int resetEmployeeWidgetId(Context context, int widgetId) {
        Log.d(TAG, "resetEmployeeWidgetId()" + "context = [" + context + "], widgetId = [" + widgetId + "]");
        long id = (EmployeeManager.getEmployee(context, widgetId)).get_id();
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Employee.COL_WIDGET_ID, (Integer) null);

        Uri uri = Uri.withAppendedPath(EmployeeQuery.CONTENT_URI, String.valueOf(id));
        int updated = resolver.update(uri, values, null, null);
        return updated;
    }

    public static void addEmployees(Context context, Employee[] employees) {
        Log.d(TAG, "addEmployees()");
        for (Employee employee : employees) {
            Log.d(TAG, "addEmployees() employee " + employee);
            if (EmployeeManager.updateEmployeeOnIcp(context, employee) == 0)
                EmployeeManager.addEmployee(context, employee);
        }
    }

    public static Employee getEmployee(Context context, String icp) {      //TODO spolecnou metodu
        Log.d(TAG, "getEmployee()" + "context = [" + context + "], id = [" + icp + "]");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EmployeeQuery.CONTENT_URI, null,
                EmployeeQuery.SELECTION_ICP, new String[]{String.valueOf(icp)}, null);
        Employee employee = null;
        while (cursor.moveToNext()) {
            employee = Employee.cursorToEmployee(cursor);
        }
        return employee;
    }

    public static Employee getEmployee(Context context, int widgetId) {  //TODO spolecnou metodu
        Log.d(TAG, "getEmployee()" + "context = [" + context + "], widgetId = [" + widgetId + "]");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EmployeeQuery.CONTENT_URI, null,
                EmployeeQuery.SELECTION_WIDGET_ID, new String[]{String.valueOf(widgetId)}, null);
        Employee employee = null;
        while (cursor.moveToNext()) {
            employee = Employee.cursorToEmployee(cursor);
        }
        return employee;
    }

    public static Employee getEmployee(Context context, long id) { //TODO spolecnou metodu
        Log.d(TAG, "getEmployee()" + "context = [" + context + "], id = [" + id + "]");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EmployeeQuery.CONTENT_URI, null,
                EmployeeQuery.SELECTION_ID, new String[]{String.valueOf(id)}, null);
        Employee employee = null;
        while (cursor.moveToNext()) {
            employee = Employee.cursorToEmployee(cursor);
        }
        return employee;
    }

    public static List<Employee> getAllEmployees(Context context) {
        Log.d(TAG, "getAllEmployees()");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EmployeeQuery.CONTENT_URI, null, null, null, null);
        List<Employee> employees = new ArrayList<>();
        Employee employee;
        while (cursor.moveToNext()) {
            employee = Employee.cursorToEmployee(cursor);
            employees.add(employee);
        }
        cursor.close();
        return employees;
    }

    final public static class EmployeeQuery {

        public static final Uri CONTENT_URI = Uri.parse(Consts.SCHEME + Consts.AUTHORITY + "/"
                + MyDatabaseHelper.TABLE_EMPLOYEES);

        /*public static final String[] PROJECTION_ALL = {Employee.COL_ID, Employee.COL_ICP,
                Employee.COL_KODPRA, Employee.COL_SUB};*/

        public static final String SELECTION_ICP = Employee.COL_ICP + "=?";

        public static final String SELECTION_ID = Employee.COL_ID + "=?";
        public static final String SELECTION_WIDGET_ID = Employee.COL_WIDGET_ID + "=?";

    }
}
