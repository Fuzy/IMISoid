package imis.client.ui.fragments;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import imis.client.R;
import imis.client.processor.BlockProcessor;
import imis.client.data.graph.PieChartSerie;
import imis.client.data.graph.PieChartData;
import imis.client.ui.activities.EventsChartActivity;
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
public class PieChartFragment extends Fragment {
    private static final String TAG = "PieChartFragment";

    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");
    }

    private void displayGraph() {
        Log.d(TAG, "displayGraph()");
        PieChartData  pieChartData = BlockProcessor.countPieChartData(mActivity.getBlockList());
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

    private void prepareGraph(PieChartData  pieChartData) {
        Log.d(TAG, "prepareGraph()");
        List<PieChartSerie> eventsGraphSeries = pieChartData.getEventsGraphSeries();
        for (PieChartSerie eventsGraphSerie : eventsGraphSeries) {
            Log.d(TAG, "prepareGraph() eventsGraphSerie " + eventsGraphSerie);
            mSeries.add(mActivity.getLabelForCode(eventsGraphSerie.getLabel()), eventsGraphSerie.getAmount());
            addSeriesRenderer(eventsGraphSerie.getColor());
        }
        LinearLayout layout = (LinearLayout) mChartContainerView;
        if (mChartView == null) {
            mChartView = ChartFactory.getPieChartView(getActivity(), mSeries, mRenderer);

            layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            mChartView.repaint();
        }
    }

    private void addSeriesRenderer(int color) {
        SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
        renderer.setColor(color);
        mRenderer.addSeriesRenderer(renderer);
    }

    private void clearGraph() {
        Log.d(TAG, "clearGraph()");
        mSeries.clear();
        mRenderer = null;
        initRenderer();
    }
/*
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
    }*/
}
