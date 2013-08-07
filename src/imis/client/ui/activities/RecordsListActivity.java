package imis.client.ui.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import imis.client.AppConsts;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.TimeUtil;
import imis.client.asynctasks.GetListOfRecords;
import imis.client.asynctasks.result.Result;
import imis.client.model.Employee;
import imis.client.model.Record;
import imis.client.ui.adapters.RecordsCursorAdapter;
import imis.client.ui.fragments.RecordListFragment;

import java.util.Arrays;
import java.util.Map;

import static imis.client.persistent.RecordManager.RecordQuery.CONTENT_URI;
import static imis.client.persistent.RecordManager.RecordQuery.SELECTION_LIST;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 15.4.13
 * Time: 17:25
 */
public class RecordsListActivity extends ControlActivity implements
        RecordListFragment.OnItemSelectedListener {
    private static final String TAG = RecordsListActivity.class.getSimpleName();
    private RecordsCursorAdapter adapter;
    private static final int LOADER_RECORDS = 0x08;
    private static final int DETAIL_ACTIVITY_CODE = 1;
    private String[] typesArray;
    private Spinner spinnerType;
    private TextView stats;
    private final String PAR_TYPE = "TYPE";
    private Map<String, Object> statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.records);
        initControlPanel();

        Resources r = getResources();
        typesArray = r.getStringArray(R.array.typ_zakazky);

        spinnerType = (Spinner) findViewById(R.id.spinnerRecords);
        Log.d(TAG, "onCreate() spinner " + spinnerType);
        ArrayAdapter<String> spinnerArrayAdapter
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, typesArray);
        spinnerType.setAdapter(spinnerArrayAdapter);
        spinnerType.setOnItemSelectedListener(this);

        stats = (TextView) findViewById(R.id.stats);
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToastWithStats();
            }
        });

        addListFragment();
    }

    private void showToastWithStats() {
        AppUtil.showInfo(this, getStatsDetailedMessage());

    }

    private void addListFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        RecordListFragment listFragment = new RecordListFragment();
        ft.replace(R.id.recordsList, listFragment, "RecordListFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        adapter = new RecordsCursorAdapter(getApplicationContext(), null, -1);
        listFragment.setListAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader()");
        switch (i) {
            case LOADER_RECORDS:
                Log.d(TAG, "onCreateLoader() SELECTION_LIST " + SELECTION_LIST);
                return new CursorLoader(this, CONTENT_URI, null, SELECTION_LIST, getSelectionArgs(), null);
            default:
                return super.onCreateLoader(i, bundle);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished() size " + cursor.getCount());
        switch (cursorLoader.getId()) {
            case LOADER_EMPLOYEES:
                Log.d(TAG, "onLoadFinished() LOADER_EMPLOYEES");
                super.onLoadFinished(cursorLoader, cursor);
                getSupportLoaderManager().initLoader(LOADER_RECORDS, null, this);
                break;
            case LOADER_RECORDS:
                adapter.swapCursor(cursor);
                break;
            default:
                super.onLoadFinished(cursorLoader, cursor);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(TAG, "onLoaderReset()");
        adapter.swapCursor(null);
    }

    @Override
    protected void processControlAsyncTask(Employee emp, String from, String to) {
        createTaskFragment(new GetListOfRecords(this, emp.getIcp(), emp.getKodpra(), from, to));
    }

    @Override
    public void onItemSelected(int position) {
        Record record = adapter.getItem(position);
        long id = record.get_id();
        startDetailActivity(id);
    }

    public void startDetailActivity(long id) {
        Intent intent = new Intent(this, RecordDetailActivity.class);
        intent.putExtra(Record.COL_ID, id);
        startActivityForResult(intent, DETAIL_ACTIVITY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DETAIL_ACTIVITY_CODE:
                adapter.notifyDataSetChanged();
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        Log.d(TAG, "onItemSelected()");
        switch (adapterView.getId()) {
            case R.id.spinnerRecords:
                selectionArgs.put(PAR_TYPE, (String) spinnerType.getSelectedItem());
                processDataQuery();
                break;
            default:
                super.onItemSelected(adapterView, view, pos, l);
        }

    }

    @Override
    protected String[] getSelectionArgs() {
        String[] args = new String[4];
        String type = selectionArgs.get(PAR_TYPE);
        args[0] = (type == null || type.equals(AppConsts.EMPTY_SPINNER_ITEM)) ? "" : type;
        String kodpra = selectionArgs.get(PAR_EMP_KOD);
        args[1] = (kodpra == null || kodpra.equals(AppConsts.EMPTY_SPINNER_ITEM)) ? "" : kodpra;
        args[2] = selectionArgs.get(PAR_FROM);
        args[3] = selectionArgs.get(PAR_TO);
        Log.d(TAG, "getSelectionArgs() args " + Arrays.toString(args));
        return args;
    }

    @Override
    protected void processDataQuery() {
        Log.d(TAG, "processDataQuery()");
        getSupportLoaderManager().restartLoader(LOADER_RECORDS, null, this);
        stats.setText("");
    }

    @Override
    public void onTaskFinished(Result result) {
        Log.d(TAG, "onTaskFinished()" + "result = [" + result + "]");
        statistics = result.getStatistics();
        stats.setText(getStatsShortMessage());
    }

    private String getStatsShortMessage() {
        if (statistics != null) {
            StringBuilder builder = new StringBuilder();
            long eventsSum = 0, recordsSum = 0;
            if (statistics.containsKey(AppConsts.SUM_EVENTS_TIME)) {
                eventsSum = (long) statistics.get(AppConsts.SUM_EVENTS_TIME);
            }
            if (statistics.containsKey(AppConsts.SUM_RECORDS_TIME)) {
                recordsSum = (long) statistics.get(AppConsts.SUM_RECORDS_TIME);
            }
            long diff = recordsSum - eventsSum;
            if (diff > 0) builder.append("+");
            builder.append(TimeUtil.formatTimeInNonLimitHour(diff));
            return builder.toString();
        }
        return "";
    }

    private String getStatsDetailedMessage() {
        if (statistics != null) {
            StringBuilder builder = new StringBuilder();
            long eventsSum = 0, recordsSum = 0;
            if (statistics.containsKey(AppConsts.SUM_EVENTS_TIME)) {
                eventsSum = (long) statistics.get(AppConsts.SUM_EVENTS_TIME);
            }
            if (statistics.containsKey(AppConsts.SUM_RECORDS_TIME)) {
                recordsSum = (long) statistics.get(AppConsts.SUM_RECORDS_TIME);
            }
            long diff = recordsSum - eventsSum;
            builder.append(getString(R.string.sum_events) + TimeUtil.formatTimeInNonLimitHour(eventsSum) + "\n");
            builder.append(getString(R.string.sum_records) + TimeUtil.formatTimeInNonLimitHour(recordsSum) + "\n");
            builder.append(getString(R.string.sum_diff));
            if (diff > 0) builder.append("+");
            builder.append(TimeUtil.formatTimeInNonLimitHour(diff));
            return builder.toString();
        }
        return "";
    }

}
