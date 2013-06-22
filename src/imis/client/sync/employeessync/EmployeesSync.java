package imis.client.sync.employeessync;

import android.content.Context;
import android.util.Log;
import imis.client.asynctasks.result.ResultItem;
import imis.client.asynctasks.result.ResultList;
import imis.client.asynctasks.util.AsyncUtil;
import imis.client.authentication.AuthenticationUtil;
import imis.client.model.Employee;
import imis.client.network.HttpClientFactory;
import imis.client.network.NetworkUtilities;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 25.5.13
 * Time: 12:54
 */
public class EmployeesSync {
    private static final String TAG = EmployeesSync.class.getSimpleName();

    private final Context context;

    public EmployeesSync(Context context) {
        this.context = context;
    }

    public ResultItem<Employee> getEmployeeLastEvent(final String icp) {
        Log.d(TAG, "getUserEvents() icp: " + icp);

        HttpHeaders requestHeaders = new HttpHeaders();
        HttpAuthentication authHeader = AuthenticationUtil.createAuthHeader(context);
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity entity = new org.springframework.http.HttpEntity<>(requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        try {
            ResponseEntity<Employee> response = restTemplate.exchange(NetworkUtilities.getEmployeesGetEventURL(context),
                    HttpMethod.GET, entity, Employee.class, icp);
            Employee employee = response.getBody();
            return new ResultItem<Employee>(response.getStatusCode(), employee);
        } catch (Exception e) {
            ResultItem<Employee> resultItem = AsyncUtil.processException(e, ResultItem.class);
            Log.d(TAG, "getEmployeeLastEvent() resultItem " + resultItem);
            return resultItem;
        }
    }

    public ResultList<Employee> getListOfEmployees() {
        Log.d(TAG, "getListOfEmployees()");

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
            ResponseEntity<Employee[]> response = restTemplate.exchange(NetworkUtilities.getEmployeesGetURL(context),
                    HttpMethod.GET, entity, Employee[].class);
            Employee[] body = response.getBody();
            Log.d(TAG, "doInBackground() body " + body);
            return new ResultList<Employee>(response.getStatusCode(), body);
        } catch (Exception e) {
            ResultList<Employee> resultList = AsyncUtil.processException(e, ResultList.class);
            Log.d(TAG, "doInBackground() resultList " + resultList);
            return resultList;
        }
    }
}
