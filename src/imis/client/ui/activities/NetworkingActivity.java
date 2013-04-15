package imis.client.ui.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import imis.client.R;
import imis.client.asynctasks.NetworkingService;

public abstract class NetworkingActivity extends FragmentActivity {

	private volatile ProgressDialog wsProgressDialog;
	protected volatile boolean progressOn = false;
	private String progressTitle;
	private String progressMessage;
	protected NetworkingService<?,?,?> service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {

			synchronized (NetworkingActivity.class) {
				progressOn = savedInstanceState.getBoolean("progressOn", false);
			}

			if (progressOn) {
				service = (NetworkingService<?,?,?>) savedInstanceState.get("service");
				if (service.isActive()) {
					progressTitle = savedInstanceState.getString("progressTitle");
					progressMessage = savedInstanceState.getString("progressMessage");
					wsProgressDialog = ProgressDialog.show(NetworkingActivity.this, progressTitle, progressMessage, true, false);
				}
			}
		}
	}

	public void showAlert(String alert) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(alert).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	@Override
	protected void onPause() {
		if (wsProgressDialog != null && wsProgressDialog.isShowing()) {
			wsProgressDialog.dismiss();
		}
		super.onPause();
	}

	public void changeProgress(final ProgressState messageType, final Message message) {
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				synchronized (NetworkingActivity.class) {
					switch (messageType) {
					case RUNNING:
						progressOn = true;
						if (wsProgressDialog != null && !wsProgressDialog.isShowing())
							wsProgressDialog.show();
						else {
							progressTitle = getString(R.string.working);
							progressMessage = (String) message.obj;
							wsProgressDialog = ProgressDialog.show(NetworkingActivity.this, progressTitle, progressMessage, true, false);
						}
						break;
					//case INACTIVE:
					case DONE:
						if (wsProgressDialog != null && wsProgressDialog.isShowing()) {
							wsProgressDialog.dismiss();
							wsProgressDialog = null;
						}
						progressOn = false;
						service = null;
						break;
					case ERROR:
						showAlert(message.obj.toString());
						service = null;
					default:
						break;
					}
				}
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		synchronized (NetworkingActivity.class) {
			super.onSaveInstanceState(outState);
			if (service != null && service.isActive()) {
				outState.putSerializable("service", service);
				outState.putBoolean("progressOn", progressOn);
				outState.putString("progressTitle", progressTitle);
				outState.putString("progressMessage", progressMessage);
			}
		}
	}

}
