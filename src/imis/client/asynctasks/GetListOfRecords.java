package imis.client.asynctasks;

import android.content.Context;
import android.util.Log;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.asynctasks.result.ResultList;
import imis.client.authentication.AuthenticationUtil;
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
public class GetListOfRecords extends NetworkingAsyncTask<String, Void, ResultList<Record>> {
    private static final String TAG = GetListOfRecords.class.getSimpleName();

    public GetListOfRecords(Context context, String... params) {
        super(context, params);
    }

    @Override
    protected ResultList<Record> doInBackground(String... params) {
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
            ResponseEntity<Record[]> response = restTemplate.exchange(NetworkUtilities.RECORDS_URL, HttpMethod.GET, entity,
                    Record[].class, kodpra, from, to);
            Record[] body = response.getBody();
            Log.d(TAG, "doInBackground() ok " + body);
            return new ResultList<Record>(response.getStatusCode(), body);
        } catch (Exception e) { //ResourceAccessException
            Log.d(TAG, e.getLocalizedMessage(), e);
            return new ResultList<Record>(context.getString(R.string.service_unavailable));
        }
    }

    @Override
    protected void onPostExecute(ResultList<Record> resultList) {


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


        if (resultList.isUnknownErr()) {
            Log.d(TAG, "onPostExecute() isUnknownErr");
            AppUtil.showError(context, resultList.getMsg());
        } else {
            Record[] records = resultList.getArray();
            if (records != null) {
                RecordManager.addRecords(context, records);
                Log.d(TAG, "onPostExecute() getAllRecords size " + records.length + " " + RecordManager.getAllRecords(context));
            } else {
                Log.d(TAG, "onPostExecute() empty");
                AppUtil.showInfo(context, context.getString(R.string.no_records));
            }
        }

        super.onPostExecute(null);
    }
}
