package imis.client.network;

import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import imis.client.json.Util;
import imis.client.model.Event;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NetworkUtilities {
    private static final String TAG = "NetworkUtilities";
    private static final String SCHEME = "http://";
    private static final String PATH = "/Imisoid_WS/";

    private static String DOMAIN = "172.20.99.43";// TODO nacist ze shared
    private static String PORT = "8081";
    private static String BASE_URL = SCHEME + DOMAIN + ":" + PORT + PATH;// 10.0.2.2
    private static String EVENTS_URI = BASE_URL + "events";
    private static String RECORDS_URI = BASE_URL + "records";
    private static String AUTH_URI = BASE_URL + "authentication";

    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_PASSWORD = "password";
    private static final int TIMEOUT = 5 * 1000; // ms
    private static HttpClient httpClient = null;

    /**
     * Configures the httpClient to connect to the URL provided.
     */
    private static HttpClient getHttpClient() {
        Log.d(TAG, "getHttpClient()");
        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
            final HttpParams params = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, TIMEOUT);
            ConnManagerParams.setTimeout(params, TIMEOUT);
        }
        return httpClient;
    } //TODO refaktor sitovani

    public static int deleteEvent(String rowid) {
        Log.d(TAG, "deleteEvent() rowid: " + rowid);
        String uri = EVENTS_URI + "/" + rowid;
        int code = sendHttpDelete(uri);
        return code;
    }

    // @SuppressWarnings("unchecked")
    public static int getUserEvents(List<Event> events, final String icp, final Date from, final Date to) {
        //String strFrom = Util.formatDate(from);
        //String strTo = Util.formatDate(to);
        String strFrom = "29.7.2004";//TODO pryc
        String strTo = "29.7.2004";
        Log.d(TAG, "getUserEvents() icp: " + icp + " strFrom: " + strFrom + " strTo:" + strTo);

        String uri = EVENTS_URI + "/" + icp + "?from=" + strFrom + "&to=" + strTo;// TODO  uri builder

        String resp = new String();
        int code = sendHttpGetForUserEvents(uri, resp);

        JsonElement o = Util.parser.parse(resp);
        JsonArray array = o.getAsJsonArray();
        JsonObject eventJson;
        //List<Event> events = new ArrayList<Event>();
        Event event;
        for (JsonElement jsonElement : array) {
            eventJson = jsonElement.getAsJsonObject();
            event = Event.jsonToEvent(eventJson);
            events.add(event);
        }

        return code;
    }

    //TODO date /> long nebo string
    /*public static int getUserRecords(List<Record> records, final String kodpra, final Date from, final Date to) {
        String strFrom = "29.7.2004";//TODO pryc
        String strTo = "29.7.2004";
        String kodpraL = "JEL";
        Log.d(TAG, "getUserRecords() kodpraL: " + kodpraL + " strFrom: " + strFrom + " strTo:" + strTo);

        String uri = RECORDS_URI + "/" + kodpraL + "?from=" + strFrom + "&to=" + strTo;// TODO  uri builder
        String resp = new String();
        int code = sendHttpGetForUserEvents(uri, resp);

        JsonElement o = Util.parser.parse(resp);
        JsonArray array = o.getAsJsonArray();
        JsonObject recordJson;

        Record record;
        for (JsonElement jsonElement : array) {
            recordJson = jsonElement.getAsJsonObject();
            record = Record.jsonToRecord(recordJson);
            records.add(record);
        }

        return code;
    }*/

    public static String getUserRecords(final String kodpra, final String from, final String to) {
        Log.d(TAG, "getUserRecords() kodpra: " + kodpra + " strFrom: " + from + " strTo:" + to);

        String response = null;
        String uri = RECORDS_URI + "/" + kodpra;// + "?from=" + strFrom + "&to=" + strTo;// TODO  uri builder
        HashMap<String, String> params = new HashMap<String, String>(2);
        params.put("from", from);
        params.put("to", to);
        try {
            Log.d("NetworkUtilities", "getUserRecords() pre");
            response = HttpRequest.sendRequest(uri, "GET", null, params, null);
            Log.d("NetworkUtilities", "getUserRecords() after");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return response;
    }

    public static int createEvent(Event event) {
        Log.d(TAG, "createEvent() event: " + event);
        String uri = EVENTS_URI;
        int code = sendHttpPost(uri, event);
        return code;
    }

    public static int updateEvent(Event event) {
        Log.d(TAG, "updateEvent() event: " + event);
        String uri = EVENTS_URI;
        int code = sendHttpPost(uri, event);
        return code;
    }

    //TODO skonsolidovat do jednoho testu
    public static int testWebServiceAndDBAvailability() {
        int code = sendHttpGetTest(EVENTS_URI);//TODO domain + port implicitne
        return code;
    }

    @Deprecated
    public static boolean testWebServiceAndDBAvailability2(String domain) {
        String strUrl = "http://stackoverflow.com/about";

        try {
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error creating HTTP connection");
            e.printStackTrace();
            // throw e;
        }
        return false;
    }

    @Deprecated
    public static boolean testHostReachability(String domain, StringBuilder errMsg) {
        Log.d("NetworkUtilities", "testHostReachability()");
        boolean isReachable = false;
        try {
            InetAddress inet = InetAddress.getByName(domain);
            isReachable = inet.isReachable(5000);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            errMsg.append(e.getMessage());
        }
        return isReachable;
    }

    //TODO prejemnovat
    private static int sendHttpGetForUserEvents(String uri, String response) {
        HttpClient httpclient = getHttpClient();
        HttpGet httpget = new HttpGet(uri);
        HttpResponse resp;
        int code = -1;
        // String respStr = null;
        try {
            resp = httpclient.execute(httpget);
            HttpEntity entity = resp.getEntity();
            code = resp.getStatusLine().getStatusCode();
            response = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return code;
    }

    private static int sendHttpGetTest(String uri) {
        HttpClient httpclient = getHttpClient();
        HttpGet httpget = new HttpGet(uri);
        HttpResponse resp;
        int code = -1;
        try {
            resp = httpclient.execute(httpget);
            code = resp.getStatusLine().getStatusCode();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.d(TAG, "sendHttpGetTest uri: " + uri + "code: " + code);
        return code;
    }

    private static int sendHttpDelete(String uri) {
        HttpClient httpClient = getHttpClient();
        HttpDelete delete = new HttpDelete(uri);
        HttpResponse resp;
        int code = -1;
        try {
            resp = httpClient.execute(delete);
            code = resp.getStatusLine().getStatusCode();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "sendDelete uri: " + uri + "code: " + code);
        return code;
    }

    private static int sendHttpPost(String uri, Event event) {
        HttpClient httpClient = getHttpClient();
        HttpPost post = new HttpPost(uri);
        HttpResponse resp;
        int code = -1;
        try {
            StringEntity se = new StringEntity(Event.getAsJson(event));
            post.setEntity(se);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            resp = httpClient.execute(post);
            code = resp.getStatusLine().getStatusCode();
            // TODO event doplni rowid
            if (event.getServer_id() == null) {
                String s = resp.getLastHeader("Location").getValue();//TODO vyjimka
                URI location = URI.create(s);
                String path = location.getPath();
                event.setServer_id(path.substring(path.lastIndexOf('/') + 1));
                Log.d(TAG, "sendHttpPost event: " + event);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "sendHttpPost uri: " + uri + "code: " + code);
        return code;
    }

    private static int testHttp() {
        return 0;
    }

    public static String authenticate(String username, String password) {
        Log.d("NetworkUtilities", "authenticate() username: " + username + " password: " + password);
        HttpClient httpClient = getHttpClient();
        HttpPost post = new HttpPost(AUTH_URI);
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
            // TODO event doplni rowid
        } catch (ClientProtocolException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return "qwertyxx";
    }

    public static String getDOMAIN() {
        return DOMAIN;
    }

    public static void setDOMAIN(String DOMAIN) {
        Log.d("NetworkUtilities", "setDOMAIN() " + DOMAIN);
        NetworkUtilities.DOMAIN = DOMAIN;
    }

    public static String getPORT() {
        return PORT;
    }

    public static void setPORT(String PORT) {
        Log.d("NetworkUtilities", "setPORT() " + PORT);
        NetworkUtilities.PORT = PORT;
    }
}
