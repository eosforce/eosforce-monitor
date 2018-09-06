package client.interceptor;

/**
 * Created by lxg on 2017/3/29.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class IntercetorContext extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new WebHandlerInterceptor(5)).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
