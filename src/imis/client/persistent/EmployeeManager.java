package imis.client.persistent;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import imis.client.AccountUtil;
import imis.client.AppConsts;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.model.Employee;
import imis.client.widget.EmployeeWidgetProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 6.4.13
 * Time: 18:03
 */
public class EmployeeManager {
    private static final String TAG = EmployeeManager.class.getSimpleName();

    public static int addEmployee(Context context, Employee employee) {
        Log.d(TAG, "addEmployee() " + employee);
        ContentValues values = employee.asContentValues();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(EmployeeQuery.CONTENT_URI, values);
        return Integer.valueOf(uri.getLastPathSegment());
    }

    private static int updateEmployee(Context context, ContentValues values, long id) {
        Log.d(TAG, "updateEmployee()" + "values = [" + values + "], id = [" + id + "]");
        Uri uri = Uri.withAppendedPath(EmployeeQuery.CONTENT_URI, String.valueOf(id));
        ContentResolver resolver = context.getContentResolver();
        int updated = resolver.update(uri, values, null, null);
        Log.d(TAG, "updateEmployee() updated " + updated);
        return updated;
    }

    public static int updateEmployeeOnIcp(Context context, Employee employee) {
        Log.d(TAG, "updateEmployeeOnIcp()" + "employee = [" + employee + "]");
        Employee employee1 = EmployeeManager.getEmployeeOnIcp(context, employee.getIcp());
        if (employee1 == null) return 0;
        ContentValues values = employee.asContentValues();
        return updateEmployee(context, values, employee1.get_id());
    }

    public static int updateEmployeeWidgetId(Context context, int empId, int widgetId) {
        Log.d(TAG, "updateEmployeeWidgetId()" + "empId = [" + empId + "], widgetId = [" + widgetId + "]");
        ContentValues values = new ContentValues();
        values.put(Employee.COL_WIDGET_ID, widgetId);
        return updateEmployee(context, values, empId);
    }

    public static int updateEmployeeIsFav(Context context, int empId, boolean isfav) {
        Log.d(TAG, "updateEmployeeIsFav()" + "empId = [" + empId + "], isfav = [" + isfav + "]");
        ContentValues values = new ContentValues();
        values.put(Employee.COL_FAV, isfav);
        return updateEmployee(context, values, empId);
    }

    public static int resetEmployeeWidgetId(Context context, int widgetId) {
        Log.d(TAG, "resetEmployeeWidgetId()" + "widgetId = [" + widgetId + "]");
        Employee employee = EmployeeManager.getEmployeeOnWidgetId(context, widgetId);
        if (employee == null) return 0;
        long id = employee.get_id();
        ContentValues values = new ContentValues();
        values.put(Employee.COL_WIDGET_ID, (Integer) null);
        return updateEmployee(context, values, id);
    }

    public static void syncEmployees(Context context, Employee[] employees) {
        Log.d(TAG, "syncEmployees() employees " + Arrays.toString(employees));
        List<Employee> currentList = getAllEmployees(context);

        for (Employee employee : employees) {
            if (EmployeeManager.updateEmployeeOnIcp(context, employee) == 0)
                EmployeeManager.addEmployee(context, employee);

            currentList.remove(employee);
        }

        // delete all which has not been actualised
        for (Employee employee : currentList) {
            deleteEmployee(context, employee.get_id());
        }
        markUser(context);
        int size = employees.length;
        AppUtil.showInfo(context, context.getString(R.string.employees_act_ok) + size);

        //update all employees widget
        new EmployeeWidgetProvider().updateAllWidgets(context);
    }

