package imis.client.ui.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import imis.client.AppConsts;
import imis.client.R;
import imis.client.model.Event;
import imis.client.model.Record;
import imis.client.ui.ColorConfig;
import imis.client.ui.adapters.EventsColorAdapter;
import imis.client.ui.adapters.RecordsColorAdapter;
import imis.client.ui.dialogs.ColorPickerDialog;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 9.5.13
 * Time: 20:38
 */
public class InfoColorActivity extends FragmentActivity implements ColorPickerDialog.OnColorChangedListener {
    private static final String TAG = InfoColorActivity.class.getSimpleName();

    private RecordsColorAdapter recordsAdapter;
    private EventsColorAdapter eventsAdapter;
//    private ColorConfig colorConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_info);

//        colorConfig = new ColorConfig(getApplicationContext());
        populateRecordTypeList();
        populateEventTypeList();
    }

    private void populateRecordTypeList() {
        final List<Map.Entry<String, Integer>> list = entryListForKeys(Record.TYPE_VALUES);
        recordsAdapter = new RecordsColorAdapter(this, -1, list);
        ListView recordsList = (ListView) findViewById(R.id.recordTypeList);
        recordsList.setAdapter(recordsAdapter);
        recordsList.setOnItemLongClickListener(new MyOnLongItemClickListener());
    }

    private void populateEventTypeList() {
        final List<Map.Entry<String, Integer>> list = entryListForKeys(Event.KOD_PO_VALUES);
        eventsAdapter = new EventsColorAdapter(this, -1, list);
        ListView recordsList = (ListView) findViewById(R.id.eventstypeList);
        recordsList.setAdapter(eventsAdapter);
        recordsList.setOnItemLongClickListener(new MyOnLongItemClickListener());
    }

    private List<Map.Entry<String, Integer>> entryListForKeys(String[] keys) {
        Map<String, Integer> colors = new TreeMap<>();
        colors.putAll(ColorConfig.getColors(getApplicationContext()));

        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList(keys));
        colors.keySet().retainAll(set);
        return new ArrayList<Map.Entry<String, Integer>>(colors.entrySet());
    }

    @Override
    public void colorChanged() {
        populateEventTypeList();
        populateRecordTypeList();
    }

    private class MyOnLongItemClickListener implements ListView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            Map.Entry<String, Integer> entry;

            switch (adapterView.getId()) {
                case R.id.recordTypeList:
                    entry = recordsAdapter.getItem(i);
                    break;
                case R.id.eventstypeList:
                    entry = eventsAdapter.getItem(i);
                    break;
                default:
                    return false;
            }
            showColorChangeDialog(entry.getKey());
            return true;
        }
    }

    private void showColorChangeDialog(String key) {
        DialogFragment dialog = new ColorPickerDialog();
        Bundle bundle = new Bundle();
        bundle.putString(AppConsts.KEY_TYPE, key);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "ColorPickerDialog");
    }

}
