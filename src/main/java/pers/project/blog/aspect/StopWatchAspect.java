package pers.project.blog.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import pers.project.blog.util.WebUtils;

import javax.servlet.http.HttpServletRequest;


/**
 * 秒表切面
 * <p>
 * 用于计算程序耗时。
 *
 * @author Luo Fei
 * @version 2023/1/29
 */
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "logging.level", name = "pers.project.blog", havingValue = "debug")
public class StopWatchAspect {

    @Pointcut("execution(* pers.project.blog.controller.*.*(..))")
    public void controller() {
    }

    @Pointcut("execution(* pers.project.blog.security.*.*(..))")
    public void security() {
    }

    @Around("controller()")
    public Object timingApi(ProceedingJoinPoint joinPoint) throws Throwable {
        // 计算接口调用耗时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        long timeMillis = stopWatch.getLastTaskTimeMillis();
        // 获取请求信息
        HttpServletRequest request = WebUtils.getCurrentRequest();
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        // 打印耗时记录日志
        String shortSignature = joinPoint.getSignature().toShortString();
        String content = String.format("%s %s 接口调用耗时: %d ms 签名: %s",
                requestMethod, requestURI, timeMillis, shortSignature);
        log.debug(content);
        return result;
    }

    @Around("security()")
    public Object timingSecurity(ProceedingJoinPoint joinPoint) throws Throwable {
        // 计算方法调用耗时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        long timeMillis = stopWatch.getLastTaskTimeMillis();
        // 打印耗时记录日志
        String shortSignature = joinPoint.getSignature().toShortString();
        String content = String.format("Security 方法调用耗时: %d ms 签名: %s",
                timeMillis, shortSignature);
        log.debug(content);
        return result;
    }

}
