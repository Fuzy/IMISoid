package imis.client.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import imis.client.AppConsts;
import imis.client.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for confirm deleting of event.
 */
public class DeleteEventDialog extends DialogFragment {
    private static final String TAG = DeleteEventDialog.class.getSimpleName();
    public static final int DEL_ARRIVE = 0, DEL_LEAVE = 1, DEL_BOTH = 2;
    private int DEL_RESULT = -1;
    private List<CharSequence> items = new ArrayList<>();

    public interface OnDeleteEventListener {
        void deleteEvent(int deleteCode);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle arguments = getArguments();
        if (arguments.getInt(AppConsts.ID_ARRIVE) != -1) items.add(getString(R.string.deleteChoiceArrive));
        if (arguments.getInt(AppConsts.ID_LEAVE) != -1) items.add(getString(R.string.deleteChoiceLeave));
        if (arguments.getInt(AppConsts.ID_ARRIVE) != -1 && arguments.getInt(AppConsts.ID_LEAVE) != -1)
            items.add(getString(R.string.deleteChoiceBoth));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int checkedItem = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final CharSequence[] choices = items.toArray(new CharSequence[items.size()]);
        builder.setTitle(R.string.delete_event)
                .setSingleChoiceItems(choices,
                        checkedItem, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String selected = choices[i].toString();
                        if (getString(R.string.deleteChoiceArrive).equals(selected)) {
                            DEL_RESULT = DEL_ARRIVE;
                        } else if (getString(R.string.deleteChoiceLeave).equals(selected)) {
                            DEL_RESULT = DEL_LEAVE;
                        } else if (getString(R.string.deleteChoiceBoth).equals(selected)) {
                            DEL_RESULT = DEL_BOTH;
                        }
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        OnDeleteEventListener activity = (OnDeleteEventListener) getActivity();
                        activity.deleteEvent(DEL_RESULT);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        return builder.create();
    }
}
