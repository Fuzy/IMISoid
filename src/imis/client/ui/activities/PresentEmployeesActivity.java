package imis.client.ui.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import imis.client.R;
import imis.client.asynctasks.GetListOfEmployees;
import imis.client.ui.adapters.EmployeesAdapter;

import static imis.client.persistent.EmployeeManager.DataQuery.CONTENT_URI;
import static imis.client.persistent.EmployeeManager.DataQuery.PROJECTION_ALL;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 6.4.13
 * Time: 22:44
 */
public class PresentEmployeesActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = PresentEmployeesActivity.class.getSimpleName();
    private EmployeesAdapter adapter;
    private GridView gridView;
    private static final int LOADER_ID = 0x02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employees_present);


        gridView = (GridView) findViewById(R.id.gridEmployees);

        adapter = new EmployeesAdapter(getApplicationContext(), null, -1);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick() i " + i);
            }
        });

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader()");
        return new CursorLoader(getApplicationContext(), CONTENT_URI,
                PROJECTION_ALL, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished()");
        adapter.swapCursor(data);
        gridView.setAdapter(adapter);
        gridView.invalidateViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset()");
        adapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.network_activity_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh() {
        new GetListOfEmployees(this).execute(null);
    }
}
