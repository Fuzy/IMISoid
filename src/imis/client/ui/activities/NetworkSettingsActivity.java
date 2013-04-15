package imis.client.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import imis.client.AppConsts;
import imis.client.R;
import imis.client.network.NetworkUtilities;
import imis.client.asynctasks.TestConnection;

import java.net.HttpURLConnection;

import static imis.client.AppConsts.KEY_DOMAIN;
import static imis.client.AppConsts.KEY_PORT;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 12.3.13
 * Time: 11:05
 */
public class NetworkSettingsActivity extends NetworkingActivity {
    private static final String TAG = NetworkSettingsActivity.class.getSimpleName();
    private ImageView imageWebService, imageDatabase;
    private EditText editTextDomain, editTextPort;
    String domain = null;
    int port = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("NetworkSettingsActivity", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_settings);

        imageWebService = (ImageView) findViewById(R.id.setWSImage);
        imageDatabase = (ImageView) findViewById(R.id.setDBImage);

        editTextDomain = (EditText) findViewById(R.id.settEditIP);
        editTextPort = (EditText) findViewById(R.id.settEditPort);

        editTextDomain.setText(NetworkUtilities.getDomainOrDefault());
        editTextPort.setText(NetworkUtilities.getPortOrDefault());

        Button testBut = (Button) findViewById(R.id.buttonTest);
        testBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testIPandPort();
            }
        });
    }

    private void testIPandPort() {
        Log.d("NetworkSettingsActivity", "testIPandPort() domain: " + domain + " port: " + port);
        readAndSaveDomainAndPort();
        refreshState();
    }

    private void readAndSaveDomainAndPort() {
        readDomainAndPort();
        setNetworkDomainAndPort();
    }

    private void readDomainAndPort() {
        domain = editTextDomain.getText().toString();
        try {
            port = Integer.parseInt(editTextPort.getText().toString());
        } catch (Exception e) {
            port = -1;
        }
    }

    @Override
    protected void onStop() {
        Log.d("NetworkSettingsActivity", "onStop()");
        super.onStop();
        saveNetworkSettingsToSharedPrefs();
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
                saveDomainAndPortAndFinish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveDomainAndPortAndFinish() {
        Log.d("NetworkSettingsActivity", "saveDomainAndPort()");
        readAndSaveDomainAndPort();
        setResult(RESULT_OK, new Intent());
        finish();
    }

    private void saveNetworkSettingsToSharedPrefs() {
        Log.d("NetworkSettingsActivity", "saveNetworkSettingsToSharedPrefs()");
        //TODO tohle volat v onstop
        SharedPreferences settings = getSharedPreferences(AppConsts.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_DOMAIN, domain);
        editor.putInt(KEY_PORT, port);
        editor.commit();
    }

    private void setNetworkDomainAndPort() {
        NetworkUtilities.resetDomainAndPort(domain, port);
    }

    private void refreshState() {
        Log.d("NetworkSettingsActivity", "refreshState()");
        if (domain.length() != 0 && domain != null) {
            if (NetworkUtilities.isOnline(getApplicationContext()) == false) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.network_unavailable, Toast.LENGTH_LONG);
                toast.show(); //TODO
                return;
            }
            new TestConnection(this).execute(null);
        }
    }

    private void setImageAsReachable(ImageView imageView) {
        imageView.setImageResource(R.drawable.btn_check_buttonless_on);
    }

    private void setImageAsUnreachable(ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_delete);
    }

    /*private class CheckWebServiceAndDBAvailability extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
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

    }*/

    public void setIconsOfAvailability(int code) {
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

