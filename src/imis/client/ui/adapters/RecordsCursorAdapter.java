package imis.client.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import imis.client.R;
import imis.client.model.Record;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 16.4.13
 * Time: 10:24
 */
public class RecordsCursorAdapter extends CursorAdapter {
    private static final String TAG = "RecordsCursorAdapter";

    public RecordsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d(TAG, "newView()");
        LayoutInflater vi = LayoutInflater.from(context);
        View view = vi.inflate(R.layout.record_row, null);
        Record record = Record.cursorToRecord(cursor);
        if (record != null) {

            TextView tt = (TextView) view.findViewById(R.id.zc);

            if (tt != null) {
                tt.setText(record.getZc());
            }
        }
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    }
}
