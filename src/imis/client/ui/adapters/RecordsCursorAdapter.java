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
import imis.client.TimeUtil;
import imis.client.model.Record;
import imis.client.ui.ColorConfig;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 16.4.13
 * Time: 10:24
 */
public class RecordsCursorAdapter extends CursorAdapter {
    private static final String TAG = "RecordsCursorAdapter";
    private LayoutInflater inflater;
//    private ColorConfig colorConfig;


    public RecordsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        colorConfig = new ColorConfig(context);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d(TAG, "newView()");
        return inflater.inflate(R.layout.record_row, null);
    }

    @Override
    public Record getItem(int position) {
        return Record.cursorToRecord((Cursor) super.getItem(position));
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(TAG, "bindView()");
        Record record = Record.cursorToRecord(cursor);
        Log.d(TAG, "bindView() record " + record);

        String zc = (record.getZc() == null) ? "" : record.getZc();
        String cpolzak = (record.getCpolzak() == null) ? "" : "" + record.getCpolzak().intValue();
        String cpozzak = (record.getCpozzak() == null) ? "" : "" + record.getCpozzak().intValue();
        String identification = zc + "/" + cpolzak + "/" + cpozzak;
        String odvedeno = TimeUtil.formatTime(record.getMnozstvi_odved());
        String poznamka = record.getPoznamka();

        TextView tt = (TextView) view.findViewById(R.id.recordIdentification);
        tt.setText(identification);
        tt = (TextView) view.findViewById(R.id.time);
        tt.setText(odvedeno);
        tt = (TextView) view.findViewById(R.id.note);
        tt.setText(poznamka);
        tt = (TextView) view.findViewById(R.id.record_type);
        tt.setBackgroundColor(ColorConfig.getColor(context, record.recordType()));
    }
}
