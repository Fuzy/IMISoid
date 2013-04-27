package imis.client.asynctasks;

import android.util.Log;
import imis.client.model.Employee;
import imis.client.model.Event;
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
 * Date: 8.4.13
 * Time: 22:29
 */
public class GetListOfEmployees extends NetworkingAsyncTask<String, Void, Employee[]> {
    private static final String TAG = GetListOfEmployees.class.getSimpleName();

    public GetListOfEmployees(String... params) {
        super(params);
    }

    @Override
    protected Employee[] doInBackground(String... params) {
        Log.d(TAG, "doInBackground()");
        String url, icp = "";
        if (params.length == 0) {
            url = NetworkUtilities.EMPLOYEES_EVENTS_URL;
        } else {
            url = NetworkUtilities.EMPLOYEES_URL;
            icp = params[0];
        }


        HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> entity = new HttpEntity<>(requestHeaders);

        //Create a Rest template
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        try {
            Log.d(TAG, "doInBackground() url " + url + " icp " + icp);
            ResponseEntity<Employee[]> response = restTemplate.exchange(url, HttpMethod.GET, entity,
                    Employee[].class, icp); //TODO test dvoji
            Employee[] body = response.getBody();
            return body;
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }

        return new Employee[]{};
    }

    @Override
    protected void onPostExecute(Employee[] employees) {

        Employee employee = new Employee("123", "KDA", false, 1364169600000L, Event.KOD_PO_ARRIVE_NORMAL, "P");
        Employee employee2 = new Employee("124", "JSS", false, 1364169650000L, Event.KOD_PO_LEAVE_LUNCH, "O");
        employees = new Employee[]{employee, employee2};  //TODO pouze pro test
        Log.d(TAG, "onPostExecute()");
        if (employees != null) {
            //EmployeeManager.addEmployees(activity, employees);
        }

        super.onPostExecute(null);
    }
}
