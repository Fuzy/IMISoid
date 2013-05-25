package imis.client.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import imis.client.asynctasks.result.Result;
import imis.client.ui.fragments.TaskFragment;

import java.io.Serializable;
import java.util.Arrays;

public abstract class NetworkingAsyncTask<T, U, V> extends AsyncTask<T, U, V> implements Serializable {
    private static final String TAG = NetworkingAsyncTask.class.getSimpleName();
    protected TaskFragment mFragment;
    protected T[] params;
    protected final Context context;

    @SuppressWarnings({"unchecked", "varargs"})
    protected NetworkingAsyncTask(Context context, T... params) {
        this.context = context;
        this.params = params;
    }


    public void setFragment(TaskFragment fragment) {
        mFragment = fragment;
    }

    public void execute() {
        Log.d(TAG, "execute() params: " + Arrays.toString(params));
        this.execute(params);
    }

    @Override
    protected void onPostExecute(V v) {

        Log.d(TAG, "onPostExecute()");

        if (mFragment != null) {
            Log.d(TAG, "onPostExecute() resultData " + v);
            mFragment.taskFinished((Result)v);
        }

        super.onPostExecute(null);
    }


}
