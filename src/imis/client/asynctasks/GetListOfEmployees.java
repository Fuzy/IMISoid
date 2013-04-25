package imis.client.asynctasks;

import android.app.Activity;
import android.util.Log;
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
public class GetListOfEmployees extends NetworkingAsyncTask<String, Void, Employee[]> {
    private static final String TAG = GetListOfEmployees.class.getSimpleName();

    public GetListOfEmployees(Activity context) {
        super(context);
    }

    @Override
    protected Employee[] doInBackground(String... params) {
        Log.d(TAG, "doInBackground()");
        String url, icp = "";
        if (params == null) {
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
        super.onPostExecute(null);
        // dialog.dismiss();
        /*Employee emp = new Employee("234", "BCD", false, 1360000000000L, "02", "O");
        employees = new Employee[]{emp}; //TODO smazat*/
        Log.d(TAG, "onPostExecute()");
        if (employees != null) {
            EmployeeManager.addEmployees(activity, employees);
        }

        OnAsyncActionCompletedListener myActivity = (OnAsyncActionCompletedListener) activity;
        myActivity.asyncActionCompleted();
    }
}
