package imis.client.ui.fragments;

import android.app.Activity;
import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import imis.client.ui.activities.ChartActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 24.4.13
 * Time: 18:15
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
