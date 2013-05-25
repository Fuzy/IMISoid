package imis.client.sync.employeessync;

import android.content.Context;
import android.util.Log;
import imis.client.asynctasks.result.ResultData;
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

    public ResultData<Employee> getEmployeeLastEvent(final String icp) {
        Log.d(TAG, "getUserEvents() icp: " + icp );

        HttpHeaders requestHeaders = new HttpHeaders();
        HttpAuthentication authHeader = AuthenticationUtil.createAuthHeader(context);
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity entity = new org.springframework.http.HttpEntity<>(requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        try {
            ResponseEntity<Employee[]> response = restTemplate.exchange(NetworkUtilities.EMPLOYEE_EVENT_URL,
                    HttpMethod.GET, entity, Employee[].class, icp);
            Employee[] employee = response.getBody();
            return new ResultData<Employee>(response.getStatusCode(), employee);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultData<Employee>(e.getLocalizedMessage());
        }
    }
}
