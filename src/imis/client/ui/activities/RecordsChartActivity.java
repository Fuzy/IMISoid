package imis.client.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import imis.client.R;
import imis.client.services.GetListOfRecords;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 29.3.13
 * Time: 15:34
 */
public class RecordsChartActivity extends NetworkingActivity {
    private static final String TAG = RecordsChartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("RecordsChartActivity", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_chart);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        // Ziska menu z XML zdroje
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.records_chart_menu, menu); //TODO refaktor pojmenovani

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.recordsRefresh:
                resfreshRecords();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void resfreshRecords() {
        Log.d("RecordsChartActivity", "resfreshRecords()");
        String kodpra = "JEL";
        String from = "26.03.08";//TODO pryc
        String to = "26.03.08";

        new GetListOfRecords(this).execute(kodpra, from, to);
    }
}
