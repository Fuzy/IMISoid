package imis.client.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.model.Event;

import java.text.ParseException;

import static imis.client.AppUtil.convertToTime;
import static imis.client.AppUtil.formatAbbrDate;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 25.4.13
 * Time: 20:44
 */
public abstract class ControlActivity extends AsyncActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener {

    private static final int CALENDAR_ACTIVITY_DAY_CODE = 1;
    private static final int CALENDAR_ACTIVITY_MONTH_CODE = 2;

    private static final String TAG = ControlActivity.class.getSimpleName();

    private final MyOnFocusChangeListener focusListener = new MyOnFocusChangeListener();

    protected Spinner spinnerEmp;
    protected ImageButton dateDayButton, dateMonthButton, dateTodayButton;
    protected EditText dateFromEdit, dateToEdit;

    private int selectedId = -1;


    protected void initControlPanel() {
        spinnerEmp = (Spinner) findViewById(R.id.spinner);
        spinnerEmp.setOnItemSelectedListener(this);
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
                    EditText selected = (EditText) findViewById(selectedId);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selected = adapterView.getItemAtPosition(i).toString();
        Log.d(TAG, "onItemSelected() selected " + selected + " l" + l);
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
            selectedId = view.getId();
            Log.d(TAG, "onFocusChange() view " + view + " b " + b);
        }

    }
}
