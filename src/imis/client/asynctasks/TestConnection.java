package imis.client.asynctasks;

import android.content.Context;
import android.util.Log;
import imis.client.asynctasks.result.Result;
import imis.client.network.NetworkUtilities;

/**
 * Asynchronous task for testing connection to server.
 */
public class TestConnection extends NetworkingAsyncTask<Void, Void, Result> {
    private static final String TAG = "TestConnection";

    public TestConnection(Context context, Void... params) {
        super(context, params);
    }

    @Override
    protected Result doInBackground(Void... objects) {
        return NetworkUtilities.testWebServiceAndDBAvailability(context);
    }

    @Override
    protected void onPostExecute(Result test) {
        Log.d(TAG, "onPostExecute()");
        super.onPostExecute(test);
    }
}
