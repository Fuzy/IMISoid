package imis.client.services;

import imis.client.R;
import imis.client.network.NetworkUtilities;
import imis.client.ui.activities.NetworkSettingsActivity;
import imis.client.ui.activities.NetworkingActivity;

import static imis.client.ui.activities.ProgressState.DONE;
import static imis.client.ui.activities.ProgressState.RUNNING;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 10.4.13
 * Time: 0:13
 */
public class TestConnection extends NetworkingService<Void, Void, Integer> {

    private static final String TAG = "TestConnection";

    public TestConnection(NetworkingActivity context) {
        super(context);
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        changeProgress(RUNNING, R.string.test_connection);
        int statusCode = NetworkUtilities.testWebServiceAndDBAvailability();
        changeProgress(DONE, null);
        return statusCode;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        NetworkSettingsActivity netSettingsActivity = (NetworkSettingsActivity) activity;
        netSettingsActivity.setIconsOfAvailability(integer);
    }
}
