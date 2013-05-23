package imis.client.asynctasks;

import android.app.Activity;
import android.util.Log;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.asynctasks.result.ResultData;
import imis.client.model.Record;
import imis.client.network.HttpClientFactory;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.RecordManager;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 13.4.13
 * Time: 19:36
 */
public class GetListOfRecords extends NetworkingAsyncTask<String, Void, ResultData<Record>> {
    private static final String TAG = GetListOfRecords.class.getSimpleName();

    private Activity activity;

    public GetListOfRecords(Activity activity, String... params) {
        super(params);
        this.activity = activity;
    }

    @Override
    protected ResultData<Record> doInBackground(String... params) {
        String kodpra = params[0], from = params[1], to = params[2];

        HttpHeaders requestHeaders = new HttpHeaders();
        String username, password;
        try {
            username = AppUtil.getUserUsername(activity);
            password = AppUtil.getUserPassword(activity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;//TODO err msg
        }
        Log.d(TAG, "doInBackground() username " + username);
        Log.d(TAG, "doInBackground() password " + password);

        HttpAuthentication authHeader = new HttpBasicAuthentication(username, password);
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> entity = new HttpEntity<>(requestHeaders);

        //Create a Rest template
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        try {
            ResponseEntity<Record[]> response = restTemplate.exchange(NetworkUtilities.RECORDS_URL, HttpMethod.GET, entity,
                    Record[].class, kodpra, from, to);
            Record[] body = response.getBody();
            Log.d(TAG, "doInBackground() ok " + body);
            return new ResultData<Record>(response.getStatusCode(), body);
        } catch (Exception e) { //ResourceAccessException
            Log.d(TAG, e.getLocalizedMessage(), e);
            return new ResultData<Record>(activity.getString(R.string.service_unavailable));
        }
    }

    @Override
    protected void onPostExecute(ResultData<Record> resultData) {


        /*Log.d(TAG, "onPostExecute() records " + Arrays.toString(records));
        //TODO test data
        records = new Record[2];

        Record record = new Record();
        record.setId("123");
        record.setZc("I-VV-2013");
        record.setCpolzak(5);
        record.setCpozzak(11);
        record.setKodpra("KDA");
        record.setDatum(1364169600000L);
        record.setStav_v("V");
        record.setMnozstvi_odved(11111111);
        record.setPoznamka("bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké " +
                "3 bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké 3 bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké " + "\n" +
                "                \"3 bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké 3 bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké " + "\n"
        );
        record.setPozn_hl("poznamka hlavni poznamka hlavni poznamka hlavni poznamka hlavni");
        record.setPozn_ukol("poznamka ukol poznamka hlavni poznamka hlavni poznamka hlavni poznamka hlavni");
        records[0] = record;

        Record record2 = new Record();
        record2.setId("124");
        record2.setZc("A-VV-2013");
        record2.setCpolzak(5);
        record2.setCpozzak(11);
        record2.setKodpra("KDA");
        record2.setDatum(1364169600000L);
        record2.setStav_v("V");
        record2.setMnozstvi_odved(11111111);
        record2.setPoznamka("bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké " +
                "3 bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké 3 bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké " + "\n" +
                "                \"3 bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké 3 bylo těžké 0 bylo těžké 1 bylo těžké  2 bylo těžké " + "\n"
        );
        record2.setPozn_hl("poznamka hlavni poznamka hlavni poznamka hlavni poznamka hlavni");
        record2.setPozn_ukol("poznamka ukol poznamka hlavni poznamka hlavni poznamka hlavni poznamka hlavni");
        records[1] = record2;*/


        if (resultData.isUnknownErr()) {
            Log.d(TAG, "onPostExecute() isUnknownErr");
            AppUtil.showError(activity, resultData.getMsg());
        } else {
            Record[] records = resultData.getArray();
            if (records != null) {
                RecordManager.addRecords(activity, records);
                Log.d(TAG, "onPostExecute() getAllRecords size " + records.length + " " + RecordManager.getAllRecords(activity));
            } else {
                Log.d(TAG, "onPostExecute() empty");
                AppUtil.showInfo(activity, activity.getString(R.string.no_records));
            }
        }

        super.onPostExecute(null);
    }
}
