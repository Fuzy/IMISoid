package imis.client.authentication;

import android.content.Context;
import android.util.Log;
import imis.client.AccountUtil;
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
            username = AccountUtil.getUserUsername(context);
            password = AccountUtil.getUserPassword(context);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
        Log.d(TAG, "createAuthHeader() username " + username);
        Log.d(TAG, "createAuthHeader() password " + password);

        return new HttpBasicAuthentication(username, password);
    }
}
