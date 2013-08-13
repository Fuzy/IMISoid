package imis.client.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import imis.client.R;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.PieChartSerie;

import java.util.List;

/**
 * Fragment showing statistics.
 */
public class StatisticsFragment extends ChartFragment {
    private static final String TAG = StatisticsFragment.class.getSimpleName();
    private TextView statsTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        mChartContainerView = inflater.inflate(R.layout.statistics, container, false);

        return mChartContainerView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        statsTextView = (TextView) getView().findViewById(R.id.statsContent);
    }

    protected void displayContent() {
        PieChartData pieChartData = mActivity.getPieChartData();
        List<PieChartSerie> eventsGraphSeries = pieChartData.getEventsGraphSeries();
        StringBuilder output = new StringBuilder();
        String total = getString(R.string.stats_event_tot) + " " + pieChartData.getTotal();
        output.append(total + "\n");

        for (PieChartSerie serie : eventsGraphSeries) {
            output.append(serie.getLabel() + ": "
                    + serie.getTime() + " (" + serie.getPercent() + "%)" + "\n");
        }

        statsTextView.setText(output);
    }

}
