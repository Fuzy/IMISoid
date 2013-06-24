package imis.client.ui.activities;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.*;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
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
public abstract class ChartActivity extends ControlActivity {
    private static final String TAG = ChartActivity.class.getSimpleName();

    protected static final String FRAG_PIE = "PieChartFragment",
            FRAG_STACK = "StackedBarFragment", FRAG_STATS = "StatisticsFragment";

    private static final String FRAG_TAG = "fragment";
    private String currentFragment;

    protected final List<CheckBox> checkBoxes = new ArrayList<>();
    protected final DataSetObservable mDataSetObservable = new DataSetObservable();
    protected final CheckBoxClickListener checkBoxClickListener = new CheckBoxClickListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_layout);
        initControlPanel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(FRAG_TAG, currentFragment);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        currentFragment = savedInstanceState.getString(FRAG_TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.switch_chart_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switchFragment:
                switchFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void switchFragment() {
        if (getSupportFragmentManager().findFragmentByTag(FRAG_STATS) != null) {
            removeFragment(FRAG_STATS);
            switchToPieChartFragment();
        } else if (getSupportFragmentManager().findFragmentByTag(FRAG_PIE) != null) {
            removeFragment(FRAG_PIE);
            switchToStackedBarFragment();
        } else if (getSupportFragmentManager().findFragmentByTag(FRAG_STACK) != null) {
            removeFragment(FRAG_STACK);
            switchToStatisticsFragment();
        }
        processDataQuery();
    }

    private void initFragment() {
        if (currentFragment == null) {
            switchToPieChartFragment();
            return;
        }

        if (currentFragment.equals(FRAG_STATS)) {
            switchToStatisticsFragment();
        } else if (currentFragment.equals(FRAG_PIE)) {
            switchToPieChartFragment();
        } else if (currentFragment.equals(FRAG_STACK)) {
            switchToStackedBarFragment();
        }
    }

    protected void removeFragment(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    protected void switchToStackedBarFragment() {
        currentFragment = FRAG_STACK;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        StackedBarFragment barFragment = new StackedBarFragment();
        ft.replace(R.id.displayChart, barFragment, FRAG_STACK);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    protected void switchToPieChartFragment() {
        currentFragment = FRAG_PIE;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        PieChartFragment pieFragment = new PieChartFragment();
        ft.replace(R.id.displayChart, pieFragment, FRAG_PIE);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    protected void switchToStatisticsFragment() {
        currentFragment = FRAG_STATS;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        StatisticsFragment statsFragment = new StatisticsFragment();
        ft.replace(R.id.displayChart, statsFragment, FRAG_STATS);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
    }

    protected void initCheckBoxes(String[] values) {
        checkBoxes.clear();
        LinearLayout box = (LinearLayout) findViewById(R.id.checkBoxesBox);
        box.removeAllViews();
        for (String value : values) {
            addCheckBox(value);
        }
    }

    protected abstract void addCheckBox(String code);

    public abstract List<String> getCheckedCodes();

    protected void addCheckBox(int index, int color) {
        final float scale = getApplication().getResources().getDisplayMetrics().density;
        LinearLayout box = (LinearLayout) findViewById(R.id.checkBoxesBox);

        CheckBox check = new CheckBox(getApplication());
        check.setId(index);
        check.setChecked(true);
        box.addView(check);
        checkBoxes.add(check);

        TextView label = new TextView(getApplication());
        label.setBackgroundColor(color);
        label.setHeight((int) (15 * scale + 0.5f));
        label.setWidth((int) (15 * scale + 0.5f));
        label.setGravity(Gravity.CENTER);
        box.addView(label);

        check.setOnClickListener(checkBoxClickListener);
    }

    private class CheckBoxClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            refreshCurrentFragment();
        }
    }

    protected void refreshCurrentFragment() {
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
