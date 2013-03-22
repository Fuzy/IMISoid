package imis.client.ui.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import imis.client.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 12.3.13
 * Time: 11:05
 */
public class NetworkSettingsActivity extends Activity {
    private static final String TAG = NetworkSettingsActivity.class.getSimpleName();
    private ImageView imageHost, imageWebService, imageDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_settings);

        imageHost = (ImageView) findViewById(R.id.setHostImage);
        imageWebService = (ImageView) findViewById(R.id.setWSImage);
        imageDatabase = (ImageView) findViewById(R.id.setDBImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refreshState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        // Ziska menu z XML zdroje
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.network_options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshState();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshState() {
        Log.d("NetworkSettingsActivity", "refreshState()");
        new CheckHostReachability().execute(new String[]{"10.0.2.2"});
        new CheckWSReachability().execute(null);
    }

    private void setImageAsReachable(ImageView imageView) {
        imageView.setImageResource(R.drawable.btn_check_buttonless_on);
    }

    private void setImageAsUnreachable(ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_delete);
    }

    private class CheckHostReachability extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... host) {
            Log.d("NetworkSettingsActivity$CheckHostReachability", "doInBackground() host: " + host[0]);
            boolean isReachable = false;
            try {
                InetAddress inet = InetAddress.getByName(host[0]);//TODO
                isReachable = inet.isReachable(5000);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return isReachable;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("NetworkSettingsActivity$CheckHostReachability", "onPostExecute() result: " + result);
            Toast toast = Toast.makeText(getApplicationContext(), "test: " + result, Toast.LENGTH_LONG);
            toast.show();

            if (result) {
                setImageAsReachable(imageHost);
            } else {
                setImageAsUnreachable(imageHost);
            }
        }
    }

    private class CheckWSReachability extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Log.d("NetworkSettingsActivity$CheckWSReachability", "doInBackground()");
            String strUrl = "http://stackoverflow.com/about";

            try {
                URL url = new URL(strUrl);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                urlConn.connect();
                if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return true;
                }
            }
            catch (IOException e) {
                System.err.println("Error creating HTTP connection");
                e.printStackTrace();
                // throw e;
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("NetworkSettingsActivity$CheckWSReachability", "onPostExecute() result: " + result);
            Toast toast = Toast.makeText(getApplicationContext(), "test: " + result, Toast.LENGTH_LONG);
            toast.show();

            if (result) {
                setImageAsReachable(imageWebService);
            } else {
                setImageAsUnreachable(imageWebService);
            }
        }

    }
}

