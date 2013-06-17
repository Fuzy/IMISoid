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

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 2.6.13
 * Time: 19:11
 */
public class AuthConfirmDialog extends DialogFragment {
    private static final String TAG = AuthConfirmDialog.class.getSimpleName();
    private String title;
    private String message;

    public interface AuthConfirmDialogListener {
        public void onConfirmClickPositiveClick();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach()");
        Bundle arguments = getArguments();
        title = arguments.getString(AppConsts.KEY_TITLE);
        message = arguments.getString(AppConsts.KEY_MSG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AuthConfirmDialogListener activity = (AuthConfirmDialogListener) getActivity();
                        activity.onConfirmClickPositiveClick();
                    }
                })
                .setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }
}
