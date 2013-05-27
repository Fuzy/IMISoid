package imis.client.sync.eventssync;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 25.5.13
 * Time: 22:35
 */
public class SyncSettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SyncSettingsFragment()).commit();
    }
}
