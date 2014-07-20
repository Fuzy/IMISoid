package imis.client.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import imis.client.ui.activities.ChartActivity;

/**
 * Generic fragment which hosts chart.
 */
public abstract class ChartFragment extends Fragment {
    private static final String TAG = ChartFragment.class.getSimpleName();

    protected ChartActivity mActivity;
    protected View mChartContainerView;


    private DataSetObserver mObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            Log.d(TAG, "onChanged()");
            displayContent();
        }
    };

    protected abstract void displayContent();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ChartActivity) activity;
        mActivity.registerDataSetObserver(mObserver);
        Log.d(TAG, "onAttach() activity " + mActivity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity.unregisterDataSetObserver(mObserver);
        Log.d(TAG, "onDetach()");
    }

}
