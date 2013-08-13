package imis.client.data.graph;

import java.util.ArrayList;
import java.util.List;

/**
 *  Class storing data for use in pie chart.
 */
public class PieChartData {
    private String total;
    private List<PieChartSerie> eventsGraphSeries = new ArrayList<>();

    public void addSerie(PieChartSerie eventsGraphSerie) {
               eventsGraphSeries.add(eventsGraphSerie);
    }

    public List<PieChartSerie> getEventsGraphSeries() {
        return eventsGraphSeries;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "PieChartData{" +
                "total='" + total + '\'' +
                ", eventsGraphSeries=" + eventsGraphSeries +
                '}';
    }
}
