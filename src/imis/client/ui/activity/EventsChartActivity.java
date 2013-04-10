package imis.client.ui.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
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

import static imis.client.persistent.EventManager.EventQuery;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 7.4.13
 * Time: 14:43
 */
public class EventsChartActivity extends NetworkingActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private CategorySeries mSeries = new CategorySeries("");

    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;
    private static final int LOADER_ID = 0x03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_chart);

        getLoaderManager().initLoader(LOADER_ID, null, this);

        initRenderer();

        String[] kody_po_desc = getResources().getStringArray(R.array.kody_po_desc);

        mSeries.add(kody_po_desc[0], 200.0);
        SimpleSeriesRenderer renderer1 = new SimpleSeriesRenderer();
        renderer1.setColor(ColorUtil.getColor_present_normal());//TODO
        mRenderer.addSeriesRenderer(renderer1);

        mSeries.add(kody_po_desc[10], 10.0);


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

    private void initRenderer() {
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(30);
        mRenderer.setLegendTextSize(30);
        mRenderer.setMargins(new int[]{20, 30, 15, 0});
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(90);
    }

    private void setSeriesRenderer(int color) {
        SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
        renderer.setColor(color);//TODO
        mRenderer.addSeriesRenderer(renderer);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getApplicationContext(), EventQuery.CONTENT_URI, EventQuery.PROJECTION_ALL, null, null,
                null);//TODO selekce EventQuery.SELECTION_DATUM, new String[]{String.valueOf(date)},
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
