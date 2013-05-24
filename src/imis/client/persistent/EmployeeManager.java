package imis.client.persistent;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.authentication.AuthenticationConsts;
import imis.client.model.Employee;

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
    private static final String TAG = "EmployeeManager";

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
        Employee employee1 = EmployeeManager.getEmployee(context, employee.getIcp());
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
        long id = (EmployeeManager.getEmployee(context, widgetId)).get_id();
        ContentValues values = new ContentValues();
        values.put(Employee.COL_WIDGET_ID, (Integer) null);
        return updateEmployee(context, values, id);
    }

    public static void syncEmployees(Context context, Employee[] employees) {
        Log.d(TAG,"syncEmployees() employees " + Arrays.toString(employees));
        List<Employee> currentList = getAllEmployees(context);
        Log.d(TAG, "syncEmployees() currentList " + currentList.size());
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(AuthenticationConsts.ACCOUNT_TYPE);
        try {
            String icp = accountManager.getUserData(accounts[0], AuthenticationConsts.KEY_ICP);
            Employee user = new Employee();
            user.setIcp(icp);
            currentList.remove(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "syncEmployees() currentList " + currentList.size());


        for (Employee employee : employees) {
            Log.d(TAG, "syncEmployees() employee " + employee);
            if (EmployeeManager.updateEmployeeOnIcp(context, employee) == 0)
                EmployeeManager.addEmployee(context, employee);

            currentList.remove(employee);
        }
        Log.d(TAG, "syncEmployees() currentList " + currentList.size());
        // delete all which has not been actualised
        for (Employee employee : currentList) {
            deleteEvent(context, employee.get_id());
        }//TODO test
    }

    public static Employee getEmployee(Context context, String icp) {
        Log.d(TAG, "getEmployee()" + "icp = [" + icp + "]");
        return getEmployee(context, EmployeeQuery.SELECTION_ICP, new String[]{String.valueOf(icp)});
    }

    public static Employee getEmployee(Context context, int widgetId) {
        Log.d(TAG, "getEmployee()" + "widgetId = [" + widgetId + "]");
        return getEmployee(context, EmployeeQuery.SELECTION_WIDGET_ID, new String[]{String.valueOf(widgetId)});
    }

    public static Employee getEmployee(Context context, long id) {
        Log.d(TAG, "getEmployee()" + "id = [" + id + "]");
        return getEmployee(context, EmployeeQuery.SELECTION_ID, new String[]{String.valueOf(id)});
    }

    public static Employee getEmployeeOnKodpra(Context context, String kodpra) {
        Log.d(TAG, "getEmployee()" + "kodpra = [" + kodpra + "]");
        return getEmployee(context, EmployeeQuery.SELECTION_KODPRA, new String[]{kodpra});
    }

    private static Employee getEmployee(Context context, String selection, String[] selectionArgs) {
        Log.d(TAG, "getEmployee()" + "selection = [" + selection + "], selectionArgs = [" + Arrays.toString(selectionArgs) + "]");
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
        Log.d(TAG,"getEmployees()" + "selection = [" + selection + "], selectionArgs = [" + selectionArgs + "]");
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

    public static int deleteEvent(Context context, long id) {
        Log.d(TAG, "delete()" + "id = [" + id + "]");
        Uri uri = Uri.withAppendedPath(EmployeeQuery.CONTENT_URI, String.valueOf(id));
        ContentResolver resolver = context.getContentResolver();
        return resolver.delete(uri, null, null);
    }

    final public static class EmployeeQuery {

        public static final Uri CONTENT_URI = Uri.parse(Consts.SCHEME + AppConsts.AUTHORITY2 + "/"
                + MyDatabaseHelper.TABLE_EMPLOYEES);

        public static final String SELECTION_ICP = Employee.COL_ICP + "=?";
        public static final String SELECTION_ID = Employee.COL_ID + "=?";
        public static final String SELECTION_KODPRA = Employee.COL_KODPRA + "=?";
        public static final String SELECTION_WIDGET_ID = Employee.COL_WIDGET_ID + "=?";
        public static final String SELECTION_WIDGET_NOT_NULL = Employee.COL_WIDGET_ID + " is not null";

        public static final String ORDER_BY_USER = Employee.COL_USER + "=1 DESC";
        public static final String ORDER_BY_PRESENT = Employee.COL_DRUH + "='P' DESC";
        public static final String ORDER_BY_FAV = Employee.COL_FAV + "=1 DESC";
        public static final String ORDER_BY_KOD = Employee.COL_KODPRA + " ASC";
        public static final String ORDER_BY = ORDER_BY_USER + "," + ORDER_BY_PRESENT +
                "," + ORDER_BY_FAV + "," + ORDER_BY_KOD;

    }
}
