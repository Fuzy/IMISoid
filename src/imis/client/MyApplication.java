package imis.client;

import android.app.Application;
import android.util.Log;
import com.appkilt.client.AppKilt;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 13.7.13
 * Time: 14:22
 */
public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        AppKilt.init(this, "73850731-0c9e-4383-8725-0aa14e81e41f");
    }
}
