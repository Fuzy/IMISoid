package imis.client.ui.activities;

import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import imis.client.R;
import imis.client.controller.BlockController;
import imis.client.model.Block;
import imis.client.ui.fragments.StackedBarFragment;

import java.util.Arrays;
import java.util.List;

import static imis.client.persistent.EventManager.EventQuery;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 7.4.13
 * Time: 14:43
 */
public class EventsChartActivity extends NetworkingActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = EventsChartActivity.class.getSimpleName();

    private List<Block> blockList;
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    private static final int LOADER_ID = 0x03;

    private String[] kody_po_values;
    private String[] kody_po_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()" + savedInstanceState == null ? "true" : "false");
        setContentView(R.layout.events_chart);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        /*if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            PieChartFragment pieFragment = new PieChartFragment();
            ft.replace(R.id.displayChart, pieFragment, "PieChartFragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }*/

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            StackedBarFragment barFragment = new StackedBarFragment();
            ft.replace(R.id.displayChart, barFragment, "PieChartFragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }

        kody_po_values = getResources().getStringArray(R.array.kody_po_values);
        kody_po_desc = getResources().getStringArray(R.array.kody_po_desc);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader()");
        return new CursorLoader(getApplicationContext(), EventQuery.CONTENT_URI, EventQuery.PROJECTION_ALL, null, null,
                null);//TODO selekce EventQuery.SELECTION_DATUM, new String[]{String.valueOf(date)},
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished()"); //TODO pozor na pozici cursoru
        blockList = BlockController.eventsToMapOfBlocks(cursor);
        mDataSetObservable.notifyChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(TAG, "onLoaderReset()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        Log.d(TAG, "onRestoreInstanceState()");
        super.onRestoreInstanceState(savedState);
        /*mSeries = (CategorySeries) savedState.getSerializable("current_series");
        mRenderer = (DefaultRenderer) savedState.getSerializable("current_renderer");*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()");
       /* outState.putSerializable("current_series", mSeries);
        outState.putSerializable("current_renderer", mRenderer);*/
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public String getLabelForCode(String kod_po) {
        int index = Arrays.asList(kody_po_values).indexOf(kod_po);
        return kody_po_desc[index];
    }

    public List<Block> getBlockList() {
        return blockList;
    }
}
