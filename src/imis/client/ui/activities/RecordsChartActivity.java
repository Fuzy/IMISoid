package imis.client.ui.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import imis.client.R;
import imis.client.asynctasks.GetListOfRecords;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.StackedBarChartData;
import imis.client.model.Record;
import imis.client.processor.DataProcessor;
import imis.client.ui.ColorUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static imis.client.persistent.RecordManager.DataQuery.CONTENT_URI;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 29.3.13
 * Time: 15:34
 */
public class RecordsChartActivity extends ChartActivity {
    private static final String TAG = RecordsChartActivity.class.getSimpleName();

    private final CheckBoxClickListener checkBoxClickListener = new CheckBoxClickListener();
    private static final int LOADER_RECORDS = 0x03;
    private List<Record> records = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("RecordsChartActivity", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_chart);
        getSupportLoaderManager().initLoader(LOADER_RECORDS, null, this);

        for (String value : Record.TYPE_VALUES) {
            addCheckBox(value);
        }

    }

    private void addCheckBox(String kod_po) {
        int index = Arrays.asList(Record.TYPE_VALUES).indexOf(kod_po);
        final float scale = getApplication().getResources().getDisplayMetrics().density;
        LinearLayout box = (LinearLayout) findViewById(R.id.checkBoxesBox);

        CheckBox check = new CheckBox(getApplication());
        check.setId(index);
        check.setChecked(true);
        box.addView(check);
        checkBoxes.add(check);

        TextView label = new TextView(getApplication());
        label.setBackgroundColor(ColorUtil.getColor(kod_po));
        label.setHeight((int) (15 * scale + 0.5f));
        label.setWidth((int) (15 * scale + 0.5f));
        label.setGravity(Gravity.CENTER);
        box.addView(label);

        check.setOnClickListener(checkBoxClickListener);
    }

    @Override
    protected void refresh() {
        Log.d("RecordsChartActivity", "resfreshRecords()");
        String kodpra = "JEL";
        String from = "26.03.08";//TODO pryc
        String to = "26.03.08";

        new GetListOfRecords(this).execute(kodpra, from, to);
    }

    @Override
    protected void restartLoaders() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PieChartData getPieChartData() {
        PieChartData data = DataProcessor.countRecordsPieChartData(records, getVisibleCodes());
        return data;
    }

    @Override
    public StackedBarChartData getStackedBarChartData() {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

/*
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }*/

    /*public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
        mDataSetObservable.notifyChanged();
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader()");
        switch (i) {
            case LOADER_RECORDS:
                return new CursorLoader(getApplicationContext(), CONTENT_URI,
                        null, null, null, null);
            //TODO selekce EventQuery.SELECTION_DATUM, new String[]{String.valueOf(date)},

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished()");
        int id = cursorLoader.getId();
        switch (id) {
            case LOADER_RECORDS:
                records.clear();
                while (cursor.moveToNext()) {
                    Record record = Record.cursorToRecord(cursor);
                    records.add(record);
                }
                mDataSetObservable.notifyChanged();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<String> getVisibleCodes() {
        List<String> codes = new ArrayList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) codes.add(Record.TYPE_VALUES[checkBox.getId()]);
        }
        Log.d(TAG, "getVisibleCodes() codes " + codes);
        return codes;
    }

    private class CheckBoxClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            CheckBox check = checkBoxes.get(view.getId());
            Log.d(TAG, "onClick() " + view.getId() + " is " +
                    check.isChecked() + " kod " + Record.TYPE_VALUES[view.getId()]);
            refreshCurrentFragment();
        }
    }

}
