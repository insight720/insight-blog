package pers.project.blog.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import pers.project.blog.annotation.AccessLimit;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.util.Result;
import pers.project.blog.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static pers.project.blog.constant.GenericConst.ONE_L;
import static pers.project.blog.enums.ResultEnum.REQUEST_BLOCKED;

/**
 * 接口限流处理拦截器
 *
 * @author Luo Fei
 * @date 2023/1/14
 */
@Component
public class AccessLimitHandlerInterceptor implements HandlerInterceptor {

    @Override
    @SuppressWarnings("all")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws IOException {
        // 如果请求输入控制器方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取方法中的注解，查看是否有注解
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit != null) {
                int seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                // key 的生成规则可以自己定义 本项目需求是对每个方法都加上限流功能，如果你只是针对地址限流，那么 key 只需要用 IP 就好
                String key = WebUtils.getIpAddress(request) + handlerMethod.getMethod().getName();
                // 从 Redis 中获取用户访问的次数
                Long count = RedisUtils.incr(key);
                if (count.equals(ONE_L)) {
                    RedisUtils.expire(key, seconds, TimeUnit.SECONDS);
                }
                if (count > maxCount) {
                    WebUtils.renderJson(response, Result.of(REQUEST_BLOCKED));
                    return false;
                }
            }
        }
        return true;
    }

}
