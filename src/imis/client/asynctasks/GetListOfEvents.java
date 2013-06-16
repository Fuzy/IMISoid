package imis.client.asynctasks;

import android.content.Context;
import android.util.Log;
import imis.client.asynctasks.result.ResultList;
import imis.client.asynctasks.util.AsyncUtil;
import imis.client.authentication.AuthenticationUtil;
import imis.client.model.Event;
import imis.client.network.HttpClientFactory;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EventManager;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 17.4.13
 * Time: 0:17
 */
public class GetListOfEvents extends NetworkingAsyncTask<String, Void, ResultList<Event>> {
    private static final String TAG = GetListOfRecords.class.getSimpleName();

    public GetListOfEvents(Context context, String... params) {
        super(context, params);
    }

    @Override
    protected ResultList<Event> doInBackground(String... params) {
        String kodpra = params[0], from = params[1], to = params[2];

        HttpHeaders requestHeaders = new HttpHeaders();
        HttpAuthentication authHeader = AuthenticationUtil.createAuthHeader(context);
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> entity = new HttpEntity<>(requestHeaders);

        //Create a Rest template
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        try {
            ResponseEntity<Event[]> response = restTemplate.exchange(NetworkUtilities.EVENTS_GET_URL, HttpMethod.GET, entity,
                    Event[].class, kodpra, from, to);
            Event[] body = response.getBody();
            Log.d(TAG, "doInBackground() body " + body);
            return new ResultList<Event>(response.getStatusCode(), body);
        } catch (Exception e) {
            ResultList<Event> resultItem = AsyncUtil.processException(e, ResultList.class);
            Log.d(TAG, "doInBackground() resultItem " + resultItem);
            return resultItem;
        }
    }

    @Override
    protected void onPostExecute(ResultList<Event> resultList) {

        if (resultList.isOk() && !resultList.isEmpty()) {
            Log.d(TAG, "onPostExecute() OK and not empty");
            Event[] events = resultList.getArray();
            EventManager.addEvents(context, events);
        }

        super.onPostExecute(resultList);
    }
}
