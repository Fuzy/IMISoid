package imis.client.services;

import android.util.Log;
import imis.client.model.Employee;
import imis.client.network.HttpClientFactory;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EmployeeManager;
import imis.client.ui.activities.NetworkingActivity;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static imis.client.ui.activities.ProgressState.DONE;
import static imis.client.ui.activities.ProgressState.RUNNING;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 8.4.13
 * Time: 22:29
 */
public class GetListOfEmployees extends NetworkingService<String, Void, Employee[]> {
    private static final String TAG = GetListOfEmployees.class.getSimpleName();

    public GetListOfEmployees(NetworkingActivity context) {
        super(context);
    }

    @Override
    protected Employee[] doInBackground(String... params) {
        Log.d(TAG, "doInBackground()");
        changeProgress(RUNNING, "working");
        String icp = params[0];

        HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> entity = new HttpEntity<>(requestHeaders);

        //Create a Rest template
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        try {
            //TODO uri variables
            ResponseEntity<Employee[]> response = restTemplate.exchange(NetworkUtilities.EMPLOYEES_URL, HttpMethod.GET, entity,
                    Employee[].class, icp);
            Employee[] body = response.getBody();
            return body;
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }finally {
            changeProgress(DONE, null);
        }

        return new Employee[]{};
    }

    @Override
    protected void onPostExecute(Employee[] employees) {
        Log.d(TAG, "onPostExecute()");
        //List<Employee> employees = EmployeeManager.jsonToList(response);
        if (employees != null) {
            EmployeeManager.addEmployees(activity, employees);

        }

        Log.i(TAG, "employees: " + EmployeeManager.getAllEmployees(activity));
    }
}
