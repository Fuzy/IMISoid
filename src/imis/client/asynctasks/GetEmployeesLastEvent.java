package imis.client.asynctasks;

import android.util.Log;
import imis.client.model.Employee;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 16.4.13
 * Time: 19:05
 */
@Deprecated
public class GetEmployeesLastEvent extends NetworkingAsyncTask<String, Void, Employee[]> {
    private static final String TAG = GetEmployeesLastEvent.class.getSimpleName();



    @Override
    protected Employee[] doInBackground(String... params) {
        Log.d(TAG, "doInBackground()");

        return new Employee[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void onPostExecute(Employee[] employees) {
        super.onPostExecute(null);
        Log.d(TAG, "onPostExecute()");

        /*Employee employee = new Employee("123", "KDA", false, 1364169600000L, Event.KOD_PO_ARRIVE_NORMAL, "P");
        EmployeeManager.addEmployee(activity, employee);
        Employee employee2 = new Employee("124", "JSS", false, 1364169650000L, Event.KOD_PO_LEAVE_LUNCH, "O");
        EmployeeManager.addEmployee(activity, employee2);*/

    }
}
