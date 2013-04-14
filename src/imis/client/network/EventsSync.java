package imis.client.network;

import android.util.Log;
import imis.client.AppUtil;
import imis.client.model.Event;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 13.4.13
 * Time: 19:29
 */
public class EventsSync {
    private static final String TAG = "EventsSync";

    public static int deleteEvent(final String rowid) {
        Log.d(TAG, "deleteEvent() rowid: " + rowid);

        HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.setAuthorization(authHeader);
        HttpEntity<Object> entity = new org.springframework.http.HttpEntity<>(requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));

        int statusCode = -1;
        try {
            ResponseEntity response = restTemplate.exchange(NetworkUtilities.EVENTS_DELETE_URL, HttpMethod.DELETE, entity, null, rowid);
            statusCode = response.getStatusCode().value();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statusCode;
    }

    public static int getUserEvents(List<Event> events, final String icp, final long from, final long to) {
        String strFrom = AppUtil.formatDate(from);//TODO pryc
        String strTo = AppUtil.formatDate(to);
        Log.d(TAG, "getUserEvents() icp: " + icp + " strFrom: " + strFrom + " strTo:" + strTo);

        HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity entity = new org.springframework.http.HttpEntity<>(requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        int statusCode = -1;
        try {
            ResponseEntity<Event[]> response = restTemplate.exchange(NetworkUtilities.EVENTS_GET_URL,
                    HttpMethod.GET, entity, Event[].class, icp, strFrom, strTo);
            Event[] eventsArray = response.getBody();
            for (Event event : eventsArray) {
                events.add(event);
            }
            Log.d(TAG, "getUserEvents() events.size() " + events.size());
            statusCode = response.getStatusCode().value();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statusCode;
    }

    public static int createEvent(Event event) {
        Log.d(TAG, "createEvent() event: " + event);

        HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.setAuthorization(authHeader);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setContentEncoding(ContentCodingType.valueOf("UTF-8"));
        HttpEntity<Event> entity = new HttpEntity<>(event, requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        int statusCode = -1;
        try {
            ResponseEntity response = restTemplate.exchange(NetworkUtilities.EVENTS_URL, HttpMethod.POST, entity, null);
            URI location = response.getHeaders().getLocation();
            String path = location.getPath();
            event.setServer_id(path.substring(location.getPath().lastIndexOf('/') + 1));
            Log.d(TAG, "createEvent() event uri : " + event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusCode;
    }

    public static int updateEvent(Event event) {
        Log.d(TAG, "updateEvent() event: " + event);
        String uri = NetworkUtilities.EVENTS_URL;
        int code = -1;//sendHttpPost(uri, event);
        return code;
    }

   /* private static int sendHttpGetForUserEvents(String uri, String response) {
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
    }*/

    /*private static int sendHttpDelete(String uri) {
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
    }*/

    /*private static int sendHttpPost(String uri, Event event) {
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
    }*/
}
