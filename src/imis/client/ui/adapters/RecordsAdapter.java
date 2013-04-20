package imis.client.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import imis.client.R;
import imis.client.model.Record;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 16.4.13
 * Time: 10:05
 */
public class RecordsAdapter extends ArrayAdapter<Record> {
    private static final String TAG = "RecordsAdapter";

    public RecordsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView()");
        LayoutInflater vi = LayoutInflater.from(getContext());
        View view = vi.inflate(R.layout.record_row, null);
        Record record = getItem(position);//mActivity.getRecords().get(position);

        if (record != null) {

            TextView tt = (TextView) view.findViewById(R.id.recordIdentification);

            if (tt != null) {
                tt.setText(record.getZc());
            }
        }

        return view;
    }
}