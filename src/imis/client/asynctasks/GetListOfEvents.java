package imis.client.asynctasks;

import android.content.Context;
import android.util.Log;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.RestUtil;
import imis.client.asynctasks.result.ResultList;
import imis.client.asynctasks.util.AsyncUtil;
import imis.client.model.Event;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EventManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Asynchronous task for downloading list of attendance events.
 */
public class GetListOfEvents extends NetworkingAsyncTask<String, Void, ResultList<Event>> {
    private static final String TAG = GetListOfEvents.class.getSimpleName();

    public GetListOfEvents(Context context, String... params) {
        super(context, params);
    }

    @Override
    protected ResultList<Event> doInBackground(String... params) {
        String icp = params[0], from = params[1], to = params[2];

        HttpEntity<Object> entity = new HttpEntity<Object>(RestUtil.prepareHttpHeaders(context));
        RestTemplate restTemplate = RestUtil.prepareRestTemplate();

        try {
            ResponseEntity<Event[]> response = restTemplate.exchange(NetworkUtilities.getEventsGetURL(context), HttpMethod.GET, entity,
                    Event[].class, icp, from, to);
            Event[] body = response.getBody();
            return new ResultList<Event>(response.getStatusCode(), body);
        } catch (Exception e) {
            @SuppressWarnings("unchecked")
            ResultList<Event> resultItem = AsyncUtil.processException(context, e, ResultList.class);
            return resultItem;
        }
    }

    @Override
    protected void onPostExecute(ResultList<Event> resultList) {

        if (resultList.isOk()) {
            Log.d(TAG, "onPostExecute() OK");

            // Delete old data for employee
            String icp = params[0];
            EventManager.deleteEventsOnIcp(context, icp);

            if (!resultList.isEmpty()) {
                Event[] events = resultList.getArray();
                EventManager.addEvents(context, events);
            } else {
                AppUtil.showInfo(context, context.getString(R.string.no_records));
            }
        }

        super.onPostExecute(resultList);
    }
}
