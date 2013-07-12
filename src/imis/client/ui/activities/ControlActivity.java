package imis.client.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import imis.client.AppConsts;
import imis.client.R;
import imis.client.TimeUtil;
import imis.client.exceptions.NotUserSelectedException;
import imis.client.model.Employee;
import imis.client.model.Event;
import imis.client.persistent.EmployeeManager;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static imis.client.AppUtil.*;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 25.4.13
 * Time: 20:44
 */
public abstract class ControlActivity extends AsyncActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener {
    private static final String TAG = ControlActivity.class.getSimpleName();

    public static final String PAR_FROM = "FROM", PAR_TO = "TO", PAR_EMP_ICP = "EMP_ICP", PAR_EMP_KOD = "EMP_KOD";
    private static final int CALENDAR_ACTIVITY_DATE_CODE = 0;
    private static final int CALENDAR_ACTIVITY_DAY_CODE = 1;
    private static final int CALENDAR_ACTIVITY_MONTH_CODE = 2;

    protected static final int LOADER_EMPLOYEES = 0x04;

    private SimpleCursorAdapter adapter;
    private final MyOnFocusChangeListener focusListener = new MyOnFocusChangeListener();
    protected final SubordinateClickListener checkBoxClickListener = new SubordinateClickListener();

    protected Spinner spinnerEmp;
    protected ImageButton dateDateButton, dateMonthButton, dateDayButton;
    protected EditText dateFromEdit, dateToEdit;

