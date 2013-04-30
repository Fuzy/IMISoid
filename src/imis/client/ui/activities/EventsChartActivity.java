package imis.client.ui.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.CheckBox;
import imis.client.R;
import imis.client.asynctasks.GetListOfEvents;
import imis.client.asynctasks.result.ResultData;
import imis.client.authentication.AuthenticationConsts;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.StackedBarChartData;
import imis.client.model.Block;
import imis.client.model.Employee;
import imis.client.model.Event;
import imis.client.persistent.EmployeeManager;
import imis.client.processor.DataProcessor;
import imis.client.ui.ColorUtil;

import java.text.ParseException;
import java.util.*;

import static imis.client.AppUtil.showAccountNotExistsError;
import static imis.client.AppUtil.showPeriodInputError;
import static imis.client.persistent.EventManager.EventQuery;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 7.4.13
 * Time: 14:43
 */
public class EventsChartActivity extends ChartActivity {
    private static final String TAG = EventsChartActivity.class.getSimpleName();

    private List<Block> blockList;

    private static final int LOADER_EVENTS = 0x03;
    private static final int LOADER_EMPLOYEES = 0x04;

    private final Map<String, String> kody_po = new HashMap<>();

    private AccountManager accountManager;
    private SimpleCursorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()" + savedInstanceState == null ? "true" : "false");
        setContentView(R.layout.events_chart);
        getSupportLoaderManager().initLoader(LOADER_EMPLOYEES, null, this);
        getSupportLoaderManager().initLoader(LOADER_EVENTS, null, this);

        // create account manager
        accountManager = AccountManager.get(this);

        initControlPanel();
        initEventCodesAndDesc();

    }

    private void initEventCodesAndDesc() {
        String[] kody_po_values = getResources().getStringArray(R.array.kody_po_values);
        String[] kody_po_desc = getResources().getStringArray(R.array.kody_po_desc);
        for (int i = 0; i < kody_po_values.length; i++) {
            kody_po.put(kody_po_values[i], kody_po_desc[i]);
        }
    }

    protected void addCheckBox(String kod_po) {
        int index = Arrays.asList(Event.KOD_PO_VALUES).indexOf(kod_po);
        int color = ColorUtil.getColor(kod_po);
        addCheckBox(index, color);

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
                        null, null, null, null);
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
                String[] values = DataProcessor.eventsCodesInBlocks(blockList);
                initCheckBoxes(values);
                mDataSetObservable.notifyChanged();
                break;
            case LOADER_EMPLOYEES:
                Log.d(TAG, "onLoadFinished() LOADER_EMPLOYEES");
                String[] from = new String[]{Employee.COL_KODPRA};
                int[] to = new int[]{android.R.id.text1};
                adapter = new SimpleCursorAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,
                        cursor, from, to, 0);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEmp.setAdapter(adapter);
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
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    protected void refresh() {
        Log.d(TAG, "refresh()");
        Account[] accounts = accountManager.getAccountsByType(AuthenticationConsts.ACCOUNT_TYPE);

        try {
            String kodpra = accounts[0].name;
            String from = getDateFrom();
            String to = getDateTo();
            createTaskFragment(new GetListOfEvents(kodpra, from, to));
        } catch (ParseException e) {
            Log.d(TAG, "resfreshRecords() " + e.getMessage());
            showPeriodInputError(this);
        } catch (Exception e) {
            showAccountNotExistsError(this);
        }


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
        StackedBarChartData data = DataProcessor.countEventsStackedBarChartData(blockList, getVisibleCodes(), kody_po);
        return data;
    }


    @Override
    public void onTaskFinished(ResultData result) {

    }
}
