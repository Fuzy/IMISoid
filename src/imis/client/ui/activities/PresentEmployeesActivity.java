package imis.client.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
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
import imis.client.asynctasks.result.Result;
import imis.client.model.Employee;
import imis.client.persistent.EmployeeManager;
import imis.client.ui.adapters.EmployeesCursorAdapter;

import static imis.client.persistent.EmployeeManager.EmployeeQuery.CONTENT_URI;
import static imis.client.persistent.EmployeeManager.EmployeeQuery.ORDER_BY;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 6.4.13
 * Time: 22:44
 */
public class PresentEmployeesActivity extends AsyncActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = PresentEmployeesActivity.class.getSimpleName();
    private EmployeesCursorAdapter adapter;
    private GridView gridView;
    private static final int LOADER_EMPLOYEES = 0x02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employees_present);

        gridView = (GridView) findViewById(R.id.gridEmployees);

        adapter = new EmployeesCursorAdapter(getApplicationContext(), null, -1);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CursorWrapper wrapper = (CursorWrapper) adapter.getItem(i);
                long id = wrapper.getLong(Employee.IND_COL_ID);
                Log.d(TAG, "onItemClick() id " + id);
                startDetailActivity(id);
            }
        });

        getSupportLoaderManager().initLoader(LOADER_EMPLOYEES, null, this);
        processAsyncTask();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader()");
        return new CursorLoader(getApplicationContext(), CONTENT_URI,
                null, null, null, ORDER_BY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished()");
        adapter.swapCursor(data);
        gridView.setAdapter(adapter);
        gridView.invalidateViews();
        Log.d(TAG, "onLoadFinished() " + EmployeeManager.getAllEmployees(getApplication()));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset()");
        adapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.record_list_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                processAsyncTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void processAsyncTask() {
        Log.d(TAG, "processAsyncTask()");
        createTaskFragment(new GetListOfEmployees(this));
    }

    public void startDetailActivity(long id) {
        Intent intent = new Intent(this, EmployeeDetailActivity.class);
        intent.putExtra(Employee.COL_ID, id);
        startActivity(intent);
    }


    @Override
    public void onTaskFinished(Result result) {
        Log.d(TAG, "onTaskFinished()");
        if (getSupportLoaderManager().getLoader(LOADER_EMPLOYEES) != null) {
            getSupportLoaderManager().restartLoader(LOADER_EMPLOYEES, null, this);
        }
    }
}
