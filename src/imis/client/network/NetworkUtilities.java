package imis.client.network;

import imis.client.model.Event;
import imis.client.model.Util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.util.Log;

public class NetworkUtilities {
  private static final String TAG = "NetworkUtilities";
  // private static final String BASE_URL =
  // "http://172.20.3.196:8080/Imisoid_WS/";
  private static final String BASE_URL = "http://10.0.0.1:8081/Imisoid_WS/";// 10.0.2.2
  private static final String EVENTS_URI = BASE_URL + "events";
  private static final int TIMEOUT = 5 * 1000; // ms
  private static HttpClient httpClient = null;

  /*
   * private static final String PARAM_FROM_DATE = "from"; private static final
   * String PARAM_TO_DATE = "to";
   */

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
  }

  public static int deleteEvent(String rowid) {
    Log.d(TAG, "deleteEvent() rowid: " + rowid);
    String uri = EVENTS_URI + "/" + rowid;
    int code = sendHttpDelete(uri);
    return code;
  }

  // @SuppressWarnings("unchecked")
  public static List<Event> getUserEvents(final String icp, final Date from, final Date to) {
    String strFrom = Util.formatDate(from);
    String strTo = Util.formatDate(to);
    Log.d(TAG, "getUserEvents() cip: " + icp + " strFrom: " + strFrom + " strTo:" + strTo);

    String uri = EVENTS_URI + "/" + icp + "?from=" + strFrom + "&to=" + strTo;// TODO
    // uri
    // builder
    Log.d(TAG, "getUserEvents uri: " + uri);

    String resp = new String();
    int code = sendHttpGetForUserEvents(uri, resp);

    JsonElement o = Util.parser.parse(resp);
    JsonArray array = o.getAsJsonArray();
    JsonObject eventJson;
    List<Event> events = new ArrayList<Event>();
    Event event;
    for (JsonElement jsonElement : array) {
      eventJson = jsonElement.getAsJsonObject();
      event = Event.jsonToEvent(eventJson);
      events.add(event);
    }

    return events;// TODO vracet kod
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

  private static int sendHttpGetForUserEvents(String uri, String response) {
    Log.d(TAG, "sendHttpGetForUserEvents() uri: " + uri);
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
    }
    catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Log.d(TAG, "sendHttpGetForUserEvents uri: " + uri + "code: " + code);
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
    }
    catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
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
        String s = resp.getLastHeader("Location").getValue();
        URI location = URI.create(s);
        String path = location.getPath();
        event.setServer_id(path.substring(path.lastIndexOf('/') + 1));
        Log.d(TAG, "sendHttpPost event: " + event);
      }
    }
    catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Log.d(TAG, "sendHttpPost uri: " + uri + "code: " + code);
    return code;
  }

}
