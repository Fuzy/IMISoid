package imis.client.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Message;
import imis.client.AppConsts;
import imis.client.R;
import imis.client.ui.activities.NetworkingActivity;
import imis.client.ui.activities.ProgressState;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.Serializable;
import java.net.UnknownHostException;

//TODO lepsi neposilat zpravu
public abstract class NetworkingService<T, U, V> extends AsyncTask<T, U, V> implements Serializable {

	private static final long serialVersionUID = -1800322528900745205L;
	protected NetworkingActivity activity;
	private ProgressState state;

	public NetworkingService(NetworkingActivity context) {
		this.activity = context;
	}

	protected void changeProgress(ProgressState state, int messageId) {
		changeProgress(state, activity.getString(messageId));
	}

	protected void changeProgress(ProgressState state, Object messageBody) {
		this.state = state;
		Message msg = Message.obtain();

		if (state == ProgressState.ERROR && messageBody instanceof Exception) {
			msg.obj = getErrorMessage((Exception) messageBody);
		} else {
			msg.obj = messageBody;
		}
		activity.changeProgress(state, msg);
	}

	private String getErrorMessage(Exception exception) {

		if (exception instanceof HttpClientErrorException) {
			HttpClientErrorException httpException = (HttpClientErrorException) exception;

			if (HttpStatus.BAD_REQUEST.equals(httpException.getStatusCode())) {
				return activity.getString(R.string.http_400);
			} else if (HttpStatus.UNAUTHORIZED.equals(httpException.getStatusCode())) {
				return activity.getString(R.string.http_401);
			} else if (HttpStatus.FORBIDDEN.equals(httpException.getStatusCode())) {
				return activity.getString(R.string.http_403);
			} else if (HttpStatus.NOT_FOUND.equals(httpException.getStatusCode())) {
				return activity.getString(R.string.http_404);
			} else if (HttpStatus.METHOD_NOT_ALLOWED.equals(httpException.getStatusCode())) {
				return activity.getString(R.string.http_405);
			} else if (HttpStatus.REQUEST_TIMEOUT.equals(httpException.getStatusCode())) {
				return activity.getString(R.string.http_408);
			} else if (HttpStatus.INTERNAL_SERVER_ERROR.equals(R.string.http_500)) {
				return activity.getString(R.string.http_500);
			} else if (HttpStatus.SERVICE_UNAVAILABLE.equals(httpException.getStatusCode())) {
				return activity.getString(R.string.http_503);
			}
		}

		if (exception.getMessage().toLowerCase().contains("timed out")) {
			return "Connection timed out. Check if you're connected.";
		}

		if (exception.getCause() instanceof UnknownHostException)
			return activity.getString(R.string.error_unknown_host);

		return exception.getLocalizedMessage();
	}
	
	public boolean isActive(){
		return (state == ProgressState.RUNNING);
	}

	protected SharedPreferences getCredentials() {
		return activity.getSharedPreferences(AppConsts.PREFS_CREDENTIALS, Context.MODE_PRIVATE);
	}
}
