package imis.client.ui.fragments;

import android.app.Activity;
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
    OnDetailSelectedListener listener;

    public RecordListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnDetailSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnURLSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick()");
        listener.onDetailSelected(position);

    }

    public interface OnDetailSelectedListener {
        public void onDetailSelected(int position);
    }


}
