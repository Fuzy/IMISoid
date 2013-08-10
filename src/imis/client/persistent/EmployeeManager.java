package imis.client.persistent;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import imis.client.AccountUtil;
import imis.client.AppConsts;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.model.Employee;
import imis.client.widget.EmployeeWidgetProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 6.4.13
 * Time: 18:03
 */
public class EmployeeManager {
    private static final String TAG = EmployeeManager.class.getSimpleName();

    private static ContentProviderOperation addEmployeeOp(Employee employee) {
        ContentValues values = employee.asContentValues();
        return ContentProviderOperation.newInsert(EmployeeQuery.CONTENT_URI).withValues(values).build();
    }

    private static int updateEmployee(Context context, ContentValues values, long id) {
//        Log.d(TAG, "updateEmployee()" + "context = [" + context + "], values = [" + values + "], id = [" + id + "]");
        ContentResolver resolver = context.getContentResolver();
        ContentProviderClient client = resolver.acquireContentProviderClient(EmployeeQuery.CONTENT_URI);
        int updated = updateEmployee(client, values, id);
        client.release();
        return updated;
    }

    private static int updateEmployee(ContentProviderClient client, ContentValues values, long id) {
//        Log.d(TAG, "updateEmployee()" + "client = [" + client + "], values = [" + values + "], id = [" + id + "]");
        Uri uri = Uri.withAppendedPath(EmployeeQuery.CONTENT_URI, String.valueOf(id));
        try {
            int updated = client.update(uri, values, null, null);
            return updated;
        } catch (RemoteException e) {
            return 0;
        }
    }

    public static int updateEmployeeOnIcp(ContentProviderClient client, Employee employee) {
        Log.d(TAG, "updateEmployeeOnIcp()" + "client = [" + client + "], employee = [" + employee + "]");
        ContentValues values = employee.asContentValues();
        try {
            return client.update(EmployeeQuery.CONTENT_URI, values, EmployeeQuery.SELECTION_ICP,
                    new String[]{String.valueOf(employee.getIcp())});
        } catch (RemoteException e) {
            return 0;
        }
    }

    private static ContentProviderOperation updateEmployeeOnIcpOp(Employee employee) {
        ContentValues values = employee.asContentValues();
        return ContentProviderOperation.newUpdate(EmployeeQuery.CONTENT_URI).withSelection(EmployeeQuery.SELECTION_ICP,
                new String[]{String.valueOf(employee.getIcp())}).withValues(values).build();
    }

    public static int updateEmployeeWidgetId(Context context, int empId, int widgetId) {
//        Log.d(TAG, "updateEmployeeWidgetId()" + "empId = [" + empId + "], widgetId = [" + widgetId + "]");
        ContentValues values = new ContentValues();
        values.put(Employee.COL_WIDGET_ID, widgetId);
        return updateEmployee(context, values, empId);
    }

    public static void updateEmployees(Context context, Employee[] employees) {
//        Log.d(TAG, "updateEmployees()" + "context = [" + context + "], employees = [" + employees + "]");
        ContentResolver resolver = context.getContentResolver();
        ContentProviderClient client = resolver.acquireContentProviderClient(EmployeeQuery.CONTENT_URI);
        for (Employee employee : employees) {
            updateEmployeeOnIcp(client, employee);
        }
        client.release();
    }

    public static int updateEmployeeIsFav(Context context, int empId, boolean isfav) {
//        Log.d(TAG, "updateEmployeeIsFav()" + "empId = [" + empId + "], isfav = [" + isfav + "]");
        ContentValues values = new ContentValues();
        values.put(Employee.COL_FAV, isfav);
        return updateEmployee(context, values, empId);
    }

    public static int resetEmployeeWidgetId(Context context, int widgetId) {
//        Log.d(TAG, "resetEmployeeWidgetId()" + "widgetId = [" + widgetId + "]");
        Employee employee = EmployeeManager.getEmployeeOnWidgetId(context, widgetId);
        if (employee == null) return 0;
        long id = employee.get_id();
        ContentValues values = new ContentValues();
        values.put(Employee.COL_WIDGET_ID, (Integer) null);
        return updateEmployee(context, values, id);
    }

    public static void syncEmployees(Context context, Employee[] employees) {
        ContentResolver resolver = context.getContentResolver();
        ContentProviderClient client = resolver.acquireContentProviderClient(EmployeeQuery.CONTENT_URI);
        syncEmployees(context, client, employees);
        client.release();
    }

