package imis.client.asynctasks;

import android.content.Context;
import android.util.Log;
import imis.client.asynctasks.result.ResultList;
import imis.client.asynctasks.util.AsyncUtil;
import imis.client.authentication.AuthenticationUtil;
import imis.client.model.Employee;
import imis.client.network.HttpClientFactory;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EmployeeManager;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 8.4.13
 * Time: 22:29
 */
public class GetListOfEmployees extends NetworkingAsyncTask<String, Void, ResultList<Employee>> {
    private static final String TAG = GetListOfEmployees.class.getSimpleName();

    public GetListOfEmployees(Context context, String... params) {
        super(context, params);
    }

    @Override
    protected ResultList<Employee> doInBackground(String... params) {
        Log.d(TAG, "doInBackground()");
        String url, icp = "";
        if (params.length == 0) {
            url = NetworkUtilities.getEmployeesGetEventsURL(context);
        } else {
            url = NetworkUtilities.getEmployeesGetURL(context);
            icp = params[0];
        }

        HttpHeaders requestHeaders = new HttpHeaders();
        HttpAuthentication authHeader = AuthenticationUtil.createAuthHeader(context);
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> entity = new HttpEntity<>(requestHeaders);

        //Create a Rest template
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        try {
            Log.d(TAG, "doInBackground() url " + url + " icp " + icp);
            ResponseEntity<Employee[]> response = restTemplate.exchange(url, HttpMethod.GET, entity,
                    Employee[].class, icp);
            Employee[] body = response.getBody();
            Log.d(TAG, "doInBackground() body " + body);
            return new ResultList<Employee>(response.getStatusCode(), body);
        } catch (Exception e) {
            ResultList<Employee> resultList = AsyncUtil.processException(context,e, ResultList.class);
            Log.d(TAG, "doInBackground() resultList " + resultList);
            return resultList;
        }
    }

    @Override
    protected void onPostExecute(ResultList<Employee> resultList) {
//        Log.d(TAG, "onPostExecute() employees " + Arrays.toString(employees));

        if (resultList.isOk() && !resultList.isEmpty()) {
            Log.d(TAG, "onPostExecute() OK and not empty");
            Employee[] employees = resultList.getArray();
            if (employees != null) {
                EmployeeManager.syncEmployees(context, employees);
            }
        }

        Log.d(TAG, "onPostExecute() " + EmployeeManager.getAllEmployees(context));
        super.onPostExecute(resultList);
    }
}
