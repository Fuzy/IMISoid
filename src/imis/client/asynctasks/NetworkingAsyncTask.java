package imis.client.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import imis.client.R;

import java.io.Serializable;

public abstract class NetworkingAsyncTask<T, U, V> extends AsyncTask<T, U, V> implements Serializable {
    private static final String TAG = NetworkingAsyncTask.class.getSimpleName();
    private ProgressDialog dialog = null;
    protected Activity activity;

	public NetworkingAsyncTask(Activity context) {
		this.activity = context;
	}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "onPreExecute()");
        dialog = new ProgressDialog(activity);
        String cancel = activity.getResources().getString(R.string.cancel);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onCancel()");
                cancel(true);
            }
        });
        String message = activity.getResources().getString(R.string.working);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.setIndeterminate(true);
        dialog.show();
    }

    @Override
    protected void onPostExecute(V v) {
        super.onPostExecute(v);
        Log.d(TAG, "onPostExecute()");
        dialog.dismiss();
    }
}
