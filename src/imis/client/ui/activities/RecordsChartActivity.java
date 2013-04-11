package imis.client.ui.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import imis.client.R;
import imis.client.model.Record;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.RecordManager;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 29.3.13
 * Time: 15:34
 */
public class RecordsChartActivity extends Activity {
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
        new GetRecordsAsyncTask().execute(null);
    }

    private class GetRecordsAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d("RecordsChartActivity$GetRecordsAsyncTask", "doInBackground()");
            String from = "26.03.08";//TODO pryc
            String to = "26.03.08";
            String kodpra = "JEL";
            return NetworkUtilities.getUserRecords(kodpra, from, to);
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d("RecordsChartActivity$GetRecordsAsyncTask", "onPostExecute()");
            List<Record> records = RecordManager.jsonToList(response);
            if (records != null) {
                RecordManager.addRecords(getApplicationContext(), records);

            }
        }
    }
}
