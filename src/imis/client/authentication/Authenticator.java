package imis.client.authentication;

import android.accounts.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class Authenticator extends AbstractAccountAuthenticator {
  private static final String TAG = "Authenticator";
  private static final String ACCOUNT_TYPE = AuthenticationConsts.ACCOUNT_TYPE;
  private static final String AUTH_TOKEN = AuthenticationConsts.AUTH_TOKEN;

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
      // If the caller requested an authToken type we don't support, then
      // return an error
      if (!authTokenType.equals(AuthenticationConsts.AUTHTOKEN_TYPE)) {
          final Bundle result = new Bundle();
          result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
          return result;
      }

      // Extract the username and password from the Account Manager, and ask
      // the server for an appropriate AuthToken.
      final AccountManager am = AccountManager.get(context);
      final String password = am.getPassword(account);
      if (password != null) {
          final String authToken = "blabla";//NetworkUtilities.authenticate(account.name, password);
          if (!TextUtils.isEmpty(authToken)) {
              final Bundle result = new Bundle();
              result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
              result.putString(AccountManager.KEY_ACCOUNT_TYPE, AuthenticationConsts.ACCOUNT_TYPE);
              result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
              return result;
          }
      }

      // If we get here, then we couldn't access the user's password - so we
      // need to re-prompt them for their credentials. We do that by creating
      // an intent to display our AuthenticatorActivity panel.
      final Intent intent = new Intent(context, AuthenticatorActivity.class);
      intent.putExtra(AuthenticatorActivity.PARAM_USERNAME, account.name);
      intent.putExtra(AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
      intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
      final Bundle bundle = new Bundle();
      bundle.putParcelable(AccountManager.KEY_INTENT, intent);
      return bundle;
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
