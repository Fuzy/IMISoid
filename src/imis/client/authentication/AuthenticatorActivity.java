package imis.client.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import imis.client.R;
import imis.client.asynctasks.result.Result;
import imis.client.asynctasks.result.ResultItem;
import imis.client.model.Employee;
import imis.client.ui.activities.AsyncActivity;

public class AuthenticatorActivity extends AsyncActivity {
    private static final String TAG = AuthenticatorActivity.class.getSimpleName();

    //Fields from android.accounts.AccountAuthenticatorActivity
    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;
    //Fields from android.accounts.AccountAuthenticatorActivity ends

    private String icp = null, password = null;
    private static final String ACCOUNT_TYPE = AuthenticationConsts.ACCOUNT_TYPE;
    //    private static final String AUTHORITY1 = AuthenticationConsts.AUTHORITY1;
    /*private static final String AUTH_TOKEN = AuthenticationConsts.AUTH_TOKEN;
    public static final String PARAM_PASSWORD = "password", PARAM_USERNAME = "username",
            PARAM_AUTHTOKEN_TYPE = "authtokenType";*/
    private AccountManager accountManager;
    private TextView mMessage;
    private EditText passwordEdit, icpEdit;

    @Override
    protected void onCreate(Bundle icicle) {
        Log.d(TAG, "onCreate()");

        //onCreate() android.accounts.AccountAuthenticatorActivity
        mAccountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }
        //onCreate() android.accounts.AccountAuthenticatorActivity ends


        super.onCreate(icicle);
        accountManager = AccountManager.get(this);
        setContentView(R.layout.user_login);
        initLayoutComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        icpEdit.setText("TST");//TODO smazat
        passwordEdit.setText("TST");
    }

    private void initLayoutComponents() {
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        icpEdit = (EditText) findViewById(R.id.icp_edit);
        mMessage = (TextView) findViewById(R.id.message);
    }

    private void finishLogin() {
        Log.d(TAG, "finishLogin()");
        final Account account = new Account(icp, ACCOUNT_TYPE);
        Bundle userdata = new Bundle();
        userdata.putString(AuthenticationConsts.KEY_ICP, icp);
        accountManager.addAccountExplicitly(account, password, userdata);


        // add employee as a user
        //TODO pridat uzivatele do tabulky
        /*Employee user = new Employee(icp, username, false, false, true);
        EmployeeManager.addEmployee(this, user);*/

        // Povoli synchronizaci pro tento ucet
        /*ContentResolver.setSyncAutomatically(account, AppConsts.AUTHORITY1, true);
        ContentResolver.addPeriodicSync(account, AppConsts.AUTHORITY1, null, 60);*/

        // Now we tell our caller, could be the Android Account Manager or even our own application
        // that the process was successful
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, icp);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onTaskFinished(Result result) {
        ResultItem<Employee> employeeResult = (ResultItem<Employee>) result;
        Log.d(TAG, "onTaskFinished() employeeResult " + employeeResult);
        if (employeeResult != null) {
            Employee employee = employeeResult.getItem();
            Log.d(TAG, "onTaskFinished() employee " + employee);
        }
        boolean success = !employeeResult.isClientError();//TODO
        if (success) {
            //TODO zobrazit vysledek, dialog
            finishLogin();
        } else {
            Log.e(TAG, "onAuthenticationResult: failed to authenticate");
            mMessage.setText(getText(R.string.login_activity_loginfail_text_both));
        }
    }

    public void handleLogin(View view) {
        Log.d("AuthenticatorActivity", "handleLogin()");
        icp = icpEdit.getText().toString();
        password = passwordEdit.getText().toString();
        Log.d(TAG, "handleLogin() password " + password);
        if (TextUtils.isEmpty(icp)) {
            mMessage.setText(getText(R.string.login_activity_loginfail_text_icpmissing));
        } else {
            createTaskFragment(new AuthEmployee(this, icp, password));
        }
    }

    @Override
    protected void onPause() {
        Log.d("AuthenticatorActivity", "onPause()");
        super.onPause();
    }

    //Methods from android.accounts.AccountAuthenticatorActivity

    /**
     * Set the result that is to be sent as the result of the request that caused this
     * Activity to be launched. If result is null or this method is never called then
     * the request will be canceled.
     *
     * @param result this is returned as the result of the AbstractAccountAuthenticator request
     */
    public final void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }

    /**
     * Sends the result or a Constants.ERROR_CODE_CANCELED error if a result isn't present.
     */
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            // send the result bundle back if set, otherwise send an error.
            if (mResultBundle != null) {
                mAccountAuthenticatorResponse.onResult(mResultBundle);
            } else {
                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED,
                        "canceled");
            }
            mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }
}
