package imis.client.syncadapter;

import android.util.Log;
import imis.client.AppUtil;
import imis.client.asynctasks.result.Result;
import imis.client.asynctasks.result.ResultData;
import imis.client.model.Event;
import imis.client.network.HttpClientFactory;
import imis.client.network.NetworkUtilities;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 13.4.13
 * Time: 19:29
 */
public class EventsSync {
    private static final String TAG = "EventsSync";

    public static Result deleteEvent(final String rowid) {
        Log.d(TAG, "deleteEvent() rowid: " + rowid);

        HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.setAuthorization(authHeader);
        HttpEntity<Object> entity = new org.springframework.http.HttpEntity<>(requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));

        try {
            ResponseEntity response = restTemplate.exchange(NetworkUtilities.EVENTS_DELETE_URL,
                    HttpMethod.DELETE, entity, null, rowid);
            return new Result(response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, e.getLocalizedMessage());
        }
    }

    public static ResultData<Event> getUserEvents(final String icp, final long from, final long to) {
        String strFrom = AppUtil.formatDate(from);
        String strTo = AppUtil.formatDate(to);
        Log.d(TAG, "getUserEvents() icp: " + icp + " strFrom: " + strFrom + " strTo:" + strTo);

        HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity entity = new org.springframework.http.HttpEntity<>(requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        try {
            ResponseEntity<Event[]> response = restTemplate.exchange(NetworkUtilities.EVENTS_GET_URL,
                    HttpMethod.GET, entity, Event[].class, icp, strFrom, strTo);
            Event[] events = response.getBody();
            Log.d(TAG, "getUserEvents() events.length " + events.length);
            return new ResultData(response.getStatusCode(), events);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultData(null, e.getLocalizedMessage());
        }
    }

    public static Result createEvent(Event event) {
        Log.d(TAG, "createEvent() event: " + event);

        HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.setAuthorization(authHeader);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setContentEncoding(ContentCodingType.valueOf("UTF-8"));
        HttpEntity<Event> entity = new HttpEntity<>(event, requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        try {
            ResponseEntity response = restTemplate.exchange(NetworkUtilities.EVENTS_URL, HttpMethod.POST, entity, null);
            URI location = response.getHeaders().getLocation();
            String path = location.getPath();
            event.setServer_id(path.substring(location.getPath().lastIndexOf('/') + 1));
            Log.d(TAG, "createEvent() event uri : " + event);
            return new Result(response.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof HttpServerErrorException) {
                HttpServerErrorException ex = (HttpServerErrorException) e;
                return new Result(null, ex.getResponseBodyAsString());
            }
            return new Result(null, null);
        }
    }

    public static Result updateEvent(Event event) {
        Log.d(TAG, "updateEvent() event: " + event);

        HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.setAuthorization(authHeader);
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setContentEncoding(ContentCodingType.valueOf("UTF-8"));
        HttpEntity<Event> entity = new HttpEntity<>(event, requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        try {
            ResponseEntity response = restTemplate.exchange(NetworkUtilities.EVENTS_UPDATE_URL, HttpMethod.PUT,
                    entity, null, event.getServer_id());
            return new Result(response.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof HttpServerErrorException) {
                HttpServerErrorException ex = (HttpServerErrorException) e;
                return new Result(null, ex.getResponseBodyAsString());
            }
            return new Result(null, null);
        }
    }

}
