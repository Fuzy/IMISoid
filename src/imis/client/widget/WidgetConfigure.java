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
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
        switch (cursorLoader.getId()) {
            case LOADER_EMPLOYEES:
                adapter.swapCursor(cursor);
                if (adapter.getCount() == 0) {
                    showInfoEmpty();
                }
                break;
        }
    }

    private void showInfoEmpty() {
        AppUtil.showInfo(this, getString(R.string.no_employees));
    }

    private void showNoEmpSelected() {
        AppUtil.showInfo(this, getString(R.string.noEmp));
    }


    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveWidgetEmployee();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveWidgetEmployee() {
        CursorWrapper wrapper = (CursorWrapper) spinnerEmp.getSelectedItem();
        if (wrapper != null) {
            int prevWidgetId = wrapper.getInt(Employee.IND_COL_WIDGET_ID);
            if (prevWidgetId != 0) {
                AppUtil.showWidgetAlreadyExists(this);
            } else {
                int empId = wrapper.getInt(Employee.IND_COL_ID);
                EmployeeManager.updateEmployeeWidgetId(this, empId, mAppWidgetId);

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                new EmployeeWidgetProvider().updateAppWidget(this, appWidgetManager, mAppWidgetId);

                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
            }
            finish();
        } else {
            showNoEmpSelected();
        }

    }
}
