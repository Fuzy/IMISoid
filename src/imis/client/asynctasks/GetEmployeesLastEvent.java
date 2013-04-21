package imis.client.asynctasks;

import android.app.Activity;
import imis.client.model.Employee;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 16.4.13
 * Time: 19:05
 */
public class GetEmployeesLastEvent extends NetworkingAsyncTask<Void, Void, Employee[]> {

    //TODO asi zbytecny, jeden async task pro oboji
    public GetEmployeesLastEvent(Activity context) {
        super(context);
    }

    @Override
    protected Employee[] doInBackground(Void... voids) {
        return new Employee[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void onPostExecute(Employee[] employees) {


       /* Employee employee = new Employee("123", "KDA", false, 1364169600000L, Event.KOD_PO_ARRIVE_NORMAL);
        EmployeeManager.addEmployee(activity, employee);
        Employee employee2 = new Employee("124", "JSS", false, 1364169650000L, Event.KOD_PO_LEAVE_LUNCH);
        EmployeeManager.addEmployee(activity, employee2);*/

    }
}
