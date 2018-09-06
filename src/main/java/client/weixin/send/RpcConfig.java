package client.weixin.send;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

/**
 * 服务器配置。
 */
@Component
public class RpcConfig {

    private HttpClient httpClient = HttpClientBuilder.create().build();
    /**
     * @param httpClient
     */
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * @return
     */
    public HttpClient getHttpClient() {
        return httpClient;
    }

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
