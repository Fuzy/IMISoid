package imis.client.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import imis.client.R;
import imis.client.asynctasks.NetworkingAsyncTask;
import imis.client.asynctasks.result.Result;

/**
 * Fragment showing progress dialog for asynchronous task.
 */
public class TaskFragment extends DialogFragment {
    private static final String TAG = TaskFragment.class.getSimpleName();

    // The task we are running.
    private NetworkingAsyncTask mTask;

    private OnAsyncActionCompletedListener mCallbacks = sDummyCallbacks;

    private static final OnAsyncActionCompletedListener sDummyCallbacks = new OnAsyncActionCompletedListener() {
        public void onTaskFinished(Result result) {
        }
    };

    public interface OnAsyncActionCompletedListener {
        public void onTaskFinished(Result result);
    }

    public void setTask(NetworkingAsyncTask task) {
        Log.d(TAG, "setTask()");
        mTask = task;

        // Tell the AsyncTask to call taskFinished() on this fragment.
        mTask.setFragment(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach()");
        if (!(activity instanceof OnAsyncActionCompletedListener)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (OnAsyncActionCompletedListener) activity;
        Log.d(TAG, "onAttach() activity " + activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        // Retain this instance so it isn't destroyed when Activity and change configuration.
        setRetainInstance(true);

        Log.d(TAG, "onCreate() execute");
        // Start the task! You could move this outside this activity if you want.
        if (mTask != null)
            mTask.execute();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog()");
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        String cancel = getResources().getString(R.string.cancel);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onCancel()");
                mTask.cancel(false);
            }
        });
        String message = getActivity().getResources().getString(R.string.working);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.setIndeterminate(true);
        return dialog;
    }

    // This is to work around what is apparently a bug. If you don't have it
    // here the dialog will be dismissed on rotation, so tell it not to dismiss.
    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView()");
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    // Also when we are dismissed we need to cancel the task.
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(TAG, "onDismiss()");
        // If true, the thread is interrupted immediately, which may do bad things.
        // If false, it guarantees a result is never returned (onPostExecute() isn't called)
        // but you have to repeatedly call isCancelled() in your doInBackground()
        // function to check if it should exit. For some tasks that might not be feasible.
        if (mTask != null) mTask.cancel(false);


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        // This is a little hacky, but we will see if the task has finished while we weren't
        // in this activity, and then we can dismiss ourselves.
        if (mTask == null) dismiss();
    }

    // This is also called by the AsyncTask.
    public void taskFinished(Result result) {
        Log.d(TAG, "taskFinished() result " + result);
        // Make sure we check if it is resumed because we will crash if trying to dismiss the dialog
        // after the user has switched to another app.
        if (isResumed()) dismiss();

        // If we aren't resumed, setting the task to null will allow us to dismiss ourselves in
        // onResume().
        mTask = null;

        // Tell the fragment that we are done.
        mCallbacks.onTaskFinished(result);

    }

}
