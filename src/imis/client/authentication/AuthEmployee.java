package imis.client.authentication;

import android.content.Context;
import android.util.Log;
import imis.client.asynctasks.NetworkingAsyncTask;
import imis.client.RestUtil;
import imis.client.asynctasks.result.ResultItem;
import imis.client.asynctasks.util.AsyncUtil;
import imis.client.model.Employee;
import imis.client.network.NetworkUtilities;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 1.6.13
 * Time: 17:18
 */
public class AuthEmployee extends NetworkingAsyncTask<String, Void, ResultItem<Employee>> {
    private static final String TAG = AuthEmployee.class.getSimpleName();

    public AuthEmployee(Context context, String... params) {
        super(context, params);
    }

    @Override
    protected ResultItem<Employee> doInBackground(String... strings) {
        String icp = params[0];
        String password = (params[1].isEmpty()) ? "" : params[1];
        Log.d(TAG, "doInBackground() icp " + icp + " password " + password);

        HttpHeaders requestHeaders = new HttpHeaders();
        HttpAuthentication authHeader = new HttpBasicAuthentication(icp, password);
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity entity = new org.springframework.http.HttpEntity<>(requestHeaders);
        RestTemplate restTemplate = RestUtil.prepareRestTemplate();

        try {
            ResponseEntity<Employee> response = restTemplate.exchange(NetworkUtilities.getEmployeeGetURL(context),
                    HttpMethod.GET, entity, Employee.class, icp);
            Employee employee = response.getBody();
            return new ResultItem<Employee>(response.getStatusCode(), employee);
        } catch (Exception e) {
            @SuppressWarnings("unchecked")
            ResultItem<Employee> resultItem = AsyncUtil.processException(context,e, ResultItem.class);
            Log.d(TAG, "doInBackground() resultItem " + resultItem);
            return resultItem;
        }
    }

    @Override
    protected void onPostExecute(ResultItem<Employee> employeeResult) {
        super.onPostExecute(employeeResult);
    }

}
