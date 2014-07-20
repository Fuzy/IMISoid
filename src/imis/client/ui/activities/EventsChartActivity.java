package imis.client.ui.activities;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import imis.client.AppUtil;
import imis.client.asynctasks.GetListOfEvents;
import imis.client.asynctasks.result.Result;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.StackedBarChartData;
import imis.client.model.Block;
import imis.client.model.Employee;
import imis.client.model.Event;
import imis.client.processor.EventsProcessor;
import imis.client.ui.ColorConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static imis.client.persistent.EventManager.EventQuery;

/**
 * Activity hosting chart and statistic for attendance events.
 */
public class EventsChartActivity extends ChartActivity {
    private static final String TAG = EventsChartActivity.class.getSimpleName();

    private static final int LOADER_EVENTS = 0x03;
    private List<Block> blockList;
    private Map<String, String> codes;
    private EventsProcessor processor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()" + savedInstanceState == null ? "true" : "false");
        processor = new EventsProcessor(getApplicationContext());
        codes = AppUtil.getCodes(this);
    }

    protected void addCheckBox(String kod_po) {
        int index = Arrays.asList(Event.KOD_PO_VALUES).indexOf(kod_po);
        int color = ColorConfig.getColor(this, kod_po);
        addCheckBox(index, color);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader()");
        switch (i) {
            case LOADER_EVENTS:
                return new CursorLoader(this, EventQuery.CONTENT_URI,
                        null, EventQuery.SELECTION_CHART, getSelectionArgs(), EventQuery.ORDER_BY_DATE_TIME_ASC);
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
                getLoaderManager().initLoader(LOADER_EVENTS, null, this);
                break;
            case LOADER_EVENTS:
                Log.d(TAG, "onLoadFinished() LOADER_EVENT size " + cursor.getCount());
                blockList = processor.eventsToMapOfBlocks(cursor);
                String[] values = processor.eventsCodesInBlocks(blockList);
                initCheckBoxes(values);
                mDataSetObservable.notifyChanged();
                break;
            default:
                super.onLoadFinished(cursorLoader, cursor);
                break;
        }

    }

    @Override
    protected String[] getSelectionArgs() {
        String[] args = new String[3];
        String icp = selectionArgs.get(PAR_EMP_ICP);
        args[0] = (icp == null) ? "" : icp;
        args[1] = selectionArgs.get(PAR_FROM);
        args[2] = selectionArgs.get(PAR_TO);
        Log.d(TAG, "getSelectionArgs() args " + Arrays.toString(args));
        return args;
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
    protected void processControlAsyncTask(Employee emp, String from, String to) {
        createTaskFragment(new GetListOfEvents(this, emp.getIcp(), from, to));

    }

    @Override
    protected void processDataQuery() {
        Log.d(TAG, "processDataQuery()");
        getLoaderManager().restartLoader(LOADER_EVENTS, null, this);
    }

    @Override
    public PieChartData getPieChartData() {
        Log.d(TAG, "getPieChartData()");
        PieChartData data = processor.countEventsPieChartData(blockList, getCheckedCodes(), codes);
        return data;
    }

    @Override
    public StackedBarChartData getStackedBarChartData() {
        Log.d(TAG, "getStackedBarChartData()");
        StackedBarChartData data = processor.countEventsStackedBarChartData(blockList, getCheckedCodes(), codes, selectionArgs);
        return data;
    }

    @Override
    public void onTaskFinished(Result result) {
        Log.d(TAG, "onTaskFinished()");
    }
}
