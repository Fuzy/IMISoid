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

import java.util.Arrays;

public class NetworkUtilities {//TODO asi jako nestaticka trida
    private static final String TAG = "NetworkUtilities";
    private static final String SCHEME = "http://";
    private static final String BASE_PATH = "/Imisoid_WS/";
    private static final String EVENTS_PATH = "events";
    private static final String EVENTS_DELETE_PATH = "events/{rowid}";
    private static final String EVENTS_UPDATE_PATH = "events/{rowid}";
    private static final String EVENTS_GET_PATH = "events/{icp}?from={from}&to={to}";
    private static final String RECORDS_PATH = "records/{kodpra}?from={from}&to={to}";
    private static final String AUTH_PATH = "authentication";
    private static final String EMPLOYEES_PATH = "employees/{icp}";
    private static final String EMPLOYEES_EVENTS_PATH = "employees/lastevents";
    private static final String EMPLOYEE_EVENT_PATH = "employees/lastevent/{icp}";
    private static final String TEST_PATH = "test/";
    private static final String TEST_MODE = "test";
    private static final String TEST_CONN = "testconnection";

    private static String DOMAIN = null;
    private static int PORT = -1;
    private static boolean isTest = false;
    private static boolean isConnected = false;
    public static String BASE_URL;
    public static String EVENTS_DELETE_URL;
    public static String EVENTS_UPDATE_URL;
    public static String EVENTS_GET_URL;
    public static String EVENTS_URL;
    public static String RECORDS_URL;
    public static String EMPLOYEES_URL;
    public static String EMPLOYEES_EVENTS_URL;
    public static String EMPLOYEE_EVENT_URL;
    public static String AUTH_URL;
    public static String TEST_CONN_URL;

    public static final String DOMAIN_DEFAULT = "10.0.0.2";//        //10.0.0.1
    public static final int PORT_DEFAULT = 8081;

    /*private static final String PARAM_USERNAME = "username";
    private static final String PARAM_PASSWORD = "password";*/

   /* public static String authenticate(String username, String password) {  //TODO predelat
        Log.d("NetworkUtilities", "authenticate() username: " + username + " password: " + password);
        HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
        HttpPost post = new HttpPost(AUTH_URL);
        HttpResponse resp;
        int code = -1;
        try {
            final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(PARAM_USERNAME, username));
            params.add(new BasicNameValuePair(PARAM_PASSWORD, password));
            String json = Util.gson.toJson(params);
            Log.d("NetworkUtilities", "authenticate() json: " + json);
            StringEntity se = new StringEntity(json);
            post.setEntity(se);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            resp = httpClient.execute(post);
            code = resp.getStatusLine().getStatusCode();
            Log.d("NetworkUtilities", "authenticate() code: " + code);
        } catch (ClientProtocolException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return "qwertyxx";
    }*/

    public static Result testWebServiceAndDBAvailability() {
        HttpHeaders requestHeaders = new HttpHeaders();
        org.springframework.http.HttpEntity<Object> entity = new org.springframework.http.HttpEntity<>(requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));

        try {
            ResponseEntity response = restTemplate.exchange(TEST_CONN_URL, HttpMethod.GET, entity,
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
    }

    public static void resetDomainAndPort(String domain, int port) {
        Log.d("NetworkUtilities", "resetDomainAndPort()");
        BASE_URL = parseBaseURL(domain, port);
        Log.d(TAG, "resetDomainAndPort() BASE_URL " + BASE_URL);
        EVENTS_URL = BASE_URL + EVENTS_PATH;
        EVENTS_DELETE_URL = BASE_URL + EVENTS_DELETE_PATH;
        EVENTS_UPDATE_URL = BASE_URL + EVENTS_UPDATE_PATH;
        EVENTS_GET_URL = BASE_URL + EVENTS_GET_PATH;
        RECORDS_URL = BASE_URL + RECORDS_PATH;
        EMPLOYEES_URL = BASE_URL + EMPLOYEES_PATH;
        EMPLOYEES_EVENTS_URL = BASE_URL + EMPLOYEES_EVENTS_PATH;
        EMPLOYEE_EVENT_URL = BASE_URL + EMPLOYEE_EVENT_PATH;
        AUTH_URL = BASE_URL + AUTH_PATH;
        TEST_CONN_URL = BASE_URL + TEST_CONN;
        Log.d(TAG, "resetDomainAndPort() TEST_CONN_URL " + TEST_CONN_URL);
    }

    private static String parseBaseURL(String domain, int port) {
        String[] splits = domain.split("/");
        Log.d(TAG,"parseBaseURL() splits " + Arrays.toString(splits));
        DOMAIN = splits[0];
        PORT = port;

        isTest = false;
        if (splits.length > 1 && splits[1].equals(TEST_MODE)) isTest = true;

        StringBuilder baseURL = new StringBuilder(SCHEME + DOMAIN + ":" + PORT + BASE_PATH);
        if (isTest) baseURL.append(TEST_PATH);
        return baseURL.toString();
    }


    private static String getPortAsString(int port) {
        return String.valueOf(port);
    }

    public static String getDomainOrDefault() {
        StringBuilder s = new StringBuilder((DOMAIN == null) ? DOMAIN_DEFAULT : DOMAIN);
        if (isTest) s.append("/" + TEST_MODE);
        return s.toString();
    }

    public static String getPortOrDefault() {
        return (PORT == -1) ? getPortAsString(PORT_DEFAULT) : getPortAsString(PORT);
    }

    public static boolean isConnected() {
        return isConnected;
    }

    public static void setConnected(boolean connected) {
        isConnected = connected;
    }
}
