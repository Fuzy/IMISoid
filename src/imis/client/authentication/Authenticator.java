package imis.client.authentication;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Authenticator extends AbstractAccountAuthenticator {
  private static final String TAG = "Authenticator";
  private static final String ACCOUNT_TYPE = Consts.ACCOUNT_TYPE;
  private static final String AUTH_TOKEN = Consts.AUTH_TOKEN;

  // Authentication Service context
  private final Context context;

  public Authenticator(Context context) {
    super(context);
    Log.d(TAG, "Authenticator()");
    this.context = context;
  }

  @Override
  public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
      String authTokenType, String[] requiredFeatures, Bundle options) {
    Log.d(TAG, "addAccount()");
    final Intent intent = new Intent(context, AuthenticatorActivity.class);
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
    final Bundle bundle = new Bundle();
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
  public Bundle editProperties(AccountAuthenticatorResponse arg0, String arg1) {
    Log.d(TAG, "editProperties()");
    return null;
  }

  
  @Override
  public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
      String authTokenType, Bundle loginOptions) throws NetworkErrorException {
    Log.d(TAG, "getAuthToken()");
    final Bundle result = new Bundle();
    result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
    result.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
    result.putString(AccountManager.KEY_AUTHTOKEN, AUTH_TOKEN);
    return result;
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
