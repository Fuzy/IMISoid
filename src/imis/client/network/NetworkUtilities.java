package imis.client.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import imis.client.asynctasks.result.Result;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

public class NetworkUtilities {
    private static final String TAG = NetworkUtilities.class.getSimpleName();


    public static Result testWebServiceAndDBAvailability(Context context) {
        HttpHeaders requestHeaders = new HttpHeaders();
        org.springframework.http.HttpEntity<Object> entity = new org.springframework.http.HttpEntity<>(requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));

        try {
            ResponseEntity response = restTemplate.exchange(getTestURL(context), HttpMethod.GET, entity,
                    null);
            return new Result(response.getStatusCode());
        } catch (Exception e) {
            if (e instanceof HttpServerErrorException) {
                HttpServerErrorException exp = (HttpServerErrorException) e;
                return new Result(exp.getStatusCode(), exp.getStatusText());
            }
            return new Result();
        }
    }

    public static boolean isOnline(Context context) { //TODO nerozlisuje typ spojeni

        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
        /*boolean connected;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String networkType = sharedPref.getString(AppConsts.KEY_NETWORK_TYPE, "WIFI");
        Log.d(TAG, "onReceive() networkType " + networkType);
        if (networkType.equals(AppConsts.NETWORK_TYPE_WIFI)) {
            NetworkInfo networkInfo = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            connected = networkInfo.isConnected();
            Log.d(TAG, "onReceive() NETWORK_TYPE_WIFI " + connected);
        } else  {
            NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
            connected = (networkInfo != null);
            Log.d(TAG, "onReceive() NETWORK_TYPE_ANY " + connected);
        }*/

    }

    public static void applyDomainAndPort(Context context, String domain, int port, boolean isTest) {
        Log.d(TAG, "applyDomainAndPort()" + "domain = [" + domain + "], port = [" + port + "], isTest = [" + isTest + "]");
        String baseURI = NetworkConsts.SCHEME + domain + ":" + port + NetworkConsts.BASE_PATH;
        if (isTest) baseURI = baseURI.concat(NetworkConsts.TEST_MODE);
        Log.d(TAG, "applyDomainAndPort() baseURI " + baseURI);
        NetworkConfig.setBaseURI(context, baseURI);
    }

    public static String getTestURL(Context context) {
        return NetworkConfig.getBaseURI(context) + NetworkConsts.TEST_CONN;
    }

    public static String getEventsCreateURL(Context context) {
        return NetworkConfig.getBaseURI(context) + NetworkConsts.EVENTS_CREATE_PATH;
    }

    public static String getEventsDeleteURL(Context context) {
        return NetworkConfig.getBaseURI(context) + NetworkConsts.EVENTS_DELETE_PATH;
    }

    public static String getEventsUpdateURL(Context context) {
        return NetworkConfig.getBaseURI(context) + NetworkConsts.EVENTS_UPDATE_PATH;
    }

    public static String getEventsGetURL(Context context) {
        return NetworkConfig.getBaseURI(context) + NetworkConsts.EVENTS_GET_PATH;
    }

    public static String getRecordsGetURL(Context context) {
        return NetworkConfig.getBaseURI(context) + NetworkConsts.RECORDS_GET_PATH;
    }

    public static String getEmployeeGetURL(Context context) {
        return NetworkConfig.getBaseURI(context) + NetworkConsts.EMPLOYEE_GET_PATH;
    }

    public static String getEmployeesGetURL(Context context) {
        return NetworkConfig.getBaseURI(context) + NetworkConsts.EMPLOYEES_GET_PATH;
    }

    public static String getEmployeesGetEventsURL(Context context) {
        return NetworkConfig.getBaseURI(context) + NetworkConsts.EMPLOYEES_GET_EVENTS_PATH;
    }

    public static String getEmployeesGetEventURL(Context context) {
        return NetworkConfig.getBaseURI(context) + NetworkConsts.EMPLOYEES_GET_EVENT_PATH;
    }

}
