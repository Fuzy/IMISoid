package imis.client.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import imis.client.R;
import imis.client.asynctasks.TestConnection;
import imis.client.asynctasks.result.Result;
import imis.client.network.NetworkConfig;
import imis.client.network.NetworkConsts;
import imis.client.network.NetworkUtilities;

import java.net.HttpURLConnection;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 12.3.13
 * Time: 11:05
 */
public class NetworkSettingsActivity extends AsyncActivity {
    private static final String TAG = NetworkSettingsActivity.class.getSimpleName();
    private ImageView imageWebService, imageDatabase;
    private EditText editTextDomain, editTextPort;
    private String domain = null;
    private int port = -1;
    private boolean isTest = false;
    //TODO save state
    private static final int IND_DOMAIN = 3, IND_PORT = 4, IND_TEST = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("NetworkSettingsActivity", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_settings);

        imageWebService = (ImageView) findViewById(R.id.setWSImage);
        imageDatabase = (ImageView) findViewById(R.id.setDBImage);

        editTextDomain = (EditText) findViewById(R.id.settEditIP);
        editTextPort = (EditText) findViewById(R.id.settEditPort);

        extractBaseURI();

        Button testBut = (Button) findViewById(R.id.buttonTest);
        testBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testIPandPort();
            }
        });
    }

    private void extractBaseURI() {
        String baseUri = NetworkConfig.getBaseURI(this);
        if (baseUri != null) {
            Log.d(TAG, "extractBaseURI() baseUri " + baseUri);
            String[] split = baseUri.split("[:/]");
            Log.d(TAG, "extractBaseURI() split " + Arrays.toString(split));

            if (split.length > IND_DOMAIN) parseDomain(split[IND_DOMAIN]);
            if (split.length > IND_PORT) parsePort(split[IND_PORT]);
            if (split.length > IND_TEST) isTest = split[IND_TEST].equals(NetworkConsts.TEST_MODE);

            String domainText = (isTest) ? domain.concat("/" + NetworkConsts.TEST_MODE) : domain;
            isTest = false;
            editTextDomain.setText(domainText);
            editTextPort.setText(Integer.toString(port));
        }
    }

    private void testIPandPort() {
        Log.d("NetworkSettingsActivity", "testIPandPort() domain: " + domain + " port: " + port);
        readDomainAndPort();
        NetworkUtilities.applyDomainAndPort(this, domain, port, isTest);
        refreshState();
    }

    private void readDomainAndPort() {
        parseDomain(domain = editTextDomain.getText().toString());
        parsePort(editTextPort.getText().toString());
    }

    private void parseDomain(String domainS) {
        String[] split = domainS.split("[/]");
        if (split.length > 0) domain = split[0];
        if (split.length > 1) {
            isTest = split[1].equals(NetworkConsts.TEST_MODE);
        }
    }

    private void parsePort(String portS) {
        try {
            port = Integer.parseInt(portS);
        } catch (Exception e) {
            port = -1;
        }
    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.save:
                saveDomainAndPortAndFinish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/


    private void refreshState() {
        Log.d("NetworkSettingsActivity", "refreshState()");
        if (domain.length() != 0 && domain != null) {
            createTaskFragment(new TestConnection(this));
        }
    }

    @Override
    protected void processAsyncTask() {
        Log.d(TAG, "processAsyncTask()");
    }

    private void setImageAsReachable(ImageView imageView) {
        imageView.setImageResource(R.drawable.btn_check_buttonless_on);
    }

    private void setImageAsUnreachable(ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_delete);
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

    @Override
    public void onTaskFinished(Result result) {
        int code = -1;
        if (result.getStatusCode() != null) {
            code = result.getStatusCode().value();
        }
        Log.d(TAG, "onTaskFinished() code " + code);
        setIconsOfAvailability(code);
    }
}

