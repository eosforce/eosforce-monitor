package client.interceptor;

import client.util.LLogger;
import client.util.LLoggerFactory;
import client.util.Utils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Created by lxg on 2017/7/18.
 */
@ControllerAdvice
public class WebResponseAdvice  implements ResponseBodyAdvice<Object> {
    private final LLogger filterLogger = LLoggerFactory.getLogger("filterLogger");
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        filterLogger.info("responseBody: {}", Utils.toJsonByGson(body));
        return body;
    }
}
