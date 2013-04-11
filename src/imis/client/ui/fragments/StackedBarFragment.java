package imis.client.ui.fragments;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import imis.client.R;
import imis.client.ui.activities.EventsChartActivity;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

import static imis.client.ui.activities.GraphUtil.*;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 11.4.13
 * Time: 18:38
 */
public class StackedBarFragment extends Fragment {
    private static final String TAG = "StackedBarFragment";

    private View mChartContainerView;
    private GraphicalView mChartView;
    private EventsChartActivity mActivity;
    private DataSetObserver mObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            //TODO change vs invalidate?
            Log.d(TAG, "onChanged()");
            displayGraph();
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //TODO sem kdyztak listener
        mActivity = (EventsChartActivity) activity;
        mActivity.registerDataSetObserver(mObserver);
        Log.d(TAG, "onAttach() activity " + mActivity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        if (container == null) {
            return null;
        }
        mChartContainerView = inflater.inflate(R.layout.chart, container, false);
        return mChartContainerView;
    }

    private void displayGraph() {
           execute();
    }

    /**/
    public void execute() {
        String[] titles = new String[]{"2008", "2007"};
        List<double[]> values = new ArrayList<double[]>();
        values.add(new double[]{14230, 12300, 14240, 15244, 15900, 19200, 22030, 21200, 19500, 15500,
                12600, 14000});
        values.add(new double[]{5230, 7300, 9240, 10540, 7900, 9200, 12030, 11200, 9500, 10500,
                11600, 13500});
        int[] colors = new int[]{Color.BLUE, Color.CYAN};
        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
        setChartSettings(renderer, "Monthly sales in the last 2 years", "Month", "Units sold", 0.5,
                12.5, 0, 24000, Color.GRAY, Color.LTGRAY);
        renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
        renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
        renderer.setXLabels(12);
        renderer.setYLabels(10);
        renderer.setXLabelsAlign(Paint.Align.LEFT);
        renderer.setYLabelsAlign(Paint.Align.LEFT);
        renderer.setPanEnabled(true, false);
        // renderer.setZoomEnabled(false);
        renderer.setZoomRate(1.1f);
        renderer.setBarSpacing(0.5f);
        mChartView = ChartFactory.getBarChartView(mActivity, buildBarDataset(titles, values), renderer,
                BarChart.Type.STACKED);
        LinearLayout layout = (LinearLayout) mChartContainerView;
        layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }
}
