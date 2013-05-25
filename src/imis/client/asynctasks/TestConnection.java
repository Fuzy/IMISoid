package imis.client.asynctasks;

import android.content.Context;
import android.util.Log;
import imis.client.asynctasks.result.Result;
import imis.client.network.NetworkUtilities;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 10.4.13
 * Time: 0:13
 */
public class TestConnection extends NetworkingAsyncTask<Void, Void, Result> {
    private static final String TAG = "TestConnection";

    public TestConnection(Context context, Void... params) {
        super(context, params);
    }

    @Override
    protected Result doInBackground(Void... objects) {
        return NetworkUtilities.testWebServiceAndDBAvailability();
    }

    @Override
    protected void onPostExecute(Result test) {
        Log.d(TAG, "onPostExecute()");
        super.onPostExecute(test);
    }
}
