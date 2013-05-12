package imis.client.ui.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.CheckBox;
import imis.client.asynctasks.GetListOfRecords;
import imis.client.asynctasks.result.ResultData;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.StackedBarChartData;
import imis.client.model.Record;
import imis.client.processor.DataProcessor;
import imis.client.ui.ColorUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static imis.client.AppUtil.showAccountNotExistsError;
import static imis.client.AppUtil.showPeriodInputError;
import static imis.client.persistent.RecordManager.DataQuery.CONTENT_URI;

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
        getSupportLoaderManager().initLoader(LOADER_RECORDS, null, this);
    }

    protected void addCheckBox(String kod_po) {
        int index = Arrays.asList(Record.TYPE_VALUES).indexOf(kod_po);
        int color = ColorUtil.getColor(kod_po);
        addCheckBox(index, color);
    }


    @Override
    protected void refresh() {
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

    @Override
    protected void restartLoaders() {
        getSupportLoaderManager().restartLoader(LOADER_RECORDS, null, this);
    }

    @Override
    public PieChartData getPieChartData() {
        PieChartData data = DataProcessor.countRecordsPieChartData(records, getVisibleCodes());
        return data;
    }

    @Override
    public StackedBarChartData getStackedBarChartData() {
        StackedBarChartData data = DataProcessor.countRecordsStackedBarChartData(records, getVisibleCodes());
        return data;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader()");
        switch (i) {
            case LOADER_RECORDS:
                return new CursorLoader(getApplicationContext(), CONTENT_URI,
                        null, null, null, null);
            //TODO selekce EventQuery.SELECTION_DATUM, new String[]{String.valueOf(date)},
            default:
                return super.onCreateLoader(i, bundle);
        }
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
                String[] values = DataProcessor.recordsCodesInRecords(records);
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
        return new String[0];
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }


    @Override
    protected void resfreshQuery() {
        Log.d(TAG, "resfreshQuery()");
    }


    public List<String> getVisibleCodes() {
        List<String> codes = new ArrayList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) codes.add(Record.TYPE_VALUES[checkBox.getId()]);
        }
        Log.d(TAG, "getVisibleCodes() codes " + codes);
        return codes;
    }

    @Override
    public void onTaskFinished(ResultData result) {
        Log.d(TAG, "onTaskFinished()");
    }
}
