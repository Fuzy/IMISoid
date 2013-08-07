package imis.client.asynctasks;

import android.content.Context;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.RestUtil;
import imis.client.asynctasks.result.ResultList;
import imis.client.asynctasks.util.AsyncUtil;
import imis.client.model.Record;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.RecordManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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

    @SuppressWarnings("unchecked")
    @Override
    protected ResultList<Record> doInBackground(String... params) {
        String icp = params[0], kodpra = params[1], from = params[2], to = params[3];

        ResultList<Record> resultList;
        HttpEntity<Object> entity;
        RestTemplate restTemplate;
        Map<String, Object> statistics = new HashMap<String, Object>();

        // get list of records
        entity = new HttpEntity<Object>(RestUtil.prepareHttpHeaders(context));

        restTemplate = RestUtil.prepareRestTemplate();
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
        entity = new HttpEntity<Object>(RestUtil.prepareHttpHeaders(context));

        restTemplate = RestUtil.prepareRestTemplate();
        try {
            ResponseEntity<Long> response = restTemplate.exchange(NetworkUtilities.getRecordsTimeGetURL(context), HttpMethod.GET, entity,
                    Long.class, icp, from, to);
            long body = response.getBody();
            Log.d(TAG, "doInBackground() ok " + body);
            statistics.put(AppConsts.SUM_RECORDS_TIME, body);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //get total time for events
        entity = new HttpEntity<Object>(RestUtil.prepareHttpHeaders(context));

        restTemplate = RestUtil.prepareRestTemplate();
        try {
            ResponseEntity<Long> response = restTemplate.exchange(NetworkUtilities.getEventsTimeGetURL(context), HttpMethod.GET, entity,
                    Long.class, icp, from, to);
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
