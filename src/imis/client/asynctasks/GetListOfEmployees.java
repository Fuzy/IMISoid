package imis.client.asynctasks;

import android.app.Activity;
import android.util.Log;
import imis.client.model.Employee;
import imis.client.model.Event;
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
public class GetListOfEmployees extends NetworkingAsyncTask<String, Void, Employee[]> {
    private static final String TAG = GetListOfEmployees.class.getSimpleName();

    private Activity activity;

    public GetListOfEmployees(Activity activity, String... params) {
        super(params);
        this.activity = activity;
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

        Employee employee = new Employee();
        employee.setIcp("123");
        employee.setKodpra("KDA");
        //employee.setSubordinate(false);
        employee.setLastEventTime(1364169600000L);
        employee.setKod_po(Event.KOD_PO_ARRIVE_NORMAL);
        employee.setDruh("P");
        //employee.setWidgetId(-1);

        Employee employee2 = new Employee();
        employee2.setIcp("124");
        employee2.setKodpra("JSK");
        //employee2.setSubordinate(false);
        employee2.setLastEventTime(1364169650000L);
        employee2.setKod_po(Event.KOD_PO_LEAVE_LUNCH);
        employee2.setDruh("O");
        //employee2.setWidgetId(-1);

        employees = new Employee[]{employee, employee2};  //TODO pouze pro test
        Log.d(TAG, "onPostExecute()");
        if (employees != null) {
            EmployeeManager.addEmployees(activity, employees);
        }
        Log.d(TAG, "onPostExecute() " + EmployeeManager.getAllEmployees(activity));
        super.onPostExecute(null);
    }
}
