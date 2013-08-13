package imis.client.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import imis.client.R;
import imis.client.ui.activities.NetworkSettingsActivity;

/**
 * Dialog calling for setting connection to server.
 */
public class SetNetworkDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.connection_unavailable)
                .setMessage(R.string.connection_unavailable_desc)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        redirectToNetworkSettings();
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

    private void redirectToNetworkSettings() {
        Intent intent = new Intent(getActivity(), NetworkSettingsActivity.class);
        getActivity().startActivity(intent);
    }
}
