package imis.client.services;

import android.util.Log;
import imis.client.model.Employee;
import imis.client.network.HttpClientFactory;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EmployeeManager;
import imis.client.ui.activity.NetworkingActivity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static imis.client.ui.activity.ProgressState.DONE;
import static imis.client.ui.activity.ProgressState.RUNNING;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 8.4.13
 * Time: 22:29
 */
public class RefreshListOfEmployees extends NetworkingService<String, Void, List<Employee>> {
    private static final String TAG = RefreshListOfEmployees.class.getSimpleName();

    public RefreshListOfEmployees(NetworkingActivity context) {
        super(context);
    }

    @Override
    protected List<Employee> doInBackground(String... params) {
        Log.d(TAG, "doInBackground()");
        changeProgress(RUNNING, "working");
        String icp = params[0];


        //Create a Rest template
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
//Create a list for the message converters
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();

//Add the Jackson Message converter
        messageConverters.add(new MappingJacksonHttpMessageConverter());

//Add the message converters to the restTemplate
        restTemplate.setMessageConverters(messageConverters);

        //A simple GET request, the response will be mapped to Example.class
        String url = NetworkUtilities.EMPLOYEES_URL;
        String employees = restTemplate.getForObject(url, String.class);
        Log.d(TAG, "doInBackground() employees: " + employees);
        //TODO pijmout list pomoci rsttemplate
        changeProgress(DONE, null);//TODO variables
        //return NetworkUtilities.getListOfEmployees(icp);
        return null;
    }

    @Override
    protected void onPostExecute(List<Employee> employees) {
        Log.d(TAG, "onPostExecute()");
        //List<Employee> employees = EmployeeManager.jsonToList(response);
        if (employees != null) {
            EmployeeManager.addEmployees(activity, employees);

        }

        Log.i(TAG, "employees: " + EmployeeManager.getAllEmployees(activity));
    }
}
