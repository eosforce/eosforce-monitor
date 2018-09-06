package client.interceptor;

import client.util.LLogger;
import client.util.LLoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lxg on 2017/4/13.
 */
public class WebHandlerInterceptor extends HandlerInterceptorAdapter {
    private static final LLogger logger = LLoggerFactory.getLogger(WebHandlerInterceptor.class);
    public long timeInterval;

    public WebHandlerInterceptor(int timeout) {
        timeInterval =  timeout;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        logger.info("entry interceptor...");
//        if (handler instanceof HandlerMethod) {
//            HandlerMethod method = (HandlerMethod) handler;
//            if (method.getBeanType() == BasicErrorController.class) {
//                throw new HandlerArgumentException(Code.PARAM_ERROR, "请求路径错误");
//            }
//
//            Token token = method.getMethodAnnotation(Token.class);
//            String authorization = request.getHeader(Constants.AUTHORIZATION);
//            if (null != token) {
//                if (Utils.isEmpty(authorization)) {
//                    throw new HandlerArgumentException(Code.LOGIN_NOT, "请登录");
//                }
//
//                logger.info("token : {}", authorization);
//                long loginTime = AuthUtil.getLoginTime(authorization);
//                long timeDiff = System.currentTimeMillis() - loginTime;
//                if (timeDiff > timeInterval) {
//                    logger.error("timeout is {}ms", timeDiff);
//                    throw new HandlerArgumentException(Code.LOGIN_NOT, "token失效，请重新登录!");
//                }
//                String userId = AuthUtil.getUserId(authorization);
//                ThreadLocalUtils.setUserId(userId);
//            }else if(Utils.isNotEmpty(authorization)){
//                String userId = AuthUtil.getUserId(authorization);
//                ThreadLocalUtils.setUserId(userId);
//            }
//        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView views) throws Exception {
    }

}
