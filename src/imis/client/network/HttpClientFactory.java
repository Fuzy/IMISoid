package imis.client.network;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpClientFactory {
    private static final int TIMEOUT = 20 * 1000;

    public synchronized static DefaultHttpClient getThreadSafeClient() {

        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager mgr = client.getConnectionManager();
        HttpParams params = client.getParams();
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT);
        ConnManagerParams.setTimeout(params, TIMEOUT);

        client = new DefaultHttpClient(
                new ThreadSafeClientConnManager(params,
                        mgr.getSchemeRegistry()), params);

        return client;
    }
}
