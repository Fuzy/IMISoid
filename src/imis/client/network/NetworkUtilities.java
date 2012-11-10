package imis.client.network;

import imis.client.model.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import android.util.Log;

public class NetworkUtilities {
  private static final String TAG = "NetworkUtilities";
  // private static final String BASE_URL =
  // "http://172.20.3.196:8080/Imisoid_WS/";
  private static final String BASE_URL = "http://10.0.2.2:8081/Imisoid_WS/";
  private static final String EVENTS_URI = BASE_URL + "events";
  private static final int TIMEOUT = 30 * 1000; // ms
  private static HttpClient httpClient = null;

  /*private static final String PARAM_FROM_DATE = "from";
  private static final String PARAM_TO_DATE = "to";*/

  /**
   * Configures the httpClient to connect to the URL provided.
   */
  public static HttpClient getHttpClient() {
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

  //@SuppressWarnings("unchecked")
  public static List<JsonObject> getEvents() {
    List<JsonObject> events = new ArrayList<JsonObject>();

    HttpClient httpclient = getHttpClient();
    // Prepare a request object
    //HttpGet httpget = new HttpGet(EVENTS_URI + "/0000001?from=29.7.2004&to=29.7.2004");
    HttpGet httpget = new HttpGet(EVENTS_URI + "/700510?from=1.11.2001&to=1.11.2001");
    //TODO UriBuilder
    Log.d(TAG, " " + httpget.getURI().toString());

    // Execute the request
    try {
      Log.d(TAG, "getEvents() executing");
      HttpResponse response = httpclient.execute(httpget);
      // Get hold of the response entity
      HttpEntity entity = response.getEntity();
      final String resp = EntityUtils.toString(entity);

      // events = (ArrayList<Event>) Util.gson.fromJson(resp, Util.listType);
      //Gson gson = new Gson();
      //Type listType = new TypeToken<List<Event>>() {
      //}.getType();
      //events = (List<Event>) gson.fromJson(resp, listType);
      JsonElement o = Util.parser.parse(resp);
      JsonArray array = o.getAsJsonArray();
      for (JsonElement jsonElement : array) {
        events.add(jsonElement.getAsJsonObject());
      }

    }
    catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (JsonSyntaxException e) {
      e.printStackTrace();
    }

    return events;
  }

}