    //TODO zkontrolovat zda zustava widgetID
    public static void syncEmployees(Context context, ContentProviderClient client, Employee[] employees) {
//        Log.d(TAG, "syncEmployees() employees " + Arrays.toString(employees));

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        List<Employee> currentList = getAllEmployees(context);

        //update existing, add new
        for (Employee employee : employees) {
            if (currentList.contains(employee)) {
                ops.add(updateEmployeeOnIcpOp(employee));
            } else {
                ops.add(addEmployeeOp(employee));
            }

            currentList.remove(employee);
        }

        // delete all which has not been actualised
        for (Employee employee : currentList) {
            ops.add(deleteEmployeeOp(employee));
        }

        //mark which of employees is user
        try {
            String icp = AccountUtil.getUserICP(context);
            ops.add(markUserOp(icp));
        } catch (Exception e) {
            Log.e(TAG, "Marking user failed.");
        }

        try {
            Log.d(TAG, "syncEmployees() ops size " + ops.size());
            long start = System.currentTimeMillis();
            client.applyBatch(ops);
            long elapsedTimeMillis = System.currentTimeMillis() - start;
            float elapsedTimeSec = elapsedTimeMillis / 1000F;
            Log.d(TAG, "syncEmployees() elapsedTimeSec " + elapsedTimeSec);
            AppUtil.showInfo(context, context.getString(R.string.employees_act_ok) + employees.length);

            //update all employees widget
            new EmployeeWidgetProvider().updateAllWidgets(context);
        } catch (Exception e) {
            AppUtil.showInfo(context, context.getString(R.string.act_fail));
        }

    }

    private static ContentProviderOperation markUserOp(String icp) {
//        Log.d(TAG, "markUser()");
        ContentValues values = new ContentValues();
        values.put(Employee.COL_USER, true);
        return ContentProviderOperation.newUpdate(EmployeeQuery.CONTENT_URI).withSelection(EmployeeQuery.SELECTION_ICP,
                new String[]{String.valueOf(icp)}).withValues(values).build();
    }

    public static Employee getEmployeeOnWidgetId(Context context, int widgetId) {
//        Log.d(TAG, "getEmployeeOnId()" + "widgetId = [" + widgetId + "]");
        ContentResolver resolver = context.getContentResolver();
        ContentProviderClient client = resolver.acquireContentProviderClient(EmployeeQuery.CONTENT_URI);
        Employee employee = getEmployee(client, EmployeeQuery.SELECTION_WIDGET_ID, new String[]{String.valueOf(widgetId)});
        client.release();
        return employee;
    }

    public static Employee getEmployeeOnId(Context context, long id) {
//        Log.d(TAG, "getEmployeeOnId()" + "id = [" + id + "]");
        ContentResolver resolver = context.getContentResolver();
        ContentProviderClient client = resolver.acquireContentProviderClient(EmployeeQuery.CONTENT_URI);
        Employee employee = getEmployee(client, EmployeeQuery.SELECTION_ID, new String[]{String.valueOf(id)});
        client.release();
        return employee;
    }

    private static Employee getEmployee(ContentProviderClient client, String selection, String[] selectionArgs) {
//        Log.d(TAG, "getEmployeeOnId()" + "selection = [" + selection + "], selectionArgs = [" + Arrays.toString(selectionArgs) + "]");
        try {
            Cursor cursor = client.query(EmployeeQuery.CONTENT_URI, null,
                    selection, selectionArgs, null);
            Employee employee = null;
            while (cursor.moveToNext()) {
                employee = Employee.cursorToEmployee(cursor);
            }
            return employee;

        } catch (RemoteException e) {
            return null;
        }
    }

    public static List<Employee> getAllEmployees(Context context) {
//        Log.d(TAG, "getAllEmployees()");
        return getEmployees(context, null, null);
    }

    public static List<Employee> getEmployeesWithWidget(Context context) {
//        Log.d(TAG, "getAllEmployees()");
        return getEmployees(context, EmployeeQuery.SELECTION_WIDGET_NOT_NULL, null);
    }

    private static List<Employee> getEmployees(Context context, String selection, String[] selectionArgs) {
//        Log.d(TAG, "getEmployees()" + "selection = [" + selection + "], selectionArgs = [" + selectionArgs + "]");
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

    private static ContentProviderOperation deleteEmployeeOp(Employee employee) {
        return ContentProviderOperation.newDelete(EmployeeQuery.CONTENT_URI).withSelection(EmployeeQuery.SELECTION_ICP,
                new String[]{String.valueOf(employee.getIcp())}).build();
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
