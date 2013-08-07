package imis.client.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import imis.client.R;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.PieChartSerie;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 10.4.13
 * Time: 22:54
 */
public class PieChartFragment extends ChartFragment {
    private static final String TAG = PieChartFragment.class.getSimpleName();

    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        mChartContainerView = inflater.inflate(R.layout.chart, container, false);
        return mChartContainerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected void displayContent() {
        PieChartData pieChartData = mActivity.getPieChartData();
        clearGraph();
        prepareGraph(pieChartData);
    }

    private void initRenderer() {
        mRenderer = new DefaultRenderer();
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(30);
        mRenderer.setLegendTextSize(30);
        mRenderer.setMargins(new int[]{20, 30, 15, 0});
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(90);
    }

    private void prepareGraph(PieChartData pieChartData) {
        List<PieChartSerie> series = pieChartData.getEventsGraphSeries();
        for (PieChartSerie serie : series) {
            mSeries.add(serie.getLabel() + "(" + serie.getPercent() + "%)", serie.getAmount());
            addSeriesRenderer(serie.getColor());
        }

        if (mChartContainerView != null) {
            LinearLayout layout = (LinearLayout) mChartContainerView;
            layout.removeAllViews();
            GraphicalView mChartView = ChartFactory.getPieChartView(getActivity(), mSeries, mRenderer);

            layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
        }
    }

    private void addSeriesRenderer(int color) {
        SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
        renderer.setColor(color);
        mRenderer.addSeriesRenderer(renderer);
    }

    private void clearGraph() {
        mSeries.clear();
        mRenderer = null;
        initRenderer();
    }
}
