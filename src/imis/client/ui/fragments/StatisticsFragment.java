package imis.client.ui.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import imis.client.R;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.PieChartSerie;
import imis.client.processor.BlockProcessor;
import imis.client.ui.activities.EventsChartActivity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 15.4.13
 * Time: 17:32
 */
public class StatisticsFragment extends Fragment {
    private static final String TAG = "StatisticsFragment";

    private View mStatsContainerView;
    private EventsChartActivity mActivity;
    private TextView statsTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        if (container == null) {
            return null;
        }
        mStatsContainerView = inflater.inflate(R.layout.statistics, container, false);

        return mStatsContainerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        statsTextView = (TextView) getView().findViewById(R.id.statsContent);
    }

    private DataSetObserver mObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            //TODO change vs invalidate?
            Log.d(TAG, "onChanged()");
            displayStats();
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (EventsChartActivity) activity;
        mActivity.registerDataSetObserver(mObserver);
        Log.d(TAG, "onAttach() activity " + mActivity);
    }


    private void displayStats() {
        PieChartData pieChartData = BlockProcessor.countPieChartData(mActivity.getBlockList(), mActivity.getVisibleCodes());
        List<PieChartSerie> eventsGraphSeries = pieChartData.getEventsGraphSeries();
        StringBuilder output = new StringBuilder();
        Resources res = mActivity.getResources();  //TODO hodilo null
        String total = res.getString(R.string.stats_event_tot) + " " + pieChartData.getTotal();
        output.append(total + "\n");

        for (PieChartSerie serie : eventsGraphSeries) {
            output.append(mActivity.getLabelForCode(serie.getLabel()) + ": "
                    + serie.getTime() + " (" + serie.getPercent() + "%)" + "\n");
        }

        statsTextView.setText(output);
    }

}
