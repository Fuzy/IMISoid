package imis.client;

import android.content.Context;
import imis.client.authentication.AuthenticationUtil;
import imis.client.network.HttpClientFactory;
import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 31.7.13
 * Time: 11:58
 */
public class RestUtil {
    private static final String TAG = RestUtil.class.getSimpleName();

    public static RestTemplate prepareRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientFactory.getThreadSafeClient()));
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
        return restTemplate;
    }

    public static MultiValueMap<String,String> prepareHttpHeaders(Context context) {
        HttpHeaders requestHeaders = new HttpHeaders();
        HttpAuthentication authHeader = AuthenticationUtil.createAuthHeader(context);
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        requestHeaders.setContentEncoding(ContentCodingType.valueOf("UTF-8"));
        return requestHeaders;
    }
}
