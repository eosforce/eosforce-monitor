package client.weixin.send;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author lxg
 * @create 2018-06-24 15:14
 * @desc
 */
public class HttpRequest {
    private static final String CHARSET = "UTF-8";
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);
    private static final String defaultResult = "{}";

    /**
     * 执行JSON—RPC 调用,并返回未解析的原始字符串,用于支持新接口,和排查非标准的 JSON-RPC 接口
     *
     * @param rpcRequest JSON-RPC 原始请求
     * @return 返回整个 JSON－RPC 结果的 json 原始字符串
     * @throws java.io.IOException 网络异常等
     */
    public static String executeJson(BasicRequest rpcRequest) throws IOException {
        if (rpcRequest.getType() == MethodTypeEnum.get) {
            return doGet(rpcRequest);
        } else if (rpcRequest.getType() == MethodTypeEnum.post) {
            return doPost(rpcRequest);
        } else {
            return defaultResult;
        }
    }

    public static String doPost(BasicRequest rpcRequest) throws IOException {
        RpcConfig config = new RpcConfig();
        HttpPost post = new HttpPost(rpcRequest.getUrl());
        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(rpcRequest.getParams());
        StringEntity content = new StringEntity(requestBody, Charset.forName("utf-8"));
        content.setContentType("application/json; charset=UTF-8");
        content.setContentEncoding("utf-8");
        post.setEntity(content);
        HttpResponse result = config.getHttpClient().execute(post);
        InputStream inputStream = result.getEntity().getContent();
        String resultBody = IOUtils.toString(inputStream, CHARSET);
        logger.debug("response: {}", resultBody);
        return resultBody;
    }

    public static String doGet(BasicRequest rpcRequest) {
        String url = rpcRequest.getUrl();
        RpcConfig config = new RpcConfig();
        HttpGet get = new HttpGet(url);
        String resultBody = null;
        try {
            HttpResponse result = config.getHttpClient().execute(get);
            InputStream inputStream = result.getEntity().getContent();
            resultBody = IOUtils.toString(inputStream, CHARSET);
            logger.debug("url: {}, response: {}", url, resultBody);
        } catch (IOException e) {
            logger.error("url: {}", e);
        }
        return resultBody;
    }
}
