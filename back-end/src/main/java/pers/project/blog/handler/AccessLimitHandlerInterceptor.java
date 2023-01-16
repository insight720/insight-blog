package pers.project.blog.handler;

import io.micrometer.core.lang.NonNullApi;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import pers.project.blog.annotation.AccessLimit;
import pers.project.blog.dto.Result;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.IpUtils;
import pers.project.blog.util.RedisUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流处理拦截器
 *
 * @author Luo Fei
 * @date 2023/1/14
 */
@Component
@NonNullApi
public class AccessLimitHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果请求输入控制器方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取方法中的注解，查看书否有注解
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit != null) {
                int seconds = accessLimit.seconds();
                int maxCount = accessLimit.maxCount();
                // Key 的生成规则可以自己定义 本项目需求是对每个方法都加上限流功能，如果你只是针对地址限流，那么key只需要只用ip就好
                String key = IpUtils.getIpAddress(request) + handlerMethod.getMethod().getName();
                // 从 Redis 中获取用户访问的次数
                Long count = RedisUtils.incrByAndExpire(key, 1L, seconds, TimeUnit.SECONDS);
                if (count > maxCount) {
                    render(response, Result.error(new ServiceException("请求过于频繁，请稍后再试试")));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 呈现响应结果
     *
     * @param response 响应
     * @param result   响应结果数据
     */
    @SuppressWarnings("all")
    private <T> void render(HttpServletResponse response, Result<T> result) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try (BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {
            bos.write(ConversionUtils.getJson(response).getBytes(StandardCharsets.UTF_8));
        }
    }

}
