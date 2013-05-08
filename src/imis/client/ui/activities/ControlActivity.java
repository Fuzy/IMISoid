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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.model.Employee;
import imis.client.model.Event;
import imis.client.persistent.EmployeeManager;

import java.text.ParseException;

import static imis.client.AppUtil.convertToTime;
import static imis.client.AppUtil.formatAbbrDate;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 25.4.13
 * Time: 20:44
 */
public abstract class ControlActivity extends AsyncActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ControlActivity.class.getSimpleName();

    private static final int CALENDAR_ACTIVITY_DAY_CODE = 1;
    private static final int CALENDAR_ACTIVITY_MONTH_CODE = 2;

    protected static final int LOADER_EMPLOYEES = 0x04;

    private SimpleCursorAdapter adapter;
    private final MyOnFocusChangeListener focusListener = new MyOnFocusChangeListener();

    protected Spinner spinnerEmp;
    protected ImageButton dateDayButton, dateMonthButton, dateTodayButton;
    protected EditText dateFromEdit, dateToEdit;

    private int selectedEditId = -1;


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
        dateDayButton = (ImageButton) findViewById(R.id.dateDayButton);
        dateDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCalendarActivity(convertToTime(dateFromEdit.getText().toString()), CALENDAR_ACTIVITY_DAY_CODE);
            }
        });
        dateTodayButton = (ImageButton) findViewById(R.id.dateTodayButton);
        dateTodayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTodayDate();
            }
        });
        dateMonthButton = (ImageButton) findViewById(R.id.dateMonthButton);
        dateMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCalendarActivity(convertToTime(dateToEdit.getText().toString()), CALENDAR_ACTIVITY_MONTH_CODE);
            }
        });
    }

    private void startCalendarActivity(long actual, int code) {
        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putExtra(Event.KEY_DATE, actual);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CALENDAR_ACTIVITY_DAY_CODE:
                if (resultCode == RESULT_OK) {
                    long date = data.getLongExtra(Event.KEY_DATE, -1);
                    EditText selected = (EditText) findViewById(selectedEditId);
                    if (selected != null) {
                        selected.setText(formatAbbrDate(date));
                    }
                }
                break;
            case CALENDAR_ACTIVITY_MONTH_CODE:
                if (resultCode == RESULT_OK) {
                    long date = data.getLongExtra(Event.KEY_DATE, -1);
                    dateFromEdit.setText(AppUtil.getFirstDateOfCurrentMonth(date));
                    dateToEdit.setText(AppUtil.getLastDateOfCurrentMonth(date));
                }
                break;
        }
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
        int id = cursorLoader.getId();
        switch (id) {
            case LOADER_EMPLOYEES:
                Log.d(TAG, "onLoadFinished() LOADER_EMPLOYEES");
                String[] from = new String[]{Employee.COL_KODPRA};
                int[] to = new int[]{android.R.id.text1};
                adapter = new SimpleCursorAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,
                        cursor, from, to, 0);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEmp.setAdapter(adapter);
                break;
        }
    }

    protected String getSelectedUser() {
        CursorWrapper wrapper = (CursorWrapper) spinnerEmp.getSelectedItem();
        return wrapper.getString(Employee.IND_COL_KODPRA);
    }

    protected String getDateFrom() throws ParseException {
        String date = dateFromEdit.getText().toString();
        AppUtil.validateDate(date);
        return date;
    }

    protected String getDateTo() throws ParseException {
        String date = dateToEdit.getText().toString();
        AppUtil.validateDate(date);
        return date;
    }

    protected void setTodayDate() {
        dateFromEdit.setText(AppUtil.todayInString());
        dateToEdit.setText(AppUtil.todayInString());
    }

    private class MyOnFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean b) {
            selectedEditId = view.getId();
            Log.d(TAG, "onFocusChange() view " + view + " b " + b);
        }

    }
}
