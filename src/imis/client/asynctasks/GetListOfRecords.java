package imis.client.asynctasks;

import android.app.Activity;
import android.util.Log;
import imis.client.model.Record;
import imis.client.network.HttpClientFactory;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.RecordManager;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 13.4.13
 * Time: 19:36
 */
public class GetListOfRecords extends NetworkingAsyncTask<String, Void, Record[]> {
    private static final String TAG = GetListOfRecords.class.getSimpleName();

    public GetListOfRecords(Activity context) {
        super(context);
    }

    @Override
    protected Record[] doInBackground(String... params) {
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
            ResponseEntity<Record[]> response = restTemplate.exchange(NetworkUtilities.RECORDS_URL, HttpMethod.GET, entity,
                    Record[].class, kodpra, from, to);
            Record[] body = response.getBody();
            return body;
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        } /*finally {
            changeProgress(DONE, null);
        }*/

        return new Record[]{};
    }

    @Override
    protected void onPostExecute(Record[] records) {
        super.onPostExecute(null);


        /*Log.d(TAG, "onPostExecute() records " + Arrays.toString(records));
        //TODO test data
        records = new Record[1];

        Record record = new Record();
        record.setZc("R-VV-2013");
        record.setCpolzak(5);
        record.setCpozzak(11);
        record.setKodpra("KDA");
        record.setDatum(454);
        record.setStav_v("V");
        record.setMnozstvi_odved(11111111);
        record.setPoznamka("bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké " +
                "3 bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké 3 bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké " + "\n" +
                "                \"3 bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké 3 bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké " + "\n"
        );
        record.setPozn_hl("poznamka hlavni poznamka hlavni poznamka hlavni poznamka hlavni");
        record.setPozn_ukol("poznamka ukol poznamka hlavni poznamka hlavni poznamka hlavni poznamka hlavni");
        records[0] = record;*/

       /* Record record2 = new Record();
        record2.setZc("456");*/


        //records[1] = record2;
        Log.d(TAG, "onPostExecute() records size " + records.length);

        RecordManager.addRecords(activity, Arrays.asList(records));
        Log.d(TAG, "onPostExecute() getAllRecords " + RecordManager.getAllRecords(activity));
    }
}
