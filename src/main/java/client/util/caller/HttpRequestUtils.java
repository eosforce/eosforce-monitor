package client.util.caller;

import client.util.LLogger;
import client.util.LLoggerFactory;
import client.weixin.send.HttpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author lxg
 * @create 2018-06-28 18:05
 * @desc
 */
public class HttpRequestUtils {
    private final static HttpClient httpClient = HttpClientBuilder.create().build();
    private final static String CHARSET = "UTF-8";
    private final static LLogger lLogger = LLoggerFactory.getLogger(HttpRequestUtils.class);

    public static String doPost(String url, String token, long timestamp, String sign, Object o) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setHeader("Accept", "application/json");
        post.setHeader("Connection", "Keep-Alive");
        post.setHeader("Token", token);
        post.setHeader("timestamp", String.valueOf(timestamp));
        post.setHeader("Sign", sign);
        post.setHeader("Content-type", "application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(o);
        post.setEntity(new StringEntity(requestBody));
        HttpResponse result = httpClient.execute(post);
        InputStream inputStream = result.getEntity().getContent();
        String resultBody = IOUtils.toString(inputStream, CHARSET);
        lLogger.debug("responseBody: {}", resultBody);
        return resultBody;
    }
}
