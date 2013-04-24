package imis.client.ui.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import imis.client.R;
import imis.client.data.graph.PieChartData;
import imis.client.data.graph.PieChartSerie;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 15.4.13
 * Time: 17:32
 */
public class StatisticsFragment extends ChartFragment {
    private static final String TAG = "StatisticsFragment";

    private TextView statsTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
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
        Resources res = mActivity.getResources();  //TODO hodilo null
        String total = res.getString(R.string.stats_event_tot) + " " + pieChartData.getTotal();
        output.append(total + "\n");

        for (PieChartSerie serie : eventsGraphSeries) {
            output.append(serie.getLabel() + ": "
                    + serie.getTime() + " (" + serie.getPercent() + "%)" + "\n");
        }

        statsTextView.setText(output);
    }

}
