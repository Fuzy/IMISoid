package imis.client.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import imis.client.R;
import imis.client.network.NetworkUtilities;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    private static final String TAG = "AuthenticatorActivity";
    private String username = null;
    private String password = null;
    private static final String ACCOUNT_TYPE = Consts.ACCOUNT_TYPE;
    private static final String AUTHORITY = Consts.AUTHORITY;
    private static final String AUTH_TOKEN = Consts.AUTH_TOKEN;
    /**
     * The Intent extras.
     */
    public static final String PARAM_PASSWORD = "password", PARAM_USERNAME = "username",
            PARAM_AUTHTOKEN_TYPE = "authtokenType";
    private AccountManager accountManager;
    /**
     * Keep track of the login task so can cancel it if requested
     */
    private UserLoginTask authTask = null;
    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password or authToken to be changed on the
     * device.
     */

    private Boolean mConfirmCredentials = false;//TODO k cemu?
    /**
     * Was the original caller asking for an entirely new account?
     */
    protected boolean requestNewAccount = false;//TODO k cemu?
    private ProgressDialog progressDialog = null;
    private TextView mMessage;
    private EditText passwordEdit, usernameEdit;

    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(TAG, "onCreate()");
        super.onCreate(icicle);
        // ziska account manager
        accountManager = AccountManager.get(this);

        requestNewAccount = (username == null) ? true : false;


        //finishLogin(AUTH_TOKEN);
        setContentView(R.layout.user_login);
        initLayoutComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        usernameEdit.setText("1520");
        passwordEdit.setText("1520");
    }

    private void initLayoutComponents() {
        usernameEdit = (EditText) findViewById(R.id.username_edit);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        mMessage = (TextView) findViewById(R.id.message);
    }

    private void finishLogin(String authToken) {
        Log.d(TAG, "finishLogin()");
        final Account account = new Account(username, ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, password, null);

        // Povoli synchronizaci pro tento ucet
        ContentResolver.setSyncAutomatically(account, AUTHORITY, true);

        // Now we tell our caller, could be the Android Account Manager or even our own application
        // that the process was successful
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param authToken the authentication token returned by the server, or NULL if
     *                  authentication failed.
     */
    public void onAuthenticationResult(String authToken) {
        Log.d("AuthenticatorActivity", "onAuthenticationResult()");
        boolean success = ((authToken != null) && (authToken.length() > 0));
        Log.i(TAG, "onAuthenticationResult(" + success + ")");

        // Our task is complete, so clear it out
        authTask = null;

        // Hide the progress dialog
        hideProgress();

        if (success) {
            if (!mConfirmCredentials) {
                finishLogin(authToken);
            } else {
                finishConfirmCredentials(success);
            }
        } else {
            Log.e(TAG, "onAuthenticationResult: failed to authenticate");
            if (requestNewAccount) {
                // "Please enter a valid username/password.
                mMessage.setText(getText(R.string.login_activity_loginfail_text_both));
            } else {
                // "Please enter a valid password." (Used when the
                // account is already in the database but the password
                // doesn't work.)
                mMessage.setText(getText(R.string.login_activity_loginfail_text_pwonly));
            }
        }
    }

    /**
     * Pouzije se pokud je treba zadat pouze heslo.
     *
     * @param result the confirmCredentials result.
     */
    private void finishConfirmCredentials(boolean result) {
        Log.i(TAG, "finishConfirmCredentials()");
        final Account account = new Account(username, Consts.ACCOUNT_TYPE);
        accountManager.setPassword(account, password);
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onAuthenticationCancel() {
        Log.i(TAG, "onAuthenticationCancel()");
        authTask = null;
        hideProgress();
    }

    private void hideProgress() {
        Log.d("AuthenticatorActivity", "hideProgress()");
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication. The button is configured to call
     * handleLogin() in the layout XML.
     *
     * @param view The Submit button for which this method is invoked
     */
    public void handleLogin(View view) {
        Log.d("AuthenticatorActivity", "handleLogin()");
        if (requestNewAccount) {
            username = usernameEdit.getText().toString();
        }
        password = passwordEdit.getText().toString();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            mMessage.setText(getMessage());
        } else {
            // Show a progress dialog, and kick off a background task to perform
            // the user login attempt.
            showProgress();
            authTask = new UserLoginTask();
            authTask.execute();
        }
    }

    /**
     * Vraci chybovou zpravu v pripade ze chybi jmeno ci heslo.
     */
    private CharSequence getMessage() {
        Log.d("AuthenticatorActivity", "getMessage()");
        if (TextUtils.isEmpty(username)) {
            return getText(R.string.login_activity_loginfail_text_usmissing);
        }
        if (TextUtils.isEmpty(password)) {
            return getText(R.string.login_activity_loginfail_text_pwmissing);
        }
        return null;
    }

    /**
     * Shows the progress UI for a lengthy operation.
     */
    private void showProgress() {
        Log.d("AuthenticatorActivity", "showProgress()");
        progressDialog = ProgressDialog.show(this, "", getText(R.string.ui_activity_authenticating),
                true, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (authTask != null) {
                    authTask.cancel(true);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        Log.d("AuthenticatorActivity", "onPause()");
        super.onPause();
        hideProgress();
    }

    /**
     * Represents an asynchronous task used to authenticate a user against the
     * SampleSync Service
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            Log.d("AuthenticatorActivity$UserLoginTask", "doInBackground()");
            // We do the actual work of authenticating the user
            // in the NetworkUtilities class.
            try {
                return NetworkUtilities.authenticate(username, password);
            } catch (Exception ex) {
                Log.e(TAG, "UserLoginTask.doInBackground: failed to authenticate");
                Log.i(TAG, ex.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String authToken) {
            Log.d("AuthenticatorActivity$UserLoginTask", "onPostExecute()");
            // Uspesna autorizace vraci autorizacni token.
            onAuthenticationResult(authToken);
        }

        @Override
        protected void onCancelled() {
            Log.d("AuthenticatorActivity$UserLoginTask", "onCancelled()");
            // If the action was canceled (by the user clicking the cancel
            // button in the progress dialog), then call back into the
            // activity to let it know.
            onAuthenticationCancel();
        }
    }


}
