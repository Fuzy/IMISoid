package imis.client.services;

import android.util.Log;
import imis.client.model.Employee;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EmployeeManager;
import imis.client.ui.activity.NetworkingActivity;

import java.util.List;

import static imis.client.ui.activity.ProgressState.*;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 8.4.13
 * Time: 22:29
 */
public class RefreshListOfEmployees extends NetworkingService<String, Void, String> {
    private static final String TAG = RefreshListOfEmployees.class.getSimpleName();

    public RefreshListOfEmployees(NetworkingActivity context) {
        super(context);
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "doInBackground()");
        changeProgress(RUNNING, "working");
        String icp = params[0];
        changeProgress(DONE, null);
        return NetworkUtilities.getListOfEmployees(icp);
    }

    @Override
    protected void onPostExecute(String response) {
        Log.d(TAG, "onPostExecute()");
        List<Employee> employees = EmployeeManager.jsonToList(response);
        if (employees != null) {
            EmployeeManager.addEmployees(activity, employees);

        }

        Log.i(TAG, "employees: " + EmployeeManager.getAllEmployees(activity));
    }
}
