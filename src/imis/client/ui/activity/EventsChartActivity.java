package imis.client.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import imis.client.R;
import imis.client.ui.ColorUtil;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 7.4.13
 * Time: 14:43
 */
public class EventsChartActivity extends Activity {
    private CategorySeries mSeries = new CategorySeries("");

    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_chart);

        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(30);
        mRenderer.setLegendTextSize(30);
        mRenderer.setMargins(new int[]{20, 30, 15, 0});
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(90);

        String[] kody_po_desc = getResources().getStringArray(R.array.kody_po_desc);

        mSeries.add(kody_po_desc[0], 200.0);
        SimpleSeriesRenderer renderer1 = new SimpleSeriesRenderer();
        renderer1.setColor(ColorUtil.getColor_present_normal());//TODO
        mRenderer.addSeriesRenderer(renderer1);

        mSeries.add(kody_po_desc[10], 10.0);
        SimpleSeriesRenderer renderer2 = new SimpleSeriesRenderer();
        renderer2.setColor(Color.YELLOW);//TODO
        mRenderer.addSeriesRenderer(renderer2);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout layout = (LinearLayout) findViewById(R.id.chartEvents);
        if (mChartView == null) {
            mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);

            layout.addView(mChartView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            mChartView.repaint();
        }
    }
}
