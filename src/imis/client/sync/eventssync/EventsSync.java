package imis.client.sync.eventssync;

import android.content.Context;
import android.util.Log;
import imis.client.TimeUtil;
import imis.client.RestUtil;
import imis.client.asynctasks.result.Result;
import imis.client.asynctasks.result.ResultList;
import imis.client.asynctasks.util.AsyncUtil;
import imis.client.model.Event;
import imis.client.network.NetworkUtilities;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 13.4.13
 * Time: 19:29
 */
public class EventsSync {
    private static final String TAG = EventsSync.class.getSimpleName();

    private final Context context;
    public static final String KEY_SYNC_RESULT = "KEY_SYNC_RESULT";

    public EventsSync(Context context) {
        this.context = context;
    }

    public Result deleteEvent(final String rowid) {
        Log.d(TAG, "delete() rowid: " + rowid);

        HttpEntity<Object> entity = new HttpEntity<Object>(RestUtil.prepareHttpHeaders(context));
        RestTemplate restTemplate = RestUtil.prepareRestTemplate();

        try {
            ResponseEntity response = restTemplate.exchange(NetworkUtilities.getEventsDeleteURL(context),
                    HttpMethod.DELETE, entity, null, rowid);
            return new Result(response.getStatusCode());
        } catch (Exception e) {
            Result result = AsyncUtil.processException(context, e, Result.class);
            return result;
        }
    }

    public ResultList<Event> getUserEvents(final String icp, final long from, final long to) {
        String strFrom = TimeUtil.formatDate(from);
        String strTo = TimeUtil.formatDate(to);
        Log.d(TAG, "getUserEvents() icp: " + icp + " strFrom: " + strFrom + " strTo: " + strTo);

        HttpEntity<Object> entity = new HttpEntity<Object>(RestUtil.prepareHttpHeaders(context));
        RestTemplate restTemplate = RestUtil.prepareRestTemplate();

        try {
            ResponseEntity<Event[]> response = restTemplate.exchange(NetworkUtilities.getEventsGetURL(context),
                    HttpMethod.GET, entity, Event[].class, icp, strFrom, strTo);
            Event[] events = response.getBody();
            Log.d(TAG, "getUserEvents() events " + events);
            return new ResultList<Event>(response.getStatusCode(), events);
        } catch (Exception e) {
            @SuppressWarnings("unchecked")
            ResultList<Event> resultList = AsyncUtil.processException(context, e, ResultList.class);
            return resultList;
        }
    }

    public Result createEvent(Event event) {
        Log.d(TAG, "createEvent() event: " + event);

        HttpEntity<Event> entity = new HttpEntity<>(event, RestUtil.prepareHttpHeaders(context));
        RestTemplate restTemplate = RestUtil.prepareRestTemplate();

        try {
            ResponseEntity response = restTemplate.exchange(NetworkUtilities.getEventsCreateURL(context), HttpMethod.POST, entity, null);
            URI location = response.getHeaders().getLocation();
            String path = location.getPath();
            event.setServer_id(path.substring(location.getPath().lastIndexOf('/') + 1));
            Log.d(TAG, "createEvent() event uri : " + location.getPath());
            return new Result(response.getStatusCode());
        } catch (Exception e) {
            Result result = AsyncUtil.processException(context, e, Result.class);
            return result;
        }

    }

    public Result updateEvent(Event event) {
        Log.d(TAG, "updateEvent() event: " + event);

        HttpEntity<Event> entity = new HttpEntity<>(event, RestUtil.prepareHttpHeaders(context));
        RestTemplate restTemplate = RestUtil.prepareRestTemplate();

        try {
            ResponseEntity response = restTemplate.exchange(NetworkUtilities.getEventsUpdateURL(context), HttpMethod.PUT,
                    entity, null, event.getServer_id());
            return new Result(response.getStatusCode());
        } catch (Exception e) {
            Result result = AsyncUtil.processException(context, e, Result.class);
            return result;
        }
    }

}
