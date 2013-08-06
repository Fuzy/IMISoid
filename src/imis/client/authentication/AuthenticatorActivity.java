package imis.client.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import imis.client.AppConsts;
import imis.client.R;
import imis.client.asynctasks.result.Result;
import imis.client.asynctasks.result.ResultItem;
import imis.client.model.Employee;
import imis.client.ui.activities.AsyncActivity;
import imis.client.ui.dialogs.AuthConfirmDialog;
import imis.client.ui.dialogs.SetNetworkDialog;

public class AuthenticatorActivity extends AsyncActivity implements AuthConfirmDialog.AuthConfirmDialogListener {
    private static final String TAG = AuthenticatorActivity.class.getSimpleName();

    //Fields from android.accounts.AccountAuthenticatorActivity
    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;
    //Fields from android.accounts.AccountAuthenticatorActivity ends

    private String icp = null, password = null;
    private static final String ACCOUNT_TYPE = AuthenticationConsts.ACCOUNT_TYPE;
    private AccountManager accountManager;
    private TextView mMessage;
    private EditText passwordEdit, icpEdit;
    private Employee employee = null;
    private static final String KEY_ICP = "key_icp", KEY_PASSWORD = "key_password";

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

    private void initLayoutComponents() {
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        icpEdit = (EditText) findViewById(R.id.icp_edit);
        mMessage = (TextView) findViewById(R.id.message);
    }

    private void finishLogin() {
        Log.d(TAG, "finishLogin()");
        final Account account = new Account(icp, ACCOUNT_TYPE);/*
        Bundle userdata = new Bundle();
        userdata.putString(AuthenticationConsts.KEY_ICP, icp);*/
        accountManager.addAccountExplicitly(account, password, null);

        ContentResolver.setIsSyncable(account, AppConsts.AUTHORITY1, 1);
        ContentResolver.setSyncAutomatically(account, AppConsts.AUTHORITY1, true);
        ContentResolver.setIsSyncable(account, AppConsts.AUTHORITY2, 1);
        ContentResolver.setSyncAutomatically(account, AppConsts.AUTHORITY2, true);
        ContentResolver.setIsSyncable(account, AppConsts.AUTHORITY3, 1);
        ContentResolver.setSyncAutomatically(account, AppConsts.AUTHORITY3, true);

        /*int inserted = EmployeeManager.addEmployee(this, employee);
        Log.d(TAG, "finishLogin() inserted " + inserted);*/

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
        @SuppressWarnings("unchecked")
        ResultItem<Employee> employeeResult = (ResultItem<Employee>) result;
        Log.d(TAG, "onTaskFinished() employeeResult " + employeeResult);

        if (employeeResult != null) {
            employee = employeeResult.getItem();
            Log.d(TAG, "onTaskFinished() employee " + employee);
        }

        if (employeeResult.isUnknownErr()) {
            Log.d(TAG, "onTaskFinished() isUnknownErr");
            mMessage.setText(getText(R.string.unknown_error));
            new SetNetworkDialog().show(getSupportFragmentManager(), "SetNetworkDialog");
        } else if (employeeResult.isServerError()) {
            Log.d(TAG, "onTaskFinished() isServerError");
            mMessage.setText(getText(R.string.server_error));
        } else if (employeeResult.isClientError()) {
            Log.d(TAG, "onTaskFinished() isClientError");
            mMessage.setText(getText(R.string.login_activity_loginfail_text_both));
        } else if (!employeeResult.isEmpty()) {
            Log.d(TAG, "onTaskFinished() not empty");
            showConfirmDialog();
        } else {
            mMessage.setText(getText(R.string.unknown_error));
        }

    }

    private void showConfirmDialog() {
        String message = employee.getName() + " (" + employee.getKodpra() + ")";
        DialogFragment deleteEventDialog = new AuthConfirmDialog();
        Bundle bundle = new Bundle();
        bundle.putString(AppConsts.KEY_TITLE, getString(R.string.auth_success));
        bundle.putString(AppConsts.KEY_MSG, message);
        deleteEventDialog.setArguments(bundle);
        deleteEventDialog.show(getSupportFragmentManager(), "AddEventDialog");
    }

    public void handleLogin(View view) {
        Log.d("AuthenticatorActivity", "handleLogin()");
        icp = icpEdit.getText().toString();
        password = passwordEdit.getText().toString();
        if (TextUtils.isEmpty(icp)) {
            mMessage.setText(getText(R.string.login_activity_loginfail_text_icpmissing));
        } else {
            processAsyncTask();
        }
    }

    @Override
    protected void processAsyncTask() {
        createTaskFragment(new AuthEmployee(this, icp, password));
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

    @Override
    public void onConfirmClickPositiveClick() {
        finishLogin();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            icpEdit.setText(savedInstanceState.getString(KEY_ICP));
            passwordEdit.setText(savedInstanceState.getString(KEY_PASSWORD));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString(KEY_ICP, icpEdit.getText().toString());
            outState.putString(KEY_PASSWORD, passwordEdit.getText().toString());
        }
    }
}
