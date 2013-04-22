package imis.client.network;

import android.util.Log;
import imis.client.AppUtil;
import imis.client.http.MyResponse;
import imis.client.model.Event;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.HttpServerErrorException;
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
            ResponseEntity response = restTemplate.exchange(NetworkUtilities.EVENTS_DELETE_URL,
                    HttpMethod.DELETE, entity, null, rowid);
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

    public static MyResponse createEvent(Event event) {
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
        String msg = null;
        try {
            ResponseEntity response = restTemplate.exchange(NetworkUtilities.EVENTS_URL, HttpMethod.POST, entity, null);
            URI location = response.getHeaders().getLocation();
            String path = location.getPath();
            event.setServer_id(path.substring(location.getPath().lastIndexOf('/') + 1));
            Log.d(TAG, "createEvent() event uri : " + event);
            statusCode = response.getStatusCode().value();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof HttpServerErrorException) {
                HttpServerErrorException ex = (HttpServerErrorException) e;
                msg = ex.getResponseBodyAsString();
            }
        }

        return new MyResponse(statusCode, msg);
    }

    public static MyResponse updateEvent(Event event) {
        Log.d(TAG, "updateEvent() event: " + event);

        HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.setAuthorization(authHeader);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setContentEncoding(ContentCodingType.valueOf("UTF-8"));
        HttpEntity<Event> entity = new HttpEntity<>(event, requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        int statusCode = -1;
        String msg = null;
        try {
            ResponseEntity response = restTemplate.exchange(NetworkUtilities.EVENTS_UPDATE_URL, HttpMethod.PUT,
                    entity, null, event.getServer_id());
            statusCode = response.getStatusCode().value();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof HttpServerErrorException) {
                HttpServerErrorException ex = (HttpServerErrorException) e;
                msg = ex.getResponseBodyAsString();
            }
        }
        return new MyResponse(statusCode, msg);
    }

}
