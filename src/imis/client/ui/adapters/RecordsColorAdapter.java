package imis.client.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import imis.client.R;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 9.5.13
 * Time: 21:16
 */
public class RecordsColorAdapter extends ArrayAdapter<Map.Entry<String, Integer>> {
    private LayoutInflater inflater;


    public RecordsColorAdapter(Context context, int textViewResourceId, List<Map.Entry<String, Integer>> objects) {
        super(context, textViewResourceId, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.color_info_row, null);
        Map.Entry<String, Integer> entry = getItem(position);
        TextView textView = (TextView) view.findViewById(R.id.type);
        textView.setText(entry.getKey());
        TextView color = (TextView) view.findViewById(R.id.typeColor);
        color.setBackgroundColor(entry.getValue());
        return view;
    }
}
