package imis.client.ui.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.CheckBox;
import imis.client.asynctasks.GetListOfRecords;
import imis.client.asynctasks.result.Result;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.StackedBarChartData;
import imis.client.model.Record;
import imis.client.processor.DataProcessor;
import imis.client.ui.ColorUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static imis.client.AppUtil.showAccountNotExistsError; //TODO staticke importy
import static imis.client.AppUtil.showPeriodInputError;
import static imis.client.persistent.RecordManager.DataQuery.CONTENT_URI;
import static imis.client.persistent.RecordManager.DataQuery.SELECTION_CHART;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 29.3.13
 * Time: 15:34
 */
public class RecordsChartActivity extends ChartActivity {
    private static final String TAG = RecordsChartActivity.class.getSimpleName();

    private static final int LOADER_RECORDS = 0x03;
    private List<Record> records = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("RecordsChartActivity", "onCreate()");
        super.onCreate(savedInstanceState);
    }

    protected void addCheckBox(String kod_po) {
        int index = Arrays.asList(Record.TYPE_VALUES).indexOf(kod_po);
        int color = ColorUtil.getColor(kod_po);
        addCheckBox(index, color);
    }


    @Override
    protected void processNetworkTask() {
        Log.d("RecordsChartActivity", "resfreshRecords()");

        try {
            String kodpra = getSelectedUser();
            String from = getStringDateFrom();
            String to = getStringDateTo();
            createTaskFragment(new GetListOfRecords(this, kodpra, from, to));
        } catch (ParseException e) {
            Log.d(TAG, "resfreshRecords() " + e.getMessage());
            showPeriodInputError(this);
        } catch (Exception e) {
            showAccountNotExistsError(this);
        }
    }

    /*@Override
    protected void restartLoaders() {
        Log.d(TAG, "restartLoaders()");
        getSupportLoaderManager().restartLoader(LOADER_RECORDS, null, this);
    }*/

    @Override
    public PieChartData getPieChartData() {
        PieChartData data = DataProcessor.countRecordsPieChartData(records, getCheckedCodes());
        return data;
    }

    @Override
    public StackedBarChartData getStackedBarChartData() {
        StackedBarChartData data = DataProcessor.countRecordsStackedBarChartData(records, getCheckedCodes(), selectionArgs);
        return data;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader()");
        switch (i) {
            case LOADER_RECORDS:
                Log.d(TAG, "onCreateLoader() SELECTION_CHART " + SELECTION_CHART);
                return new CursorLoader(this, CONTENT_URI, null, SELECTION_CHART, getSelectionArgs(), null);
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
                getSupportLoaderManager().initLoader(LOADER_RECORDS, null, this);
                break;
            case LOADER_RECORDS:
                records.clear();
                while (cursor.moveToNext()) {
                    Record record = Record.cursorToRecord(cursor);
                    records.add(record);
                }
                String[] values = DataProcessor.recordsCodesInRecords(records);
                initCheckBoxes(values);
                mDataSetObservable.notifyChanged();
                break;
            default:
                super.onLoadFinished(cursorLoader, cursor);
        }
    }

    /*@Override
    protected String[] getSelectionArgs() {
        String[] args = new String[3];
        args[0] = selectionArgs.get(PAR_EMP);
        args[1] = selectionArgs.get(PAR_FROM);
        args[2] = selectionArgs.get(PAR_TO);
        Log.d(TAG, "getSelectionArgs() args " + Arrays.toString(args));
        return args;
    }*/

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }


    @Override
    protected void processDataQuery() {
        Log.d(TAG, "processDataQuery()");
        getSupportLoaderManager().restartLoader(LOADER_RECORDS, null, this);
    }

    @Override
    public List<String> getCheckedCodes() {
        List<String> codes = new ArrayList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) codes.add(Record.TYPE_VALUES[checkBox.getId()]);
        }
        Log.d(TAG, "getCheckedCodes() codes " + codes);
        return codes;
    }

    @Override
    public void onTaskFinished(Result result) {
        Log.d(TAG, "onTaskFinished()");
    }
}
