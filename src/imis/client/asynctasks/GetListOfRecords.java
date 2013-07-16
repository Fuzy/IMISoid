package imis.client.asynctasks;

import android.content.Context;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.asynctasks.result.ResultList;
import imis.client.asynctasks.util.AsyncUtil;
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
import java.util.HashMap;
import java.util.Map;

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

        ResultList<Record> resultList;
        HttpEntity<Object> entity;
        RestTemplate restTemplate;
        Map<String, Object> statistics = new HashMap<String, Object>();

        // get list of records
        entity = prepareEntity();
        restTemplate = prepareRestTemplate();
        try {
            ResponseEntity<Record[]> response = restTemplate.exchange(NetworkUtilities.getRecordsGetURL(context), HttpMethod.GET, entity,
                    Record[].class, kodpra, from, to);
            Record[] body = response.getBody();
            Log.d(TAG, "doInBackground() ok " + body);
            resultList = new ResultList<Record>(response.getStatusCode(), body);
        } catch (Exception e) {
            resultList = AsyncUtil.processException(context, e, ResultList.class);
            return resultList;
        }

        //get total time for records
        entity = prepareEntity();
        restTemplate = prepareRestTemplate();
        try {
            ResponseEntity<Long> response = restTemplate.exchange(NetworkUtilities.getRecordsTimeGetURL(context), HttpMethod.GET, entity,
                    Long.class, kodpra, from, to);
            long body = response.getBody();
            Log.d(TAG, "doInBackground() ok " + body);
            statistics.put(AppConsts.SUM_RECORDS_TIME, body);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //get total time for events
        entity = prepareEntity();
        restTemplate = prepareRestTemplate();
        try {
            ResponseEntity<Long> response = restTemplate.exchange(NetworkUtilities.getEventsTimeGetURL(context), HttpMethod.GET, entity,
                    Long.class, kodpra, from, to);
            long body = response.getBody();
            Log.d(TAG, "doInBackground() ok " + body);
            statistics.put(AppConsts.SUM_EVENTS_TIME, body);
        } catch (Exception e) {
            e.printStackTrace();
        }

        resultList.setStatistics(statistics);
        Log.d(TAG, "doInBackground() resultList " + resultList);
        return resultList;
    }

    private RestTemplate prepareRestTemplate() {  //TODO nekam jako util
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        return restTemplate;
    }

    private HttpEntity<Object> prepareEntity() {
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpAuthentication authHeader = AuthenticationUtil.createAuthHeader(context);
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> entity = new HttpEntity<>(requestHeaders);
        return entity;
    }

    @Override
    protected void onPostExecute(ResultList<Record> resultList) {

        if (resultList.isOk()) {
            Log.d(TAG, "onPostExecute() OK");
            String kodpra = params[0];
            RecordManager.deleteRecordsOnKodpra(context, kodpra);

            if (!resultList.isEmpty()) {
                Log.d(TAG, "onPostExecute() OK and not empty");
                Record[] records = resultList.getArray();
                if (records != null) {
                    RecordManager.addRecords(context, records);
                    Log.d(TAG, "onPostExecute() getAllRecords size " + records.length + " " + RecordManager.getAllRecords(context));
                }
            }
        }

        super.onPostExecute(resultList);
    }
}
