package imis.client.ui.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import imis.client.model.Event;
import imis.client.processor.DataProcessor;
import imis.client.ui.activities.DayTimelineActivity;
import imis.client.ui.adapters.EventsCursorAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 15.6.13
 * Time: 10:40
 */
public class DayTimelineListFragment extends ListFragment {
    private static final String TAG = DayTimelineListFragment.class.getSimpleName();

    private DayTimelineActivity mActivity;
    private EventsCursorAdapter adapter;

    public DayTimelineListFragment() {
        Log.d(TAG, "DayTimelineListFragment()");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach()");
        mActivity = (DayTimelineActivity) activity;
        mActivity.registerDataSetObserver(mObserver);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new EventsCursorAdapter(mActivity, null, -1);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick()");
        startEditActivity(position);
    }

    private void startEditActivity(int position) {
        Event actEvent, scndEvent = null;
        int arriveId = -1, leaveId = -1;
        actEvent = adapter.getItem(position);
        Cursor cursor = adapter.getCursor();
        if (actEvent.isDruhArrival()) {
            arriveId = actEvent.get_id();
            scndEvent = DataProcessor.getNextEvent(cursor, Event.DRUH_LEAVE);
            if (scndEvent != null) leaveId = scndEvent.get_id();
        } else if (actEvent.isDruhLeave()) {
            leaveId = actEvent.get_id();
            scndEvent = DataProcessor.getPrevEvent(cursor, Event.DRUH_ARRIVAL);
            if (scndEvent != null) arriveId = scndEvent.get_id();
        }
       /* Log.d(TAG, "onListItemClick() actEvent " + actEvent);
        Log.d(TAG, "onListItemClick() scndEvent " + scndEvent);
        Log.d(TAG, "startEditActivity() arriveId " + arriveId);
        Log.d(TAG, "startEditActivity() leaveId " + leaveId);*/
        mActivity.startEditActivity(arriveId, leaveId);
    }

    private DataSetObserver mObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            Log.d(TAG, "onChanged()");
            adapter.swapCursor(mActivity.getCursor());
            Log.d(TAG, "onChanged() mActivity.getCursor() " + mActivity.getCursor().getCount());
            Log.d(TAG, "onChanged() adapter " + adapter.getCount());
        }
    };
}
