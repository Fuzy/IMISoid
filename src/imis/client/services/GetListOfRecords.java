package imis.client.services;

import android.util.Log;
import imis.client.model.Record;
import imis.client.network.HttpClientFactory;
import imis.client.network.NetworkUtilities;
import imis.client.ui.activities.NetworkingActivity;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;

import static imis.client.ui.activities.ProgressState.DONE;
import static imis.client.ui.activities.ProgressState.RUNNING;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 13.4.13
 * Time: 19:36
 */
public class GetListOfRecords extends NetworkingService<String, Void, Record[]> {
    private static final String TAG = GetListOfRecords.class.getSimpleName();

    public GetListOfRecords(NetworkingActivity context) {
        super(context);
    }

    @Override
    protected Record[] doInBackground(String... params) {
        changeProgress(RUNNING, "working");
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
            ResponseEntity<Record[]> response = restTemplate.exchange(NetworkUtilities.RECORDS_URL, HttpMethod.GET, entity,
                    Record[].class, kodpra, from, to);
            Record[] body = response.getBody();
            return body;
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        } finally {
            changeProgress(DONE, null);
        }

        return new Record[]{};
    }

    @Override
    protected void onPostExecute(Record[] records) {
        Log.d(TAG, "onPostExecute() records " + Arrays.toString(records));
        super.onPostExecute(records);
    }
}
