package imis.client.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.*;
import android.widget.*;
import imis.client.R;
import imis.client.model.Block;
import imis.client.model.Employee;
import imis.client.model.Event;
import imis.client.persistent.EmployeeManager;
import imis.client.processor.BlockProcessor;
import imis.client.ui.ColorUtil;
import imis.client.ui.fragments.PieChartFragment;
import imis.client.ui.fragments.StackedBarFragment;
import imis.client.ui.fragments.StatisticsFragment;

import java.util.Arrays;
import java.util.List;

import static imis.client.AppUtil.convertToTime;
import static imis.client.AppUtil.formatAbbrDate;
import static imis.client.persistent.EventManager.EventQuery;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 7.4.13
 * Time: 14:43
 */
public class EventsChartActivity extends NetworkingActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener {
    private static final String TAG = EventsChartActivity.class.getSimpleName();
    //private static final String LAB = "label", FROM = "from", TO = "to";

    private List<Block> blockList;
    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    private final CheckBoxClickListener checkBoxClickListener = new CheckBoxClickListener();

    private static final int LOADER_EVENTS = 0x03;
    private static final int LOADER_EMPLOYEES = 0x04;

    private String[] kody_po_values;
    private String[] kody_po_desc;

    private static final int CALENDAR_ACTIVITY_FROM_CODE = 1;
    private static final int CALENDAR_ACTIVITY_TO_CODE = 2;

    private SimpleCursorAdapter adapter;

    //TODO spolecny predek grafovych aktivit
    private Spinner spinner;
    private ImageButton dateFromButton, dateToButton;
    private EditText dateFromEdit, dateToEdit;

    private Fragment current = null;

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


        kody_po_values = getResources().getStringArray(R.array.kody_po_values);
        kody_po_desc = getResources().getStringArray(R.array.kody_po_desc);

        for (String value : Event.KOD_PO_VALUES) {
            addCheckBox(value);
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

        TextView label = new TextView(getApplication());
        label.setBackgroundColor(ColorUtil.getColor(kod_po));
        label.setHeight((int) (15 * scale + 0.5f));
        label.setWidth((int) (15 * scale + 0.5f));
        label.setGravity(Gravity.CENTER);
        box.addView(label);

        check.setOnClickListener(checkBoxClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        switchFragment();
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
                blockList = BlockProcessor.eventsToMapOfBlocks(cursor);
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
        /*mSeries = (CategorySeries) savedState.getSerializable("current_series");
        mRenderer = (DefaultRenderer) savedState.getSerializable("current_renderer");*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()");
       /* outState.putSerializable("current_series", mSeries);
        outState.putSerializable("current_renderer", mRenderer);*/
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public String getLabelForCode(String kod_po) {
        if (kod_po.equals(Event.KOD_PO_OTHERS)) return Event.OTHERS;
        int index = Arrays.asList(kody_po_values).indexOf(kod_po);
        return kody_po_desc[index];
    }

    public String[] codesToTitles(String[] kod_po) {
        String[] titles = new String[kod_po.length];
        for (int i = 0; i < kod_po.length; i++) {
            titles[i] = getLabelForCode(kod_po[i]);
        }
        return titles;
    }

    public List<Block> getBlockList() {
        return blockList;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.network_activity_menu, menu);
        inflater.inflate(R.menu.switch_chart_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                return true;
            case R.id.switchFragment:
                switchFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void switchFragment() {
        Log.d(TAG, "switchFragment()");
        if (current == null || (current instanceof StatisticsFragment)) {
            switchToPieChartFragment();
        } else if (current instanceof PieChartFragment) {
            switchToStackedBarFragment();
        } else if (current instanceof StackedBarFragment) {
            switchToStatisticsFragment();
        }
        getSupportLoaderManager().restartLoader(LOADER_EVENTS, null, this);
    }

    private void switchToStackedBarFragment() {
        Log.d(TAG, "switchToStackedBarFragment()");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        StackedBarFragment barFragment = new StackedBarFragment();
        current = barFragment;
        ft.replace(R.id.displayChart, barFragment, "PieChartFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    private void switchToPieChartFragment() {
        Log.d(TAG, "switchToPieChartFragment()");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        PieChartFragment pieFragment = new PieChartFragment();
        current = pieFragment;
        ft.replace(R.id.displayChart, pieFragment, "PieChartFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    private void switchToStatisticsFragment() {
        Log.d(TAG, "switchToPieChartFragment()");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        StatisticsFragment statsFragment = new StatisticsFragment();
        current = statsFragment;
        ft.replace(R.id.displayChart, statsFragment, "StatisticsFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    private void refresh() {
        Log.d(TAG, "refresh()");
        //new GetEmployeesLastEvent(this).execute();
    }

    private class CheckBoxClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            CheckBox check = (CheckBox) view;
            Log.d(TAG, "onClick() " + view.getId());
            Log.d(TAG, "onClick() " + view.getId() + " is " + check.isChecked() + " kod " + Event.KOD_PO_VALUES[view.getId()]);
        }
    }
}
