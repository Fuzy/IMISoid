package imis.client.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import imis.client.R;
import imis.client.model.Event;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 14.3.13
 * Time: 23:03
 */
public class CalendarActivity extends Activity {
    private static final String TAG = CalendarActivity.class.getSimpleName();
    private int initYear, initMonth, initDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CalendarActivity", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        long initialTime = getIntent().getLongExtra(Event.KEY_DATE, System.currentTimeMillis());
        Calendar initCal = Calendar.getInstance();
        initCal.setTimeInMillis(initialTime);
        calendarView.setDate(initCal.getTimeInMillis());
        calendarView.setOnDateChangeListener(new CalendarDateChangeListener(calendarView));

        initYear = initCal.get(Calendar.YEAR);
        initMonth = initCal.get(Calendar.MONTH);
        initDay = initCal.get(Calendar.DAY_OF_MONTH);
    }

    class CalendarDateChangeListener implements CalendarView.OnDateChangeListener {
        private CalendarView calendarView;

        CalendarDateChangeListener(CalendarView calendarActivity) {
            this.calendarView = calendarActivity;
        }

        @Override
        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, dayOfMonth);

            if (year != initYear || month != initMonth || dayOfMonth != initDay) {
                Intent resIntent = getIntentWithDateSet(cal.getTimeInMillis());
                Log.d(TAG, "onSelectedDayChange() cal " + cal.getTimeInMillis() + " year: " + year + " month: " + month +
                        " dayOfMonth: " + dayOfMonth);
                setResult(RESULT_OK, resIntent);
                finish();
            }
        }

        private Intent getIntentWithDateSet(long millis) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Event.KEY_DATE, millis);
            return returnIntent;
        }


    }

}
