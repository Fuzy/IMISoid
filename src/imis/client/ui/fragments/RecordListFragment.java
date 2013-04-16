package imis.client.ui.fragments;

import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 15.4.13
 * Time: 17:29
 */
public class RecordListFragment extends ListFragment {
    private static final String TAG = "RecordListFragment";

    public RecordListFragment() {
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick()");
        // Do something with the data

    }



}
