package imis.client.ui.fragments;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import imis.client.R;
import imis.client.data.graph.StackedBarChartData;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import static imis.client.ui.activities.util.GraphUtil.*;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 11.4.13
 * Time: 18:38
 */
public class StackedBarFragment extends ChartFragment {
    private static final String TAG = "StackedBarFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        if (container == null) {
            return null;
        }
        mChartContainerView = inflater.inflate(R.layout.chart, container, false);
        return mChartContainerView;
    }

    protected void displayContent() {
        Log.d(TAG, "displayContent()");
        StackedBarChartData chartData = mActivity.getStackedBarChartData();
        clearGraph();
        prepareGraph(chartData);
    }

    private void prepareGraph(StackedBarChartData chartData) {
        XYMultipleSeriesRenderer renderer = buildBarRenderer(chartData.getColors());  //TODO
        setChartSettings(renderer, "Události docházky pro daný den", "Den", "Čas", 0.5,
                12.5, 0, (chartData.getyMax() * 1.1), Color.GRAY, Color.LTGRAY);
        renderer.setXLabels(12);
        renderer.setYLabels(10);
        renderer.setXLabelsAlign(Paint.Align.LEFT);
        renderer.setYLabelsAlign(Paint.Align.LEFT);
        renderer.setPanEnabled(true, false);
        renderer.setZoomRate(1.1f);
        renderer.setBarSpacing(0.5f);
        Log.d(TAG, "prepareGraph() renderer minX " + renderer.getXAxisMin());
        GraphicalView mChartView = ChartFactory.getBarChartView(mActivity, buildBarDataset(chartData.getTitles(),
                chartData.getValues()), renderer,
                BarChart.Type.STACKED);
        LinearLayout layout = (LinearLayout) mChartContainerView;
        layout.removeAllViews();
        layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private void clearGraph() {
        Log.d(TAG, "clearGraph()");
        LinearLayout layout = (LinearLayout) mChartContainerView;
        layout.removeAllViews();
    }
}
