package imis.client.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import imis.client.AppUtil;
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
    private int port = -1, code = -1;
    private boolean isTest = false;
    private static final int IND_DOMAIN = 3, IND_PORT = 4, IND_TEST = 6;
    private static final String KEY_DOMAIN = "key_domain", KEY_PORT = "key_port", KEY_CODE = "key_code";

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

            if (split.length > IND_DOMAIN) parseDomain(split[IND_DOMAIN]);
            if (split.length > IND_PORT) parsePort(split[IND_PORT]);
            if (split.length > IND_TEST) isTest = split[IND_TEST].equals(NetworkConsts.TEST_MODE);

            String domainText = (isTest) ? domain.concat(NetworkConsts.TEST_PATH) : domain;
            isTest = false;
            editTextDomain.setText(domainText);
            editTextPort.setText(Integer.toString(port));
        }
    }

    private void testIPandPort() {
        setIconsOfAvailability(0);
        readDomainAndPort();
        Log.d("NetworkSettingsActivity", "testIPandPort() domain = [" + domain + "], port = [" + port + "], isTest = [" + isTest + "]");
        if (domain.length() != 0 && domain != null) {
            NetworkUtilities.applyDomainAndPort(this, domain, port, isTest);
            AppUtil.showInfo(this, getString(R.string.saved));
            processAsyncTask();
        }
    }

    private void readDomainAndPort() {
        parseDomain(editTextDomain.getText().toString());
        parsePort(editTextPort.getText().toString());
    }

    private void parseDomain(String domainS) {
        String[] split = domainS.split("[/]");
        Log.d(TAG, "parseDomain() split " + Arrays.toString(split));
        if (split.length > 0) domain = split[0];
        isTest = false;
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

    @Override
    protected void processAsyncTask() {
        createTaskFragment(new TestConnection(this));
    }

    private void setImageAsReachable(ImageView imageView) {
        imageView.setImageResource(R.drawable.btn_check_buttonless_on);
    }

    private void setImageAsUnreachable(ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_delete);
    }

    private void setImageAsReset(ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_lockscreen_forgotpassword_pressed);
    }


    private void setIconsOfAvailability(int code) {
        switch (code) {
            case -1:
                setImageAsUnreachable(imageWebService);
                setImageAsUnreachable(imageDatabase);
                break;
            case 0:
                setImageAsReset(imageWebService);
                setImageAsReset(imageDatabase);
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
        if (result.getStatusCode() != null) {
            code = result.getStatusCode().value();
        }
        setIconsOfAvailability(code);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString(KEY_DOMAIN, editTextDomain.getText().toString());
            outState.putString(KEY_PORT, editTextPort.getText().toString());
            outState.putInt(KEY_CODE, code);

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            editTextDomain.setText(savedInstanceState.getString(KEY_DOMAIN));
            editTextPort.setText(savedInstanceState.getString(KEY_PORT));
            code = savedInstanceState.getInt(KEY_CODE);
            setIconsOfAvailability(code);
        }
    }

}

