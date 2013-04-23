package imis.client.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import imis.client.R;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.StackedBarChartData;
import imis.client.model.Block;
import imis.client.model.Employee;
import imis.client.model.Event;
import imis.client.persistent.EmployeeManager;
import imis.client.processor.DataProcessor;
import imis.client.ui.ColorUtil;

import java.util.*;

import static imis.client.AppUtil.convertToTime;
import static imis.client.AppUtil.formatAbbrDate;
import static imis.client.persistent.EventManager.EventQuery;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 7.4.13
 * Time: 14:43
 */
public class EventsChartActivity extends ChartActivity {
    private static final String TAG = EventsChartActivity.class.getSimpleName();
    //private static final String LAB = "label", FROM = "from", TO = "to";

    private final CheckBoxClickListener checkBoxClickListener = new CheckBoxClickListener();
    private List<Block> blockList;

    private static final int LOADER_EVENTS = 0x03;
    private static final int LOADER_EMPLOYEES = 0x04;

    private final Map<String, String> kody_po = new HashMap<>();

    //TODO spolecne
    private static final int CALENDAR_ACTIVITY_FROM_CODE = 1;
    private static final int CALENDAR_ACTIVITY_TO_CODE = 2;

    private SimpleCursorAdapter adapter;

    //TODO spolecny predek grafovych aktivit
    private Spinner spinner;
    private ImageButton dateFromButton, dateToButton;
    private EditText dateFromEdit, dateToEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()" + savedInstanceState == null ? "true" : "false");
        setContentView(R.layout.events_chart);
        getSupportLoaderManager().initLoader(LOADER_EMPLOYEES, null, this);
        getSupportLoaderManager().initLoader(LOADER_EVENTS, null, this);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        dateFromEdit = (EditText) findViewById(R.id.dateFromEdit);
        dateToEdit = (EditText) findViewById(R.id.dateDayButton);
        dateFromButton = (ImageButton) findViewById(R.id.dateFromButton);
        dateFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCalendarActivity(convertToTime(dateFromEdit.getText().toString()), CALENDAR_ACTIVITY_FROM_CODE);
            }
        });
        dateToButton = (ImageButton) findViewById(R.id.dateMonthButton);
        dateToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startCalendarActivity(convertToTime(dateToEdit.getText().toString()), CALENDAR_ACTIVITY_TO_CODE);
            }
        });

        initEventCodesAndDesc();

        for (String value : Event.KOD_PO_VALUES) {
            addCheckBox(value);
        }
    }

    private void initEventCodesAndDesc() {
        String[] kody_po_values = getResources().getStringArray(R.array.kody_po_values);
        String[] kody_po_desc = getResources().getStringArray(R.array.kody_po_desc);
        for (int i = 0; i < kody_po_values.length; i++) {
            kody_po.put(kody_po_values[i], kody_po_desc[i]);
        }
    }

    private void addCheckBox(String kod_po) {
        int index = Arrays.asList(Event.KOD_PO_VALUES).indexOf(kod_po);
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader()");
        switch (i) {
            case LOADER_EVENTS:
                return new CursorLoader(getApplicationContext(), EventQuery.CONTENT_URI,
                        EventQuery.PROJECTION_ALL, null, null, null);//TODO selekce EventQuery.SELECTION_DATUM, new String[]{String.valueOf(date)},
            case LOADER_EMPLOYEES:
                return new CursorLoader(getApplicationContext(), EmployeeManager.DataQuery.CONTENT_URI,
                        EmployeeManager.DataQuery.PROJECTION_ALL,
                        null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished()"); //TODO pozor na pozici cursoru
        int id = cursorLoader.getId();
        switch (id) {
            case LOADER_EVENTS:
                Log.d(TAG, "onLoadFinished() LOADER_EVENTS");
                blockList = DataProcessor.eventsToMapOfBlocks(cursor);
                mDataSetObservable.notifyChanged();
                break;
            case LOADER_EMPLOYEES:
                Log.d(TAG, "onLoadFinished() LOADER_EMPLOYEES");
                String[] from = new String[]{Employee.COL_KODPRA};
                int[] to = new int[]{android.R.id.text1};
                adapter = new SimpleCursorAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,
                        cursor, from, to, 0);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                break;
        }

    }

    private void startCalendarActivity(long actual, int code) {
        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putExtra(Event.KEY_DATE, actual);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CALENDAR_ACTIVITY_FROM_CODE:
                if (resultCode == RESULT_OK) {
                    long date = data.getLongExtra(Event.KEY_DATE, -1);
                    dateFromEdit.setText(formatAbbrDate(date));
                }
                break;
            case CALENDAR_ACTIVITY_TO_CODE:
                if (resultCode == RESULT_OK) {
                    long date = data.getLongExtra(Event.KEY_DATE, -1);
                    dateToEdit.setText(formatAbbrDate(date));
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(TAG, "onLoaderReset()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        Log.d(TAG, "onRestoreInstanceState()");
        super.onRestoreInstanceState(savedState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()");
    }

    public List<String> getVisibleCodes() {
        List<String> codes = new ArrayList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) codes.add(Event.KOD_PO_VALUES[checkBox.getId()]);
        }
        Log.d(TAG, "getVisibleCodes() codes " + codes);
        return codes;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selected = adapterView.getItemAtPosition(i).toString();
        Log.d(TAG, "onItemSelected() selected " + selected + " l" + l);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void refresh() {
        Log.d(TAG, "refresh()");
        //new GetEmployeesLastEvent(this).execute();
    }

    @Override
    protected void restartLoaders() {
        getSupportLoaderManager().restartLoader(LOADER_EVENTS, null, this);
    }

    @Override
    public PieChartData getPieChartData() {
        Log.d(TAG, "getPieChartData()");
        PieChartData data = DataProcessor.countEventsPieChartData(blockList, getVisibleCodes(), kody_po);
        return data;
    }

    @Override
    public StackedBarChartData getStackedBarChartData() {
        Log.d(TAG, "getStackedBarChartData()");
        StackedBarChartData data =  DataProcessor.countEventsStackedBarChartData(blockList, getVisibleCodes(), kody_po);
        return data;
    }

    private class CheckBoxClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            CheckBox check = checkBoxes.get(view.getId());
            Log.d(TAG, "onClick() " + view.getId() + " is " +
                    check.isChecked() + " kod " + Event.KOD_PO_VALUES[view.getId()]);
            refreshCurrentFragment();
        }
    }

}
