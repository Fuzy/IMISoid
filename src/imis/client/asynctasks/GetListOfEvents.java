package imis.client.asynctasks;

import android.app.Activity;
import android.util.Log;
import imis.client.model.Event;
import imis.client.network.HttpClientFactory;
import imis.client.network.NetworkUtilities;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 17.4.13
 * Time: 0:17
 */
public class GetListOfEvents extends NetworkingAsyncTask<String, Void, Event[]>{
    private static final String TAG = GetListOfRecords.class.getSimpleName();

    public GetListOfEvents(Activity context) {
        super(context);
    }

    @Override
    protected Event[] doInBackground(String... params) {
        //changeProgress(RUNNING, "working");
        String kodpra = params[0], from = params[1], to = params[2];

        HttpHeaders requestHeaders = new HttpHeaders();
        //requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> entity = new HttpEntity<>(requestHeaders);

        //Create a Rest template
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        try {
            Log.d(TAG, "doInBackground()");
            //TODO uri variables
            ResponseEntity<Event[]> response = restTemplate.exchange(NetworkUtilities.EVENTS_GET_URL, HttpMethod.GET, entity,
                    Event[].class, kodpra, from, to);
            Event[] body = response.getBody();
            return body;
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }

        return new Event[]{};
    }

    @Override
    protected void onPostExecute(Event[] events) {
        super.onPostExecute(null);
        Log.d(TAG, "onPostExecute() events " + Arrays.toString(events));

    }
}
