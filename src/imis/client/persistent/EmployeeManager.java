package imis.client.persistent;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.google.gson.reflect.TypeToken;
import imis.client.model.Employee;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static imis.client.json.Util.gson;

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
        ContentValues values = employee.getAsContentValues();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(DataQuery.CONTENT_URI, values);
        return Integer.valueOf(uri.getLastPathSegment());
    }

    public static void addEmployees(Context context, List<Employee> employees) {
        Log.d(TAG, "addEmployees()");
        for (Employee employee : employees) {
            addEmployee(context, employee);
        }
    }

    public static List<Employee> getAllEmployees(Context context) {
        Log.d(TAG, "getAllEmployees()");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(DataQuery.CONTENT_URI, DataQuery.PROJECTION_ALL, null, null, null);
        List<Employee> employees = new ArrayList<Employee>();
        Employee employee;
        while (cursor.moveToNext()) {
            employee = Employee.cursorToEmployee(cursor);
            employees.add(employee);
        }
        cursor.close();
        return employees;
    }

    public static List<Employee> jsonToList(String json) {
        Log.d(TAG, "jsonToList()");
        Type type = new TypeToken<Collection<Employee>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    final public static class DataQuery {

        public static final Uri CONTENT_URI = Uri.parse(Consts.SCHEME + Consts.AUTHORITY + "/"
                + MyDatabaseHelper.TABLE_EMPLOYEES);

        public static final String[] PROJECTION_ALL = {Employee.COL_ID, Employee.COL_ICP,
                Employee.COL_KODPRA, Employee.COL_SUB};

    }
}
