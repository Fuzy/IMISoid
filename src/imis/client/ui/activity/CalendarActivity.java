package imis.client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import imis.client.R;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 14.3.13
 * Time: 23:03
 */
public class CalendarActivity extends Activity {
    private long initialTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CalendarActivity", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        //calendarView.setDate(getIntent().getLongExtra("date", 0));//TODO default today
        initialTime = calendarView.getDate();
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
                long millis = cal.getTimeInMillis();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("millis", millis);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }

    }

}
