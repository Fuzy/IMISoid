package imis.client.ui.activities;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import imis.client.ui.adapters.RecordsCursorAdapter;
import imis.client.ui.dialogs.ColorPickerDialog;
import imis.client.ui.fragments.RecordDetailFragment;
import imis.client.ui.fragments.RecordListFragment;

import java.text.ParseException;

import static imis.client.AppUtil.showAccountNotExistsError;
import static imis.client.AppUtil.showPeriodInputError;
import static imis.client.persistent.RecordManager.DataQuery.CONTENT_URI;
import static imis.client.persistent.RecordManager.DataQuery.SELECTION_ZC;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 15.4.13
 * Time: 17:25
 */
public class RecordListActivity extends ControlActivity implements
        RecordListFragment.OnDetailSelectedListener, ColorPickerDialog.OnColorChangedListener,
        AdapterView.OnItemSelectedListener {
    private static final String TAG = RecordListActivity.class.getSimpleName();
    private RecordsCursorAdapter adapter;
    private static final int LOADER_RECORDS = 0x08;
    private int position = -1;
    private int typesPos = 0;
    private String[] typesArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.records);

        Resources r = getResources();
        typesArray = r.getStringArray(R.array.typ_zakazky);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerRecords);
        Log.d(TAG, "onCreate() spinner " + spinner);
        ArrayAdapter<String> spinnerArrayAdapter
                = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, typesArray);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        RecordListFragment listFragment = new RecordListFragment();
        ft.replace(R.id.recordsList, listFragment, "RecordListFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        adapter = new RecordsCursorAdapter(getApplicationContext(), null, -1);
        listFragment.setListAdapter(adapter);
        getSupportLoaderManager().initLoader(LOADER_RECORDS, null, this);

        initControlPanel();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Log.d(TAG, "onCreateLoader()");
        switch (i) {
            case LOADER_RECORDS:
                String selection;
                String[] selectArgs;
                if (typesPos == 0) {
                    selection = null;
                    selectArgs = null;
                } else {
                    selection = SELECTION_ZC;
                    selectArgs = new String[]{typesArray[typesPos]};
                }

                if (selectArgs != null)
                    Log.d(TAG, "onCreateLoader() selection " + selection + " selectionArgs " + selectArgs[0]);
                else Log.d(TAG, "onCreateLoader() selection " + selection + " selectionArgs " + selectArgs);

                return new CursorLoader(getApplicationContext(), CONTENT_URI,
                        null, selection, selectArgs, null);//PROJECTION_ALL
            default:
                return super.onCreateLoader(i, bundle);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished() size " + cursor.getCount());
        int id = cursorLoader.getId();
        switch (id) {
            case LOADER_RECORDS:
                adapter.swapCursor(cursor);
                break;
            default:
                super.onLoadFinished(cursorLoader, cursor);
        }
       /* int pos = cursor.getPosition();
        while (cursor.moveToNext()) Log.d(TAG, "onLoadFinished() record " + Record.cursorToRecord(cursor) );
        cursor.moveToPosition(pos);*/

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(TAG, "onLoaderReset()");
        adapter.swapCursor(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        // Ziska menu z XML zdroje
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.network_activity_menu, menu); //TODO refaktor pojmenovani

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.refresh:
                resfreshRecords();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void resfreshRecords() {
        Log.d(TAG, "resfreshRecords()");

        try {
            String kodpra = getSelectedUser();
            String from = getDateFrom();
            String to = getDateTo();
            createTaskFragment(new GetListOfRecords(kodpra, from, to));
        } catch (ParseException e) {
            Log.d(TAG, "resfreshRecords() " + e.getMessage());
            showPeriodInputError(this);
        } catch (Exception e) {
            showAccountNotExistsError(this);
        }
    }


    @Override
    public void onDetailSelected(int position) {
        this.position = position;
        Record record = adapter.getItem(position);
        createDetailFragment(record);
    }

    public void createDetailFragment(Record record) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        RecordDetailFragment detailFragment = new RecordDetailFragment();
        detailFragment.setRecord(record);
        ft.replace(R.id.recordsList, detailFragment, "RecordDetailFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void deleteDetailFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("RecordDetailFragment");
        if (fragment != null) getSupportFragmentManager().popBackStack();
    }


    @Override
    public void colorChanged() {
        Log.d(TAG, "colorChanged()");
        adapter.notifyDataSetChanged();
        deleteDetailFragment();
        createDetailFragment(adapter.getItem(position));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

        int id = adapterView.getId();
        switch (id) {
            case R.id.spinnerRecords:
                Log.d(TAG, "onItemSelected() " + typesArray[pos]);
                typesPos = pos;
                getSupportLoaderManager().restartLoader(LOADER_RECORDS, null, this);
                break;
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onTaskFinished(ResultData result) {
        Log.d(TAG, "onTaskFinished()");
    }
}
