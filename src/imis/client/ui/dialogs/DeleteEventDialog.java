package imis.client.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import imis.client.R;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 9.4.13
 * Time: 16:56
 */
public class DeleteEventDialog extends DialogFragment {
    public static final int DEL_ARRIVE = 0, DEL_LEAVE = 1, DEL_BOTH = 2;
    private int DEL_RESULT = -1;

    public interface OnDeleteEventListener {
        void deleteEvent(int deleteCode);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int checkedItem = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.delete_event)
                .setSingleChoiceItems(R.array.delete_event_options, checkedItem, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DEL_RESULT = i;
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
