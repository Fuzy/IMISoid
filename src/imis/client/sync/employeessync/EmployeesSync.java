package imis.client.sync.employeessync;

import android.content.Context;
import android.util.Log;
import imis.client.RestUtil;
import imis.client.asynctasks.result.ResultItem;
import imis.client.asynctasks.result.ResultList;
import imis.client.asynctasks.util.AsyncUtil;
import imis.client.model.Employee;
import imis.client.network.NetworkUtilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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

        HttpEntity<Object> entity = new HttpEntity<Object>(RestUtil.prepareHttpHeaders(context));
        RestTemplate restTemplate = RestUtil.prepareRestTemplate();

        try {
            ResponseEntity<Employee> response = restTemplate.exchange(NetworkUtilities.getEmployeesGetEventURL(context),
                    HttpMethod.GET, entity, Employee.class, icp);
            Employee employee = response.getBody();
            return new ResultItem<Employee>(response.getStatusCode(), employee);
        } catch (Exception e) {
            @SuppressWarnings("unchecked")
            ResultItem<Employee> resultItem = AsyncUtil.processException(context, e, ResultItem.class);
            Log.d(TAG, "getEmployeeLastEvent() resultItem " + resultItem);
            return resultItem;
        }
    }

    public ResultList<Employee> getListOfEmployees() {
        Log.d(TAG, "getListOfEmployees()");

        HttpEntity<Object> entity = new HttpEntity<Object>(RestUtil.prepareHttpHeaders(context));
        RestTemplate restTemplate = RestUtil.prepareRestTemplate();

        try {
            ResponseEntity<Employee[]> response = restTemplate.exchange(NetworkUtilities.getEmployeesGetURL(context),
                    HttpMethod.GET, entity, Employee[].class);
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
}
