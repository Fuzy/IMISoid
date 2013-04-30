package imis.client.asynctasks;

import android.os.AsyncTask;
import android.util.Log;
import imis.client.asynctasks.result.ResultData;
import imis.client.ui.fragments.TaskFragment;

import java.io.Serializable;
import java.util.Arrays;

public abstract class NetworkingAsyncTask<T, U, V> extends AsyncTask<T, U, V> implements Serializable {
    private static final String TAG = NetworkingAsyncTask.class.getSimpleName();
    protected TaskFragment mFragment;
    protected ResultData resultData;
    protected T[] params;

    protected NetworkingAsyncTask(T... params) {
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
            Log.d(TAG, "onPostExecute() resultData " + resultData);
            mFragment.taskFinished(resultData);
        }

        super.onPostExecute(null);
    }


}
