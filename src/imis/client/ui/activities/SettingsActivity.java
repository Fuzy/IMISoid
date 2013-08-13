package imis.client.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import imis.client.ui.fragments.SettingsFragment;


/**
 * Activity hosts user settings.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
