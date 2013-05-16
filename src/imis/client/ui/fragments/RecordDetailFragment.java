package imis.client.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import imis.client.AppUtil;
import imis.client.R;
import imis.client.model.Record;
import imis.client.ui.ColorUtil;
import imis.client.ui.dialogs.ColorPickerDialog;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 18.4.13
 * Time: 23:14
 */
public class RecordDetailFragment extends Fragment {
    private static final String TAG = "RecordDetailFragment";

    private View detail;
    private Record record;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        Log.d(TAG, "onCreateView() container " + container);
        detail = inflater.inflate(R.layout.record_detail, container, false);
        //detail.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return detail;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (record != null) {
            String zc = (record.getZc() == null) ? "" : record.getZc();
            String cpolzak = (record.getCpolzak() == null) ? "" : "" + record.getCpolzak().intValue();
            String cpozzak = (record.getCpozzak() == null) ? "" : "" + record.getCpozzak().intValue();
            String identification = zc + "/" + cpolzak + "/" + cpozzak;

            TextView tt;
            tt = (TextView) detail.findViewById(R.id.record_detail_state);
            tt.setText(record.getStav_v());
            tt = (TextView) detail.findViewById(R.id.recordIdentification);
            tt.setText(identification);
            tt = (TextView) detail.findViewById(R.id.time);
            tt.setText(AppUtil.formatTime(record.getMnozstvi_odved()));
            tt = (TextView) detail.findViewById(R.id.record_detail_report);
            tt.setText(record.getPozn_hl());
            tt = (TextView) detail.findViewById(R.id.record_detail_task);
            tt.setText(record.getPozn_ukol());
            tt = (TextView) detail.findViewById(R.id.note);
            tt.setText(record.getPoznamka());
            tt = (TextView) detail.findViewById(R.id.record_type);
            tt.setBackgroundColor(ColorUtil.getColor(record.recordType()));
        }

        TextView tt = (TextView) detail.findViewById(R.id.record_type);
        tt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLongClick()");
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(50);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment dialog = new ColorPickerDialog(record.recordType());
                dialog.show(fm, "ColorPickerDialog");
                return false;
            }
        });
    }

    public void setRecord(Record record) {
        this.record = record;
    }

}
