package imis.client.asynctasks;

import android.app.Activity;
import imis.client.network.NetworkUtilities;
import imis.client.ui.activities.NetworkSettingsActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 10.4.13
 * Time: 0:13
 */
public class TestConnection extends NetworkingAsyncTask<Void, Void, Integer> {
    private static final String TAG = "TestConnection";

    public TestConnection(Activity context) {
        super(context);
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int statusCode = NetworkUtilities.testWebServiceAndDBAvailability();
        return statusCode;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(null);
        NetworkSettingsActivity netSettingsActivity = (NetworkSettingsActivity) activity;
        netSettingsActivity.setIconsOfAvailability(integer);
    }
}
