package imis.client.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import imis.client.R;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 12.3.13
 * Time: 11:05
 */
public class NetworkSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_settings);
    }
}

