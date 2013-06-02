package imis.client.authentication;

import android.content.Context;
import android.util.Log;
import imis.client.asynctasks.NetworkingAsyncTask;
import imis.client.asynctasks.result.Result;
import imis.client.asynctasks.result.ResultItem;
import imis.client.model.Employee;
import imis.client.network.HttpClientFactory;
import imis.client.network.NetworkUtilities;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
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

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        try {
            ResponseEntity<Employee> response = restTemplate.exchange(NetworkUtilities.EMPLOYEE_URL,
                    HttpMethod.GET, entity, Employee.class, icp);
            Employee employee = response.getBody();
            return new ResultItem<Employee>(response.getStatusCode(), employee);
        } catch (Exception e) {
            e.printStackTrace();//TODO client error
            ResultItem<Employee> item = processException(e, ResultItem.class);
            Log.d(TAG, "doInBackground() item " + item);
            return item;
        }
    }

    @Override
    protected void onPostExecute(ResultItem<Employee> employeeResult) {
        super.onPostExecute(employeeResult);
    }

    private <T extends Result> T processException(Exception e, Class<T> type) {
        T instance = null;
        try {
            if (e instanceof HttpClientErrorException) {
                Log.d(TAG, "processException() HttpClientErrorException");
                instance = type.getDeclaredConstructor(HttpStatus.class, String.class)
                        .newInstance(((HttpClientErrorException) e).getStatusCode(), e.getLocalizedMessage());
            } else if (e instanceof HttpServerErrorException) {
                Log.d(TAG, "processException() HttpServerErrorException");
                instance = type.getDeclaredConstructor(HttpStatus.class, String.class)
                        .newInstance(((HttpServerErrorException) e).getStatusCode(), e.getLocalizedMessage());
            } else {
                instance = type.getDeclaredConstructor(String.class).newInstance(e.getLocalizedMessage());
            }
        } catch (Exception e1) {
            e1.printStackTrace(); //TODO
        }
        return instance;
    }


            /*if (e instanceof HttpClientErrorException) {
                Log.d(TAG, "doInBackground() HttpClientErrorException");
                return new ResultItem<Employee>(((HttpClientErrorException) e).getStatusCode(), e.getLocalizedMessage());
            } else if (e instanceof HttpServerErrorException) {
                Log.d(TAG, "doInBackground() HttpServerErrorException");
                return new ResultItem<Employee>(((HttpServerErrorException) e).getStatusCode(), e.getLocalizedMessage());
            }
            return new ResultItem<Employee>(e.getLocalizedMessage());*/
}
