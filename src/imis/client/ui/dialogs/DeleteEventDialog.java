package imis.client.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.R;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 9.4.13
 * Time: 16:56
 */
public class DeleteEventDialog extends DialogFragment {
    private static final String TAG = DeleteEventDialog.class.getSimpleName();
    public static final int DEL_ARRIVE = 0, DEL_LEAVE = 1, DEL_BOTH = 2;
    private int DEL_RESULT = -1;
    private CharSequence[] items;

    public interface OnDeleteEventListener {
        void deleteEvent(int deleteCode);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach()");
        Bundle arguments = getArguments();
        int size = 0;
        if (arguments.getInt(AppConsts.ID_ARRIVE) != -1) size++;
        if (arguments.getInt(AppConsts.ID_LEAVE) != -1) size++;
        if (arguments.getInt(AppConsts.ID_ARRIVE) != -1 && arguments.getInt(AppConsts.ID_LEAVE) != -1) size++;
        Log.d(TAG, "onAttach() size " + size);
        items = new CharSequence[size];
        String[] array = getActivity().getResources().getStringArray(R.array.delete_event_options);
        for (int i = 0; i < items.length; i++) {
            items[i] = array[i];
        }
        Log.d(TAG, "onAttach() items " + Arrays.toString(items));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int checkedItem = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.delete_event)
                .setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {

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
