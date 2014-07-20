package imis.client.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import imis.client.AppConsts;
import imis.client.R;

/**
 * Dialog for adding an account.
 */
public class AddAccountDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.no_account_set_title)
                .setMessage(R.string.no_account_set_desc)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        redirectToCreateAccount();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    private void redirectToCreateAccount() {
        Intent intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
        intent.putExtra(Settings.EXTRA_AUTHORITIES, new String[]{AppConsts.AUTHORITY1, AppConsts.AUTHORITY2,
                AppConsts.AUTHORITY3});
        getActivity().startActivity(intent);
    }
}
