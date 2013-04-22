package imis.client.ui.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import imis.client.R;
import imis.client.asynctasks.GetListOfRecords;
import imis.client.model.Record;
import imis.client.ui.adapters.RecordsCursorAdapter;
import imis.client.ui.dialogs.ColorPickerDialog;
import imis.client.ui.fragments.RecordDetailFragment;
import imis.client.ui.fragments.RecordListFragment;

import static imis.client.persistent.RecordManager.DataQuery.CONTENT_URI;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 15.4.13
 * Time: 17:25
 */
public class RecordListActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        RecordListFragment.OnDetailSelectedListener, ColorPickerDialog.OnColorChangedListener {
    private static final String TAG = RecordListActivity.class.getSimpleName();
    private RecordsCursorAdapter adapter;
    private static final int LOADER_ID = 0x08;
    private int position = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.records);


        //if (savedInstanceState == null) {
        //Log.d(TAG, "savedInstanceState()");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        RecordListFragment listFragment = new RecordListFragment();
        ft.replace(R.id.recordsList, listFragment, "RecordListFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        //}

        adapter = new RecordsCursorAdapter(getApplicationContext(), null, -1);
        listFragment.setListAdapter(adapter);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader()");
        //TODO filtrovani
        return new CursorLoader(getApplicationContext(), CONTENT_URI,
                null, null, null, null);//PROJECTION_ALL
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished() size " + cursor.getCount());
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

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
        String kodpra = "JEL";
        String from = "26.03.08";//TODO pryc
        String to = "26.03.08";

        new GetListOfRecords(this).execute(kodpra, from, to);
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
        if (fragment != null)  getSupportFragmentManager().popBackStack();
    }


    @Override
    public void colorChanged() {
        Log.d(TAG, "colorChanged()");
        adapter.notifyDataSetChanged();
        deleteDetailFragment();
        createDetailFragment(adapter.getItem(position));
    }
}
