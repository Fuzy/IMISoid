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
        Uri uri = resolver.insert(DataQuery.CONTENT_URI, values);
        int id = Integer.valueOf(uri.getLastPathSegment());
        if (id == -1) {
            updateEmployee(context, employee);
        }
        return id;
    }

    public static int updateEmployee(Context context, Employee employee) {
        Log.d(TAG, "updateEmployee()");
        ContentValues values = employee.asContentValues();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(DataQuery.CONTENT_URI, String.valueOf(employee.getIcp()));
        int updated = resolver.update(uri, values, null, null);
        Log.d(TAG, "updateEmployee() updated " + updated);
        return updated;
    }

    public static void addEmployees(Context context, Employee[] employees) {
        Log.d(TAG, "addEmployees()");
        for (Employee employee : employees) {
            addEmployee(context, employee);
        }
    }

    public static List<Employee> getAllEmployees(Context context) {
        Log.d(TAG, "getAllEmployees()");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(DataQuery.CONTENT_URI, DataQuery.PROJECTION_ALL, null, null, null);
        List<Employee> employees = new ArrayList<>();
        Employee employee;
        while (cursor.moveToNext()) {
            employee = Employee.cursorToEmployee(cursor);
            employees.add(employee);
        }
        cursor.close();
        return employees;
    }

    final public static class DataQuery {

        public static final Uri CONTENT_URI = Uri.parse(Consts.SCHEME + Consts.AUTHORITY + "/"
                + MyDatabaseHelper.TABLE_EMPLOYEES);

        public static final String[] PROJECTION_ALL = {Employee.COL_ID, Employee.COL_ICP,
                Employee.COL_KODPRA, Employee.COL_SUB};

    }
}
