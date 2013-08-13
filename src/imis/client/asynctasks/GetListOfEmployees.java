package imis.client.asynctasks;

import android.content.Context;
import android.util.Log;
import imis.client.RestUtil;
import imis.client.asynctasks.result.ResultList;
import imis.client.asynctasks.util.AsyncUtil;
import imis.client.model.Employee;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EmployeeManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Asynchronous task for downloading list of employees.
 */
public class GetListOfEmployees extends NetworkingAsyncTask<String, Void, ResultList<Employee>> {
    private static final String TAG = GetListOfEmployees.class.getSimpleName();
    private boolean isSync;

    public GetListOfEmployees(Context context, String... params) {
        super(context, params);
    }

    @Override
    protected ResultList<Employee> doInBackground(String... params) {
        Log.d(TAG, "doInBackground()");
        String url, icp = "";
        if (params.length == 0) {
            url = NetworkUtilities.getEmployeesGetEventsURL(context);
            isSync = false;
        } else {
            url = NetworkUtilities.getEmployeesGetURL(context);
            isSync = true;
            icp = params[0];
        }

        HttpEntity<Object> entity = new HttpEntity<Object>(RestUtil.prepareHttpHeaders(context));

        //Create a Rest template
        RestTemplate restTemplate = RestUtil.prepareRestTemplate();

        try {
            Log.d(TAG, "doInBackground() url " + url + " icp " + icp);
            ResponseEntity<Employee[]> response = restTemplate.exchange(url, HttpMethod.GET, entity,
                    Employee[].class, icp);
            Employee[] body = response.getBody();
            Log.d(TAG, "doInBackground() body " + body);
            return new ResultList<Employee>(response.getStatusCode(), body);
        } catch (Exception e) {
            @SuppressWarnings("unchecked")
            ResultList<Employee> resultList = AsyncUtil.processException(context, e, ResultList.class);
            Log.d(TAG, "doInBackground() resultList " + resultList);
            return resultList;
        }
    }

    @Override
    protected void onPostExecute(ResultList<Employee> resultList) {

        if (resultList.isOk() && !resultList.isEmpty()) {
            Employee[] employees = resultList.getArray();
            if (employees != null) {
                if (isSync) {
                    EmployeeManager.syncEmployees(context, employees);
                } else {
                    EmployeeManager.updateEmployees(context, employees);
                }
            }
        }

        Log.d(TAG, "onPostExecute() " + EmployeeManager.getAllEmployees(context));
        super.onPostExecute(resultList);
    }
}
