package imis.client.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import imis.client.R;
import imis.client.model.Record;
import imis.client.ui.activities.RecordsChartActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 15.4.13
 * Time: 17:29
 */
public class RecordListFragment extends ListFragment {
    private static final String TAG = "RecordListFragment";

    private RecordsChartActivity mActivity;//TODO hayi null RecordListActivity
    //private SimpleAdapter adapter;
    private RecordsAdapter adapter;


    private DataSetObserver mObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            Log.d(TAG, "onChanged()");
            adapter.clear();
            adapter.addAll(mActivity.getRecords());
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (RecordsChartActivity) activity;
        mActivity.registerDataSetObserver(mObserver);
        Log.d(TAG, "onAttach() activity " + mActivity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*String[] from = new String[]{Record.COL_ZC, Record.COL_CPOLZAK};
        int[] to = new int[]{R.id.zc, R.id.cpolzak};*/
        //adapter = new ArrayAdapter(mActivity, )
        //new SimpleAdapter(mActivity, mActivity.getRecords(), R.layout.record_row, from, to);
        adapter = new RecordsAdapter(getActivity(), -1);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "onListItemClick()");
        // Do something with the data

    }

    private class RecordsAdapter extends ArrayAdapter<Record> {

        public RecordsAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d(TAG, "getView()");
            LayoutInflater vi = LayoutInflater.from(getContext());
            View view = vi.inflate(R.layout.record_row, null);
            Record record = mActivity.getRecords().get(position);

            if (record != null) {

                TextView tt = (TextView) view.findViewById(R.id.zc);

                if (tt != null) {
                    tt.setText(record.getZc());
                }
            }

            return view;
        }
    }
}
