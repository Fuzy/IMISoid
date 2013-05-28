package imis.client.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Spinner;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.model.Employee;
import imis.client.persistent.EmployeeManager;
import imis.client.ui.adapters.EmployeeResourceCursorAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 2.5.13
 * Time: 21:24
 */
public class WidgetConfigure extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String TAG = WidgetConfigure.class.getSimpleName();
    protected static final int LOADER_EMPLOYEES = 0x04;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    protected Spinner spinnerEmp;
    private EmployeeResourceCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.widget_config);
        spinnerEmp = (Spinner) findViewById(R.id.spinnerEmp);

        getSupportLoaderManager().initLoader(LOADER_EMPLOYEES, null, this);

        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        adapter = new EmployeeResourceCursorAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, null, -1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEmp.setAdapter(adapter);
        Log.d(TAG, "onCreate() mAppWidgetId " + mAppWidgetId);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader()");
        switch (i) {
            case LOADER_EMPLOYEES:
                return new CursorLoader(getApplicationContext(), EmployeeManager.EmployeeQuery.CONTENT_URI,
                        null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished()");
        switch (cursorLoader.getId()) {
            case LOADER_EMPLOYEES:
                Log.d(TAG, "onLoadFinished() LOADER_EMPLOYEES");
                adapter.swapCursor(cursor);
                break;
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
        inflater.inflate(R.menu.save_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.save:
                saveWidgetEmployee();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveWidgetEmployee() {
        Log.d(TAG, "saveWidgetEmployee() ");
        CursorWrapper wrapper = (CursorWrapper) spinnerEmp.getSelectedItem();
        if (wrapper != null) {
            int prevWidgetId = wrapper.getInt(Employee.IND_COL_WIDGET_ID);
            if (prevWidgetId != 0) {
                AppUtil.showWidgetAlreadyExists(this);
            } else {
                Log.d(TAG, "saveWidgetEmployee() prevWidgetId " + prevWidgetId);
                int empId = wrapper.getInt(Employee.IND_COL_ID);
                Log.d(TAG, "saveWidgetEmployee() empId " + empId + " mAppWidgetId " + mAppWidgetId);
                EmployeeManager.updateEmployeeWidgetId(this, empId, mAppWidgetId);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                WidgetProvider.updateAppWidget(this, appWidgetManager, mAppWidgetId);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
            }
        }
        finish();
    }
}
