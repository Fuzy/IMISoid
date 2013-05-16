package imis.client.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import imis.client.R;
import imis.client.model.Record;
import imis.client.persistent.RecordManager;
import imis.client.ui.dialogs.ColorPickerDialog;
import imis.client.ui.fragments.RecordDetailFragment;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 16.5.13
 * Time: 13:38
 */
public class RecordDetailActivity extends FragmentActivity implements ColorPickerDialog.OnColorChangedListener {
    private static final String TAG = RecordDetailActivity.class.getSimpleName();

    private Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.record_detail_activity);

        long id = getIntent().getLongExtra(Record.COL_ID, -1);

        record = RecordManager.getRecord(this, id);

        refreshDetailFragment();
    }

    private void refreshDetailFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        RecordDetailFragment detailFragment = new RecordDetailFragment();
        detailFragment.setRecord(record);
        ft.replace(R.id.record, detailFragment, "RecordDetailFragment");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    public void colorChanged() {
        refreshDetailFragment();
    }
}
