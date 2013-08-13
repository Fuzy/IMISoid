package imis.client.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Service for authentication.
 */
public class AuthenticationService extends Service {
  private static final String TAG = AuthenticationService.class.getSimpleName();
  private AccountAuthenticator accountAuthenticator;

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "onCreate()");
    accountAuthenticator = new AccountAuthenticator(this);
  }

  @Override
  public IBinder onBind(Intent intent) {
    Log.d(TAG, "onBind()" + intent);
    return accountAuthenticator.getIBinder();
  }

}
