package imis.client.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import imis.client.AppConsts;
import imis.client.R;
import imis.client.TimeUtil;
import imis.client.model.Record;
import imis.client.ui.ColorConfig;
import imis.client.ui.dialogs.ColorPickerDialog;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 18.4.13
 * Time: 23:14
 */
public class RecordDetailFragment extends Fragment {
    private static final String TAG = RecordDetailFragment.class.getSimpleName();

//    private ColorConfig colorConfig;
    private Context context;
    private View detail;
    private Record record;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        colorConfig = new ColorConfig(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
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
            tt = (TextView) detail.findViewById(R.id.recordKodpra);
            tt.setText(record.getKodpra());
            tt = (TextView) detail.findViewById(R.id.date);
            tt.setText(TimeUtil.formatAbbrDate(record.getDatum()));
            tt = (TextView) detail.findViewById(R.id.time);
            tt.setText(TimeUtil.formatTimeInNonLimitHour(record.getMnozstvi_odved()));
            tt = (TextView) detail.findViewById(R.id.record_detail_report);
            tt.setText(record.getPozn_hl());
            tt = (TextView) detail.findViewById(R.id.record_detail_task);
            tt.setText(record.getPozn_ukol());
            tt = (TextView) detail.findViewById(R.id.note);
            tt.setText(record.getPoznamka());
            tt = (TextView) detail.findViewById(R.id.record_type);
            tt.setBackgroundColor(ColorConfig.getColor(context,record.recordType()));
        }

        TextView tt = (TextView) detail.findViewById(R.id.record_type);
        tt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLongClick()");
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(50);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment dialog = new ColorPickerDialog();
                Bundle bundle = new Bundle();
                bundle.putString(AppConsts.KEY_TYPE, record.recordType());
                dialog.setArguments(bundle);
                dialog.show(fm, "ColorPickerDialog");
                return false;
            }
        });
    }

    public void setRecord(Record record) {
        this.record = record;
    }

}
