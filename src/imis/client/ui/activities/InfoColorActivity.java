package imis.client.ui.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Activity showing color setting for types of events and records.
 */
public class InfoColorActivity extends Activity implements ColorPickerDialog.OnColorChangedListener {
    private static final String TAG = InfoColorActivity.class.getSimpleName();

    private RecordsColorAdapter recordsAdapter;
    private EventsColorAdapter eventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_info);

        populateRecordTypeList();
        populateEventTypeList();
    }

    private void populateRecordTypeList() {
        final List<Map.Entry<String, Integer>> list = new ArrayList<>();
        for (int i = 0; i < Record.TYPE_VALUES.length; i++) {
            String typeValue = Record.TYPE_VALUES[i];
            MyEntry<String, Integer> entry = new MyEntry<String, Integer>(typeValue, ColorConfig.getColor(this, typeValue));
            list.add(entry);
        }
        recordsAdapter = new RecordsColorAdapter(this, -1, list);
        ListView recordsList = (ListView) findViewById(R.id.recordTypeList);
        recordsList.setAdapter(recordsAdapter);
        recordsList.setOnItemLongClickListener(new MyOnLongItemClickListener());
    }

    private void populateEventTypeList() {
        final List<Map.Entry<String, Integer>> list = new ArrayList<>();
        for (int i = 0; i < Event.KOD_PO_VALUES.length; i++) {
            String typeValue = Event.KOD_PO_VALUES[i];
            MyEntry<String, Integer> entry = new MyEntry<String, Integer>(typeValue, ColorConfig.getColor(this, typeValue));
            list.add(entry);
        }
        eventsAdapter = new EventsColorAdapter(this, -1, list);
        ListView recordsList = (ListView) findViewById(R.id.eventstypeList);
        recordsList.setAdapter(eventsAdapter);
        recordsList.setOnItemLongClickListener(new MyOnLongItemClickListener());
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
        dialog.show(getFragmentManager(), "ColorPickerDialog");
    }

    final class MyEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public MyEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }

}
