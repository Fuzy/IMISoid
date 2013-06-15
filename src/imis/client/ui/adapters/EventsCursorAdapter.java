package imis.client.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.model.Event;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 15.6.13
 * Time: 11:02
 */
public class EventsCursorAdapter extends CursorAdapter {
    private static final String TAG = EventsCursorAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private String[] values;
    private String[] desc;

    public EventsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        values = context.getResources().getStringArray(R.array.kody_po_values);
        desc = context.getResources().getStringArray(R.array.kody_po_desc);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        Log.d(TAG, "newView()");
        return inflater.inflate(R.layout.event_row, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(TAG, "bindView()");
        Event event = Event.cursorToEvent(cursor);
        int i = Arrays.asList(values).indexOf(event.getKod_po());
        String description = desc[i];
        TextView tt = (TextView) view.findViewById(R.id.event_type);
        String type = event.getDruh() + " " + description;
        tt.setText(type);
        tt = (TextView) view.findViewById(R.id.event_time);
        String time = AppUtil.formatEmpDate(event.getDatum())
                + " " + AppUtil.formatTime(event.getCas());
        tt.setText(time);

    }
}
