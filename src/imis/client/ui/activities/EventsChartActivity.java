package imis.client.ui.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.CheckBox;
import imis.client.R;
import imis.client.asynctasks.GetListOfEvents;
import imis.client.asynctasks.result.Result;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.StackedBarChartData;
import imis.client.model.Block;
import imis.client.model.Event;
import imis.client.processor.DataProcessor;
import imis.client.ui.ColorUtil;

import java.util.*;

import static imis.client.persistent.EventManager.EventQuery;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 7.4.13
 * Time: 14:43
 */
public class EventsChartActivity extends ChartActivity {
    private static final String TAG = EventsChartActivity.class.getSimpleName();

    private static final int LOADER_EVENTS = 0x03;
    private List<Block> blockList;
    private final Map<String, String> kody_po = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()" + savedInstanceState == null ? "true" : "false");
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
                return new CursorLoader(this, EventQuery.CONTENT_URI,
                        null, EventQuery.SELECTION_CHART, getSelectionArgs(), null);
            default:
                return super.onCreateLoader(i, bundle);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished()");
        switch (cursorLoader.getId()) {
            case LOADER_EMPLOYEES:
                Log.d(TAG, "onLoadFinished() LOADER_EMPLOYEES");
                super.onLoadFinished(cursorLoader, cursor);
                getSupportLoaderManager().initLoader(LOADER_EVENTS, null, this);
                break;
            case LOADER_EVENTS:
                Log.d(TAG, "onLoadFinished() LOADER_EVENTS");
                blockList = DataProcessor.eventsToMapOfBlocks(cursor);
                String[] values = DataProcessor.eventsCodesInBlocks(blockList);
                initCheckBoxes(values);
                mDataSetObservable.notifyChanged();
                break;
            default:
                super.onLoadFinished(cursorLoader, cursor);
                break;
        }

    }

    /*@Override
    protected String[] getSelectionArgs() {
        String[] args = super.getSelectionArgs();
        Employee employee = EmployeeManager.getEmployeeOnKodpra(this, args[0]);
        args[0] = employee.getIcp();
        Log.d(TAG, "getSelectionArgs() args " + Arrays.toString(args));
        return args;
    }*/

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

    @Override
    public List<String> getCheckedCodes() {
        List<String> codes = new ArrayList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) codes.add(Event.KOD_PO_VALUES[checkBox.getId()]);
        }
        Log.d(TAG, "getCheckedCodes() codes " + codes);
        return codes;
    }

    @Override
    protected void processControlAsyncTask(String kodpra, String from, String to) {
        createTaskFragment(new GetListOfEvents(this, kodpra, from, to));

    }

    @Override
    protected void processDataQuery() {
        Log.d(TAG, "processDataQuery()");
        getSupportLoaderManager().restartLoader(LOADER_EVENTS, null, this);
    }

    @Override
    public PieChartData getPieChartData() {
        Log.d(TAG, "getPieChartData()");
        PieChartData data = DataProcessor.countEventsPieChartData(blockList, getCheckedCodes(), kody_po);
        return data;
    }

    @Override
    public StackedBarChartData getStackedBarChartData() {
        Log.d(TAG, "getStackedBarChartData()");
        StackedBarChartData data = DataProcessor.countEventsStackedBarChartData(blockList, getCheckedCodes(), kody_po, selectionArgs);
        return data;
    }


    @Override
    public void onTaskFinished(Result result) {
        Log.d(TAG, "onTaskFinished()");
        //TODO
    }
}
