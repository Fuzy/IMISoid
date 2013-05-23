package imis.client.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.*;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.model.Employee;
import imis.client.model.Event;
import imis.client.persistent.EmployeeManager;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 25.4.13
 * Time: 20:44
 */
public abstract class ControlActivity extends AsyncActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener {
    private static final String TAG = ControlActivity.class.getSimpleName();

    public static final String PAR_FROM = "FROM", PAR_TO = "TO", PAR_EMP = "EMP";
    private static final int CALENDAR_ACTIVITY_DATE_CODE = 0;
    private static final int CALENDAR_ACTIVITY_DAY_CODE = 1;
    private static final int CALENDAR_ACTIVITY_MONTH_CODE = 2;

    protected static final int LOADER_EMPLOYEES = 0x04;

    private SimpleCursorAdapter adapter;
    private final MyOnFocusChangeListener focusListener = new MyOnFocusChangeListener();

    protected Spinner spinnerEmp;
    protected ImageButton dateDateButton, dateMonthButton, dateDayButton;
    protected EditText dateFromEdit, dateToEdit;

    protected long dateFrom, dateTo;

    private int selectedEditId = -1;

//    protected String PAR_FROM = "FROM", PAR_TO = "TO", PAR_EMP = "EMP";
    protected Map<String, String> selectionArgs = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(LOADER_EMPLOYEES, null, this);
    }


    protected void initControlPanel() {
        spinnerEmp = (Spinner) findViewById(R.id.spinnerEmp);
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
                setDateToSelectedField(AppUtil.todayInLong());
                resfreshQuery();
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
                setDay(AppUtil.todayInLong());
                resfreshQuery();
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
                setMonth(AppUtil.todayInLong());
                resfreshQuery();
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
        setMonth(AppUtil.todayInLong());

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

        resfreshQuery();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader()");
        switch (i) {
            case LOADER_EMPLOYEES:
                return new CursorLoader(getApplicationContext(), EmployeeManager.EmployeeQuery.CONTENT_URI,
                        null, null, null, null);//TODO razeni na prvnim miste sebe
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
                String[] from = new String[]{Employee.COL_KODPRA};
                int[] to = new int[]{android.R.id.text1};
                adapter = new SimpleCursorAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,
                        cursor, from, to, 0);
                spinnerEmp.setAdapter(adapter);
                selectionArgs.put(PAR_EMP, getSelectedUser());
                break;
        }
    }

    protected String getSelectedUser() {
        CursorWrapper wrapper = (CursorWrapper) spinnerEmp.getSelectedItem();
        return wrapper.getString(Employee.IND_COL_KODPRA);
    }

    private long getSelectedLongDateOrDefault() {
        if (selectedEditId == R.id.dateFromEdit) {
            return dateFrom;
        } else if (selectedEditId == R.id.dateToEdit) {
            return dateTo;
        } else {
            return AppUtil.todayInLong();
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
        AppUtil.validateDate(date);
        return date;
    }

    protected String getStringDateTo() throws ParseException {
        String date = dateToEdit.getText().toString();
        AppUtil.validateDate(date);
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
        long start = AppUtil.getFirstDateOfMonth(date);
        long end = AppUtil.getLastDateOfMonth(date);
        setFromDate(start);
        setToDate(end);
    }

    private void setFromDate(long date) {
        selectionArgs.put(PAR_FROM, String.valueOf(date));
        dateFrom = date;
        Log.d(TAG, "setFromDate() date " + date);
        String from = AppUtil.formatAbbrDate(date);
        Log.d(TAG, "setFromDate() from " + from);
        dateFromEdit.setText(from);
    }

    private void setToDate(long date) {
        selectionArgs.put(PAR_TO, String.valueOf(date));
        dateTo = date;
        Log.d(TAG, "setToDate() date " + date);
        String to = AppUtil.formatAbbrDate(date);
        Log.d(TAG, "setToDate() to " + to);
        dateToEdit.setText(to);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemSelected()");
        switch (adapterView.getId()) {
            case R.id.spinnerEmp:
                Log.d(TAG, "onItemSelected() spinnerEmp");
                selectionArgs.put(PAR_EMP, getSelectedUser());
                resfreshQuery();
                break;
        }

    }

    protected abstract String[] getSelectionArgs();

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    protected abstract void resfreshQuery();

    private class MyOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean b) {
            selectedEditId = view.getId();
            Log.d(TAG, "onFocusChange() view " + view + " b " + b);
        }

    }
}
