package imis.client.ui.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import imis.client.R;
import imis.client.asynctasks.GetListOfRecords;
import imis.client.asynctasks.result.ResultData;
import imis.client.model.Record;
import imis.client.persistent.RecordManager;
import imis.client.ui.adapters.RecordsCursorAdapter;
import imis.client.ui.fragments.RecordListFragment;

import java.text.ParseException;
import java.util.Arrays;

import static imis.client.AppUtil.showAccountNotExistsError;
import static imis.client.AppUtil.showPeriodInputError;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 15.4.13
 * Time: 17:25
 */
public class RecordListActivity extends ControlActivity implements
        RecordListFragment.OnDetailSelectedListener {
    private static final String TAG = RecordListActivity.class.getSimpleName();
    private RecordsCursorAdapter adapter;
    private static final int LOADER_RECORDS = 0x08;
    private static final int DETAIL_ACTIVITY_CODE = 1;
    private int position = -1;

    private String[] typesArray;
    protected Spinner spinnerType;
    private final String PAR_TYPE = "TYPE";

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
        spinnerEmp.setOnItemSelectedListener(this);

        addListFragment();

        initSelectionValues();
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


    private void initSelectionValues() {
        selectionArgs.put(PAR_TYPE, "");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader()");
        switch (i) {
            case LOADER_RECORDS:
                Log.d(TAG, "onCreateLoader() SELECTION " + RecordManager.DataQuery.SELECTION);
                return new CursorLoader(this, RecordManager.DataQuery.CONTENT_URI,
                        null, RecordManager.DataQuery.SELECTION, getSelectionArgs(), null);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.record_list_activity_menu, menu); //TODO refaktor pojmenovani

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshRecords();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshRecords() {
        Log.d(TAG, "refreshRecords()");

        try {
            String kodpra = getSelectedUser();
            String from = getStringDateFrom();
            String to = getStringDateTo();
            createTaskFragment(new GetListOfRecords(this, kodpra, from, to));
        } catch (ParseException e) {
            Log.d(TAG, "refreshRecords() " + e.getMessage());
            showPeriodInputError(this);
        } catch (Exception e) {
            showAccountNotExistsError(this);
        }
    }


    @Override
    public void onDetailSelected(int position) {
        this.position = position;
        Record record = adapter.getItem(position);
        long id = record.get_id();
        createDetailFragment(id);
    }

    public void createDetailFragment(long id) {
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

        switch (adapterView.getId()) {
            case R.id.spinnerRecords:
                Log.d(TAG, "onItemSelected() spinnerRecords");
                selectionArgs.put(PAR_TYPE, getSelectedType());
                resfreshQuery(); //TODO co s tim?
                break;
            default:
                super.onItemSelected(adapterView, view, pos, l);
        }

    }

    @Override
    protected String[] getSelectionArgs() {
        String[] args = new String[4];
        args[2] = selectionArgs.get(PAR_FROM);
        args[3] = selectionArgs.get(PAR_TO);
        args[1] = selectionArgs.get(PAR_EMP);
        args[0] = selectionArgs.get(PAR_TYPE);
        Log.d(TAG, "getSelectionArgs() args " + Arrays.toString(args));
        return args;
    }

    @Override
    protected void resfreshQuery() {
        Log.d(TAG, "resfreshQuery()");
        getSupportLoaderManager().restartLoader(LOADER_RECORDS, null, this);
    }

    private String getSelectedType() {
        String type = (String) spinnerType.getSelectedItem();
        if (type == null || type.equals("-")) type = "";
        Log.d(TAG, "getSelectedType() type " + type);
        return type;
    }

    @Override
    public void onTaskFinished(ResultData result) {
        Log.d(TAG, "onTaskFinished()");
    }

}
