package imis.client.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import imis.client.R;
import imis.client.network.NetworkUtilities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 12.3.13
 * Time: 11:05
 */
public class NetworkSettingsActivity extends Activity {
    private static final String TAG = NetworkSettingsActivity.class.getSimpleName();
    private ImageView imageHost, imageWebService, imageDatabase;
    private EditText editTextIP, editTextPort;
    String address = null;
    StringBuilder errMsg = new StringBuilder();
    int port = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("NetworkSettingsActivity", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_settings);

        //imageHost = (ImageView) findViewById(R.id.setHostImage);
        imageWebService = (ImageView) findViewById(R.id.setWSImage);
        imageDatabase = (ImageView) findViewById(R.id.setDBImage);

        editTextIP = (EditText) findViewById(R.id.settEditIP);
        editTextIP.setText("10.0.0.3");
        editTextPort = (EditText) findViewById(R.id.settEditPort);
        editTextPort.setText("8081");
        Button testBut = (Button) findViewById(R.id.buttonTest);
        testBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testIPandPort();
            }
        });
    }

    private void testIPandPort() {
        address = editTextIP.getText().toString();
        try {
            port = Integer.parseInt(editTextPort.getText().toString());
        } catch (Exception e) {
            port = -1;
        }
        Log.d("NetworkSettingsActivity", "testIPandPort() address: " + address + " port: " + port);
        refreshState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //refreshState();
    }

    @Override
    protected void onStop() {
        Log.d("NetworkSettingsActivity", "onStop()");
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        /*Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();*/
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
            case R.id.networkSave:
                sendResultToDayTimelineActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendResultToDayTimelineActivity() {
        Log.d("NetworkSettingsActivity", "sendResultToDayTimelineActivity()");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("address", address);
        returnIntent.putExtra("port", port);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void refreshState() {
        Log.d("NetworkSettingsActivity", "refreshState()");
        if (address.length() != 0 && address != null) {
            //new CheckHostReachability().execute(new String[]{address});
            new CheckWebServiceAndDBAvailability().execute(null);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "address = null or 0", Toast.LENGTH_LONG);
            toast.show();
        }

        //new CheckWSReachability().execute(null);
    }

    private void setImageAsReachable(ImageView imageView) {
        imageView.setImageResource(R.drawable.btn_check_buttonless_on);
    }

    private void setImageAsUnreachable(ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_delete);
    }

   /* private class CheckHostReachability extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... host) {

            boolean isReachable = NetworkUtilities.testHostReachability(host[0], errMsg);
            Log.d("NetworkSettingsActivity$CheckHostReachability", "doInBackground() host: " + host[0] + " errMsg: " + errMsg);
            *//*try {
                InetAddress inet = InetAddress.getByName(host[0]);//TODO
                isReachable = inet.isReachable(5000);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*//*
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
    }*/

    private class CheckWebServiceAndDBAvailability extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Log.d("NetworkSettingsActivity$CheckWSReachability", "doInBackground()");
            return NetworkUtilities.testWebServiceAndDBAvailability();
        }


        @Override
        protected void onPostExecute(Integer result) {
            Log.d("NetworkSettingsActivity$CheckWSReachability", "onPostExecute() result: " + result);
            Toast toast = Toast.makeText(getApplicationContext(), "test: " + result, Toast.LENGTH_LONG);
            toast.show();

            setIconsOfAvailability(result);
        }

    }

    private void setIconsOfAvailability(int code) {
        switch (code) {
            case -1:
                setImageAsUnreachable(imageWebService);
                setImageAsUnreachable(imageDatabase);
                break;
            case HttpURLConnection.HTTP_OK:
                setImageAsReachable(imageWebService);
                setImageAsReachable(imageDatabase);
                break;
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                setImageAsReachable(imageWebService);
                setImageAsUnreachable(imageDatabase);
                break;
            default:
                break;
        }
    }
}

