package imis.client.authentication;

import android.accounts.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import imis.client.R;

/**
 * Class serves obtaining authentication information for a connection.
 */
public class AccountAuthenticator extends AbstractAccountAuthenticator {
    private static final String TAG = AccountAuthenticator.class.getSimpleName();

    // Authentication Service context
    private final Context context;

    public AccountAuthenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                             String authTokenType, String[] requiredFeatures, Bundle options) {
        Log.d(TAG, "addAccount()");

        // Don't create account if already exists
        AccountManager accountManager = AccountManager.get(context);
        Account[] accountsByType = accountManager.getAccountsByType(accountType);
        final Bundle bundle = new Bundle();
        if (accountsByType.length > 0) {
            Log.d(TAG, "addAccount() " + context.getString(R.string.account_allready_exists));
            bundle.putString(AccountManager.KEY_ERROR_MESSAGE, context.getString(R.string.account_allready_exists));
            return bundle;
        }

        final Intent intent = new Intent(context, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse arg0, Account arg1, Bundle arg2)
            throws NetworkErrorException {
        Log.d(TAG, "confirmCredentials()");
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse,
                               Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse arg0, String arg1) {
        Log.d(TAG, "editProperties()");
        return null;
    }

    @Override
    public String getAuthTokenLabel(String arg0) {
        Log.d(TAG, "getAuthTokenLabel()");
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse arg0, Account arg1, String[] arg2)
            throws NetworkErrorException {
        Log.d(TAG, "hasFeatures()");
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse arg0, Account arg1, String arg2,
                                    Bundle arg3) throws NetworkErrorException {
        Log.d(TAG, "updateCredentials()");
        return null;
    }

}
