package imis.client.ui.fragments;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import imis.client.AppConsts;
import imis.client.R;
import imis.client.TimeUtil;
import imis.client.model.Block;
import imis.client.ui.BlockView;
import imis.client.ui.BlocksLayout;
import imis.client.ui.ObservableScrollView;
import imis.client.ui.activities.DayTimelineActivity;
import imis.client.ui.adapters.EventsArrayAdapter;
import imis.client.ui.dialogs.ColorPickerDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 15.6.13
 * Time: 17:06
 */
public class DayTimelineBlocksFragment extends Fragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {
    private static final String TAG = DayTimelineBlocksFragment.class.getSimpleName();
    private BlocksLayout blocks;
    private ObservableScrollView scroll;
    private List<Block> blockList;
    private EventsArrayAdapter adapter;
    private DayTimelineActivity mActivity;


    private DataSetObserver mObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            Log.d(TAG, "onChanged()");
            resfreshAdaptersDataList();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach()");
        mActivity = (DayTimelineActivity) activity;
        mActivity.registerDataSetObserver(mObserver);
    }

    /*@Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        mActivity.performScroll();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        if (container == null) {
            return null;
        }
        scroll = (ObservableScrollView) inflater.inflate(R.layout.blocks_content, container, false);
        blocks = (BlocksLayout) scroll.findViewById(R.id.blocks);
        blocks.setOnItemClickListener(this);
        blocks.setOnItemLongClickListener(this);
        return scroll;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");
        blockList = new ArrayList<>();
        adapter = new EventsArrayAdapter(getActivity(), -1, blockList);
        blocks.setAdapter(adapter);
    }

    void resfreshAdaptersDataList() {
        Log.d(TAG, "resfreshAdaptersDataList()");
        if (adapter == null) return;
        adapter.clear();
        adapter.setDate(mActivity.getDate());
        blockList = null;
        if (mActivity.getCursor() != null) {
            blockList = mActivity.getProcessor().eventsToMapOfBlocks(mActivity.getCursor());
            adapter.addAll(blockList);
            adapter.notifyDataSetChanged();
        }
        blocks.setVisibility(View.GONE);
        blocks.setVisibility(View.VISIBLE);
        mActivity.performScroll();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BlockView block = (BlockView) view;
        int arriveID = block.getArriveId(), leaveID = block.getLeaveId();
        Log.d(TAG, "onItemClick() position: " + position + " id: " + id + " arriveID: " + arriveID
                + " leaveID: " + leaveID);
        mActivity.startEditActivity(arriveID, leaveID);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("DayTimelineActivity", "onItemLongClick() position: " + position);
        BlockView block = (BlockView) view;
        DialogFragment dialog = new ColorPickerDialog();
        Bundle bundle = new Bundle();
        bundle.putString(AppConsts.KEY_TYPE, block.getType());
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "ColorPickerDialog");
        return true;
    }

    public void scrollTo() {
        int y = (int) (((double) TimeUtil.currentDayTimeInLong() / (double) AppConsts.MS_IN_DAY) * blocks.getHeight());
        y -= blocks.getHeight() / 4;
        y = (y < 0) ? 0 : y;
        scroll.scrollTo(0, y);
    }

}