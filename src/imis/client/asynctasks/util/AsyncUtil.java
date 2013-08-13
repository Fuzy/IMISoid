package imis.client.asynctasks.util;

import android.content.Context;
import android.util.Log;
import imis.client.R;
import imis.client.asynctasks.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

/**
 *  Stores utility methods for processing of async request to server.
 */
public class AsyncUtil {
    private static final String TAG = AsyncUtil.class.getSimpleName();

    public static <T extends Result> T processException(Context context, Exception e, Class<T> type) {
        T instance = null;
        try {
            if (e instanceof HttpClientErrorException) {
                HttpClientErrorException ex = (HttpClientErrorException) e;
                instance = type.getDeclaredConstructor(HttpStatus.class, String.class)
                        .newInstance(ex.getStatusCode(), ex.getResponseBodyAsString());
            } else if (e instanceof HttpServerErrorException) {
                HttpServerErrorException ex = (HttpServerErrorException) e;
                instance = type.getDeclaredConstructor(HttpStatus.class, String.class)
                        .newInstance(ex.getStatusCode(), ex.getResponseBodyAsString());
            } else {
                instance = type.getDeclaredConstructor(String.class).newInstance(context.getString(R.string.connection_unavailable));
            }
        } catch (Exception e1) {
            Log.e(TAG, e.getMessage(), e);
        }
        return instance;
    }
}
