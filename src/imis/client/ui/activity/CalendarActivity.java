package imis.client.ui.activity;

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
    private long initialTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CalendarActivity", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        initialTime = getIntent().getLongExtra(Event.KEY_DATE, System.currentTimeMillis());
        calendarView.setDate(initialTime);
        calendarView.setOnDateChangeListener(new CalendarDateChangeListener(calendarView));
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

            Log.d("CalendarActivity", "onSelectedDayChange() year: " + year + " month: " + month +
                    " dayOfMonth: " + dayOfMonth);//+ " cal: " + cal.toString()
            if (calendarView.getDate() != initialTime) {
                Log.d("CalendarActivity$CalendarDateChangeListener", "onSelectedDayChange() finish");
                Intent resIntent = getIntentWithDateSet(cal.getTimeInMillis());
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
