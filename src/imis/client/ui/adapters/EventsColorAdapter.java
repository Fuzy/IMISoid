package imis.client.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import imis.client.R;
import imis.client.model.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 9.5.13
 * Time: 21:16
 */
public class EventsColorAdapter extends ArrayAdapter<Map.Entry<String, Integer>> {
    private static final String TAG = EventsColorAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Map<String, String> codes;


    public EventsColorAdapter(Context context, int textViewResourceId, List<Map.Entry<String, Integer>> objects) {
        super(context, textViewResourceId, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String[] kody_po_values = context.getResources().getStringArray(R.array.kody_po_values);
        String[] kody_po_desc = context.getResources().getStringArray(R.array.kody_po_desc);
        codes = new HashMap<>();
        for (int i = 0; i < kody_po_values.length; i++) {
            codes.put(kody_po_values[i], kody_po_desc[i]);
        }
        codes.put(Event.KOD_PO_OTHERS, context.getString(R.string.eventTypeOthers));
        Log.d(TAG, "EventsColorAdapter() codes " + codes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.color_info_row, null);
        Map.Entry<String, Integer> entry = getItem(position);
        TextView textView = (TextView) view.findViewById(R.id.type);
        textView.setText(codes.get(entry.getKey()));
        TextView color = (TextView) view.findViewById(R.id.typeColor);
        color.setBackgroundColor(entry.getValue());
        return view;
    }
}
