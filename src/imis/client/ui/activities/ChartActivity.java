package imis.client.ui.activities;

import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CheckBox;
import imis.client.R;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.StackedBarChartData;
import imis.client.ui.fragments.PieChartFragment;
import imis.client.ui.fragments.StackedBarFragment;
import imis.client.ui.fragments.StatisticsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 23.4.13
 * Time: 17:51
 */
public abstract class ChartActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener {
    protected static final String FRAG_PIE = "PieChartFragment",
            FRAG_STACK = "StackedBarFragment", FRAG_STATS = "StatisticsFragment";
    private static final String TAG = ChartActivity.class.getSimpleName();

    protected final List<CheckBox> checkBoxes = new ArrayList<>();
    protected final DataSetObservable mDataSetObservable = new DataSetObservable();

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        switchFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.network_activity_menu, menu);
        inflater.inflate(R.menu.switch_chart_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                return true;
            case R.id.switchFragment:
                switchFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected abstract void refresh();

    protected void switchFragment() {
        Log.d(TAG, "switchFragment()");
        if (getSupportFragmentManager().findFragmentByTag(FRAG_STATS) != null) {
            switchToPieChartFragment();
        } else if (getSupportFragmentManager().findFragmentByTag(FRAG_PIE) != null) {
            switchToStackedBarFragment();
        } else if (getSupportFragmentManager().findFragmentByTag(FRAG_STACK) != null) {
            switchToStatisticsFragment();
        } else {
            switchToPieChartFragment();
        }
        restartLoaders();
    }

    protected abstract void restartLoaders();

    protected void switchToStackedBarFragment() {
        Log.d(TAG, "switchToStackedBarFragment()");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        StackedBarFragment barFragment = new StackedBarFragment();
        ft.replace(R.id.displayChart, barFragment, FRAG_STACK);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    protected void switchToPieChartFragment() {
        Log.d(TAG, "switchToPieChartFragment()");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        PieChartFragment pieFragment = new PieChartFragment();
        ft.replace(R.id.displayChart, pieFragment, FRAG_PIE);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    protected void switchToStatisticsFragment() {
        Log.d(TAG, "switchToStatisticsFragment()");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        StatisticsFragment statsFragment = new StatisticsFragment();
        ft.replace(R.id.displayChart, statsFragment, FRAG_STATS);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    protected void refreshCurrentFragment() {
        Log.d(TAG, "refreshCurrentFragment()");
        mDataSetObservable.notifyChanged();
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public abstract PieChartData getPieChartData();

    public abstract StackedBarChartData getStackedBarChartData();

}