    protected long dateFrom, dateTo;
    private CheckBox subOnly;  //TODO napoveda polozky
    private int selectedEditId = -1;
    protected Map<String, String> selectionArgs = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(LOADER_EMPLOYEES, null, this);
    }


    protected void initControlPanel() {
        spinnerEmp = (Spinner) findViewById(R.id.spinnerEmp);
        spinnerEmp.setOnItemSelectedListener(this);
        subOnly = (CheckBox) findViewById(R.id.subOnly);
        subOnly.setOnClickListener(checkBoxClickListener);
        dateFromEdit = (EditText) findViewById(R.id.dateFromEdit);
        dateFromEdit.setOnFocusChangeListener(focusListener);
        dateFromEdit.setInputType(InputType.TYPE_NULL);
        dateToEdit = (EditText) findViewById(R.id.dateToEdit);
        dateToEdit.setOnFocusChangeListener(focusListener);
        dateToEdit.setInputType(InputType.TYPE_NULL);
        dateDateButton = (ImageButton) findViewById(R.id.dateDayButton);
        dateDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateToSelectedField(TimeUtil.todayDateInLong());
                processDataQuery();
            }
        });
        dateDateButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startCalendarActivity(getSelectedLongDateOrDefault(), CALENDAR_ACTIVITY_DATE_CODE);
                return true;
            }
        });
        dateDayButton = (ImageButton) findViewById(R.id.dateTodayButton);
        dateDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDay(TimeUtil.todayDateInLong());
                processDataQuery();
            }
        });
        dateDayButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startCalendarActivity(getLongDateFrom(), CALENDAR_ACTIVITY_DAY_CODE);
                return true;
            }
        });
        dateMonthButton = (ImageButton) findViewById(R.id.dateMonthButton);
        dateMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMonth(TimeUtil.todayDateInLong());
                processDataQuery();
            }
        });
        dateMonthButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startCalendarActivity(getLongDateFrom(), CALENDAR_ACTIVITY_MONTH_CODE);
                return true;
            }
        });

        initSelectionValues();
    }

    private void initSelectionValues() {
        setMonth(TimeUtil.todayDateInLong());
    }

    private void startCalendarActivity(long actual, int code) {
        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putExtra(Event.KEY_DATE, actual);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        long date = data.getLongExtra(Event.KEY_DATE, -1);
        Log.d(TAG, "onActivityResult() date " + date);

        switch (requestCode) {
            case CALENDAR_ACTIVITY_DATE_CODE:
                setDateToSelectedField(date);
                break;
            case CALENDAR_ACTIVITY_DAY_CODE:
                setDay(date);
                break;
            case CALENDAR_ACTIVITY_MONTH_CODE:
                setMonth(date);
                break;
        }

        processDataQuery();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader()");

        switch (i) {
            case LOADER_EMPLOYEES:
                String selection = null;
                if (subOnly != null && subOnly.isChecked()) {
                    selection = EmployeeManager.EmployeeQuery.SELECTION_SUBORDINATES;
                }
                return new CursorLoader(getApplicationContext(), EmployeeManager.EmployeeQuery.CONTENT_URI,
                        null, selection, null, EmployeeManager.EmployeeQuery.ORDER_BY);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished()");
        int id = cursorLoader.getId();
        switch (id) {
            case LOADER_EMPLOYEES:
                Log.d(TAG, "onLoadFinished() LOADER_EMPLOYEES");
                Cursor extendedCursor = mergeCursorWithEmptyItem(cursor);
                String[] from = new String[]{Employee.COL_KODPRA};
                int[] to = new int[]{android.R.id.text1};
                adapter = new SimpleCursorAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                        extendedCursor, from, to, 0);
                adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int i) {
                        Log.d(TAG, "setViewValue() icp " + cursor.getString(Employee.IND_COL_ICP));
                        if (cursor.isNull(Employee.IND_COL_ICP)) return false;
                        String value = (cursor.isNull(i)) ? cursor.getString(Employee.IND_COL_ICP) : cursor.getString(i);
                        ((TextView) view).setText(value);
                        return true;
                    }
                });
                spinnerEmp.setAdapter(adapter);
                selectionArgs.put(PAR_EMP_ICP, getSelectedUser().getIcp());
                selectionArgs.put(PAR_EMP_KOD, getSelectedUser().getKodpra());
                break;
        }
    }

    private Cursor mergeCursorWithEmptyItem(Cursor cursor) {
        MatrixCursor extras = new MatrixCursor(cursor.getColumnNames());
        String[] emptyEmp = new String[cursor.getColumnNames().length];
        emptyEmp[Employee.IND_COL_KODPRA] = AppConsts.EMPTY_SPINNER_ITEM;
        extras.addRow(emptyEmp);
        Cursor[] cursors = {extras, cursor};
        Cursor extendedCursor = new MergeCursor(cursors);
        return extendedCursor;
    }

    protected Employee getSelectedUser() {
        MergeCursor selectedItem = (MergeCursor) spinnerEmp.getSelectedItem();
        Employee employee = Employee.cursorToEmployee(selectedItem);
        Log.d(TAG, "getSelectedUser() employee " + employee);
        return employee;
    }

    private long getSelectedLongDateOrDefault() {
        if (selectedEditId == R.id.dateFromEdit) {
            return dateFrom;
        } else if (selectedEditId == R.id.dateToEdit) {
            return dateTo;
        } else {
            return TimeUtil.todayDateInLong();
        }

    }

    protected long getLongDateFrom() {
        return dateFrom;
    }

    protected long getLongDateTo() {
        return dateTo;
    }

    protected String getStringDateFrom() throws ParseException {
        String date = dateFromEdit.getText().toString();
        TimeUtil.validateDate(date);
        return date;
    }

    protected String getStringDateTo() throws ParseException {
        String date = dateToEdit.getText().toString();
        TimeUtil.validateDate(date);
        return date;
    }

    protected void setDateToSelectedField(long date) {
        Log.d(TAG, "setMonth() date " + date);
        if (selectedEditId == R.id.dateFromEdit) {
            setFromDate(date);
        } else if (selectedEditId == R.id.dateToEdit) {
            setToDate(date);
        } else {
            Toast toast = Toast.makeText(this, R.string.no_item_set, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    protected void setDay(long date) {
        Log.d(TAG, "setMonth() date " + date);
        setFromDate(date);
        setToDate(date);
    }

    protected void setMonth(long date) {
        Log.d(TAG, "setMonth() date " + date);
        long start = TimeUtil.getFirstDateOfMonth(date);
        long end = TimeUtil.getLastDateOfMonth(date);
        setFromDate(start);
        setToDate(end);
    }

    private void setFromDate(long date) {
        selectionArgs.put(PAR_FROM, String.valueOf(date));
        dateFrom = date;
        Log.d(TAG, "setFromDate() date " + date);
        String from = TimeUtil.formatAbbrDate(date);
        Log.d(TAG, "setFromDate() from " + from);
        dateFromEdit.setText(from);
    }

    private void setToDate(long date) {
        selectionArgs.put(PAR_TO, String.valueOf(date));
        dateTo = date;
        Log.d(TAG, "setToDate() date " + date);
        String to = TimeUtil.formatAbbrDate(date);
        Log.d(TAG, "setToDate() to " + to);
        dateToEdit.setText(to);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemSelected()");
        switch (adapterView.getId()) {
            case R.id.spinnerEmp:
                Log.d(TAG, "onItemSelected() spinnerEmp");
                selectionArgs.put(PAR_EMP_ICP, getSelectedUser().getIcp());
                selectionArgs.put(PAR_EMP_KOD, getSelectedUser().getKodpra());
                processDataQuery();
                break;
        }

    }

    protected abstract String[] getSelectionArgs();

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d(TAG, "onNothingSelected()");
    }

    protected abstract void processDataQuery();

    private class MyOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean b) {
            selectedEditId = view.getId();
            Log.d(TAG, "onFocusChange() view " + view + " b " + b);
        }

    }

    private class SubordinateClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick()");
            getSupportLoaderManager().restartLoader(LOADER_EMPLOYEES, null, ControlActivity.this);
            processDataQuery();
        }
    }

    @Override
    protected void processAsyncTask() {
        try {
            Employee employee = getSelectedUser();
            if (employee.getKodpra().equals(AppConsts.EMPTY_SPINNER_ITEM))
                throw new NotUserSelectedException(getString(R.string.noEmp));
            getStringDateFrom();
            getStringDateTo();
            processControlAsyncTask(getSelectedUser(), TimeUtil.formatDate(dateFrom), TimeUtil.formatDate(dateTo));
        } catch (ParseException e) {
            Log.d(TAG, "refreshRecords() " + e.getMessage());
            showPeriodInputError(this);
        } catch (NotUserSelectedException e) {
            Log.d(TAG, "refreshRecords() " + e.getMessage());
            showNotUserSelectedError(this, e.getMessage());
        } catch (Exception e) {
            Log.d(TAG, "refreshRecords() " + e.getMessage());
            showAccountNotExistsError(getSupportFragmentManager());
        }
    }

    protected abstract void processControlAsyncTask(Employee emp, String from, String to);
}
