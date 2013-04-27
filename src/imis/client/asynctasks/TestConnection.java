package imis.client.asynctasks;

import android.util.Log;
import imis.client.asynctasks.result.TestConnectionResultData;
import imis.client.network.NetworkUtilities;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 10.4.13
 * Time: 0:13
 */
public class TestConnection extends NetworkingAsyncTask<Void, Void, Integer> {
    private static final String TAG = "TestConnection";

    @Override
    protected Integer doInBackground(Void... objects) {
        int statusCode = NetworkUtilities.testWebServiceAndDBAvailability();
        return statusCode;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        Log.d(TAG, "onPostExecute()");
        TestConnectionResultData result = new TestConnectionResultData();
        result.setCode(integer);
        resultData = result;

        super.onPostExecute(null);
    }
}
