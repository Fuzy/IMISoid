package imis.client.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AuthenticationService extends Service {
  private static final String TAG = AuthenticationService.class.getSimpleName();
  private Authenticator authenticator;

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "onCreate()");
    authenticator = new Authenticator(this);
  }

  @Override
  public IBinder onBind(Intent intent) {
    Log.d(TAG, "onBind()" + intent);
    return authenticator.getIBinder();
  }

}
