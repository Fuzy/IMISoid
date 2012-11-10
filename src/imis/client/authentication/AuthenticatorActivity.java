package imis.client.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {
  private static final String TAG = "AuthenticatorActivity";
  private final String username = "1520";// nevyuzito
  private final String password = "";// nevyuzito
  private static final String ACCOUNT_TYPE = Consts.ACCOUNT_TYPE;
  private static final String AUTHORITY = Consts.AUTHORITY;
  private static final String AUTH_TOKEN = Consts.AUTH_TOKEN;
  private AccountManager accountManager;

  @Override
  protected void onCreate(Bundle icicle) {
    Log.d(TAG, "onCreate()");
    super.onCreate(icicle);
    // ziska account manager
    accountManager = AccountManager.get(this);

    // provede zjednoduseny login
    finishLogin(AUTH_TOKEN);
  }

  private void finishLogin(String authToken) {
    Log.d(TAG, "finishLogin()");
    final Account account = new Account(username, ACCOUNT_TYPE);//TODO zmenit account name

    // vytvori novy ucet, na heslu nezalezi
    accountManager.addAccountExplicitly(account, password, null);

    // Povoli synchronizaci pro tento ucet
    ContentResolver.setSyncAutomatically(account, AUTHORITY, true);

    final Intent intent = new Intent();
    intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
    setAccountAuthenticatorResult(intent.getExtras());
    setResult(RESULT_OK, intent);
    finish();
  }
}
