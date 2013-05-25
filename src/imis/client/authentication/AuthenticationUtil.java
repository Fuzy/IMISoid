package imis.client.authentication;

import android.content.Context;
import android.util.Log;
import imis.client.AppUtil;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 25.5.13
 * Time: 14:37
 */
public class AuthenticationUtil {
    private static final String TAG = AuthenticationUtil.class.getSimpleName();

    public static HttpAuthentication createAuthHeader(Context context) {
        String username, password;
        try {
            username = AppUtil.getUserUsername(context);
            password = AppUtil.getUserPassword(context);
        } catch (Exception e) {
            e.printStackTrace();
            return null;//TODO err msg
        }
        Log.d(TAG, "doInBackground() username " + username);
        Log.d(TAG, "doInBackground() password " + password);

        return new HttpBasicAuthentication(username, password);
    }
}
