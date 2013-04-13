package imis.client.network;

import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import imis.client.json.Util;
import imis.client.model.Event;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.sql.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 13.4.13
 * Time: 19:29
 */
public class EventsSync {
    private static final String TAG = "EventsSync";

    public static int deleteEvent(String rowid) {
        Log.d(TAG, "deleteEvent() rowid: " + rowid);
        String uri = NetworkUtilities.EVENTS_URL + "/" + rowid;
        int code = sendHttpDelete(uri);
        return code;
    }

    public static int getUserEvents(List<Event> events, final String icp, final Date from, final Date to) {
        String strFrom = "29.7.2004";//TODO pryc
        String strTo = "29.7.2004";
        Log.d(TAG, "getUserEvents() icp: " + icp + " strFrom: " + strFrom + " strTo:" + strTo);

        String uri = NetworkUtilities.EVENTS_URL + "/" + icp + "?from=" + strFrom + "&to=" + strTo;// TODO  uri builder

        String resp = new String();
        int code = sendHttpGetForUserEvents(uri, resp);

        //TODO refaktor
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

    public static int createEvent(Event event) {
        Log.d(TAG, "createEvent() event: " + event);
        String uri = NetworkUtilities.EVENTS_URL;
        int code = sendHttpPost(uri, event);
        return code;
    }

    public static int updateEvent(Event event) {
        Log.d(TAG, "updateEvent() event: " + event);
        String uri = NetworkUtilities.EVENTS_URL;
        int code = sendHttpPost(uri, event);
        return code;
    }

    private static int sendHttpGetForUserEvents(String uri, String response) {
        HttpClient httpclient = HttpClientFactory.getThreadSafeClient();
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

    private static int sendHttpDelete(String uri) {
        HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
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
        HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
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
}
