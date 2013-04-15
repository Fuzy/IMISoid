package imis.client.ui.activities;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import imis.client.R;
import imis.client.asynctasks.GetListOfRecords;
import imis.client.model.Record;
import imis.client.ui.fragments.RecordListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 29.3.13
 * Time: 15:34
 */
public class RecordsChartActivity extends NetworkingActivity {
    private static final String TAG = RecordsChartActivity.class.getSimpleName();

    private final DataSetObservable mDataSetObservable = new DataSetObservable();
    private List<Record> records = new ArrayList<>();
    ///private List<Map<String, String>> records = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("RecordsChartActivity", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_chart);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            RecordListFragment listFragment = new RecordListFragment();
            ft.replace(R.id.displayChart, listFragment, "RecordListFragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
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

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
        mDataSetObservable.notifyChanged();
    }

    /*public List<Map<String, String>> getRecords() {
        return records;
    }

    public void setRecords(Record[] records) {
        Log.d(TAG, "setRecords()");
        List<Map<String, String>> recordsMap = new ArrayList<>();
        for (Record record : records) {
            Map map = new HashMap();
            map.put(Record.COL_ZC, record.getZc());
            map.put(Record.COL_CPOLZAK, record.getCpolzak());
            recordsMap.add(map);
        }
        this.records = recordsMap;
        mDataSetObservable.notifyChanged();
    }*/
}