    private static void markUser(Context context) {
        Log.d(TAG, "markUser()");
        try {
            String icp = AccountUtil.getUserICP(context);
            Employee employee = getEmployeeOnIcp(context, icp);
            if (employee != null) {
                int id = employee.get_id();
                ContentValues values = new ContentValues();
                values.put(Employee.COL_USER, true);
                int i = updateEmployee(context, values, id);
                Log.d(TAG, "markUser() icp " + icp + " markUser() i " + i);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    public static Employee getEmployeeOnIcp(Context context, String icp) {
        Log.d(TAG, "getEmployeeOnId()" + "icp = [" + icp + "]");
        return getEmployee(context, EmployeeQuery.SELECTION_ICP, new String[]{String.valueOf(icp)});
    }

    public static Employee getEmployeeOnWidgetId(Context context, int widgetId) {
        Log.d(TAG, "getEmployeeOnId()" + "widgetId = [" + widgetId + "]");
        return getEmployee(context, EmployeeQuery.SELECTION_WIDGET_ID, new String[]{String.valueOf(widgetId)});
    }

    public static Employee getEmployeeOnId(Context context, long id) {
        Log.d(TAG, "getEmployeeOnId()" + "id = [" + id + "]");
        return getEmployee(context, EmployeeQuery.SELECTION_ID, new String[]{String.valueOf(id)});
    }

    /*public static Employee getEmployeeOnKodpra(Context context, String kodpra) {
        Log.d(TAG, "getEmployeeOnId()" + "kodpra = [" + kodpra + "]");
        return getEmployeeOnId(context, EmployeeQuery.SELECTION_KODPRA, new String[]{kodpra});
    }*/

    private static Employee getEmployee(Context context, String selection, String[] selectionArgs) {
        Log.d(TAG, "getEmployeeOnId()" + "selection = [" + selection + "], selectionArgs = [" + Arrays.toString(selectionArgs) + "]");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EmployeeQuery.CONTENT_URI, null,
                selection, selectionArgs, null);
        Employee employee = null;
        while (cursor.moveToNext()) {
            employee = Employee.cursorToEmployee(cursor);
        }
        return employee;
    }

    public static List<Employee> getAllEmployees(Context context) {
        Log.d(TAG, "getAllEmployees()");
        return getEmployees(context, null, null);
    }

    public static List<Employee> getEmployeesWithWidget(Context context) {
        Log.d(TAG, "getAllEmployees()");
        return getEmployees(context, EmployeeQuery.SELECTION_WIDGET_NOT_NULL, null);
    }

    public static List<Employee> getEmployees(Context context, String selection, String[] selectionArgs) {
        Log.d(TAG, "getEmployees()" + "selection = [" + selection + "], selectionArgs = [" + selectionArgs + "]");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(EmployeeQuery.CONTENT_URI, null, selection, selectionArgs, null);
        List<Employee> employees = new ArrayList<>();
        Employee employee;
        while (cursor.moveToNext()) {
            employee = Employee.cursorToEmployee(cursor);
            employees.add(employee);
        }
        cursor.close();
        return employees;
    }

    public static int deleteEmployee(Context context, long id) {
        Log.d(TAG, "delete()" + "id = [" + id + "]");
        Uri uri = Uri.withAppendedPath(EmployeeQuery.CONTENT_URI, String.valueOf(id));
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(uri, null, null);
    }

    final public static class EmployeeQuery {

        public static final Uri CONTENT_URI = Uri.parse(Consts.SCHEME + AppConsts.AUTHORITY2 + "/"
                + MyDatabaseHelper.TABLE_EMPLOYEES);

        private static final String SELECTION_ICP = Employee.COL_ICP + "=?";
        private static final String SELECTION_ID = Employee.COL_ID + "=?";
        private static final String SELECTION_WIDGET_ID = Employee.COL_WIDGET_ID + "=?";
        private static final String SELECTION_WIDGET_NOT_NULL = Employee.COL_WIDGET_ID + " is not null";
        public static final String SELECTION_SUBORDINATES = Employee.COL_SUB + "=1";

        private static final String ORDER_BY_USER = Employee.COL_USER + "=1 DESC";
        private static final String ORDER_BY_PRESENT = Employee.COL_DRUH + "='P' DESC";
        private static final String ORDER_BY_FAV = Employee.COL_FAV + "=1 DESC";
        private static final String ORDER_BY_KOD = Employee.COL_KODPRA + " ASC";
        public static final String ORDER_BY = ORDER_BY_USER + "," + ORDER_BY_PRESENT +
                "," + ORDER_BY_FAV + "," + ORDER_BY_KOD;

    }
}
