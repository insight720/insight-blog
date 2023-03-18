package pers.project.blog.aspect;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.entity.OperationLog;
import pers.project.blog.mapper.OperationLogMapper;
import pers.project.blog.security.BlogUserDetails;
import pers.project.blog.util.ConvertUtils;
import pers.project.blog.util.SecurityUtils;
import pers.project.blog.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 操作日志切面
 *
 * @author Luo Fei
 * @version 2023/1/1
 */
@Aspect
@Component
public class OperatingLogAspect {

    @Resource
    private OperationLogMapper operationLogMapper;

    @Pointcut("@annotation(pers.project.blog.annotation.OperatingLog)")
    public void operatingLog() {
    }

    @AfterReturning(pointcut = "operatingLog()", returning = "result")
    public void saveOperationLog(JoinPoint joinPoint, Object result) {
        // 获取方法签名信息
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Class<?> declaringType = methodSignature.getDeclaringType();
        Tag tag = declaringType.getAnnotation(Tag.class);
        Operation operation = method.getAnnotation(Operation.class);
        OperatingLog operatingLog = method.getAnnotation(OperatingLog.class);
        // 获取请求信息和用户信息
        HttpServletRequest request = WebUtils.getCurrentRequest();
        BlogUserDetails userDetails = SecurityUtils.getUserDetails();
        // 封装操作日志
        OperationLog operationLog = OperationLog.builder()
                .optModule(tag.name())
                .optType(operatingLog.type().getValue())
                .optUrl(request.getRequestURI())
                .optMethod(method.getName())
                .optDesc(operation.summary())
                .requestParam(ConvertUtils.getJson(joinPoint.getArgs()))
                .requestMethod(request.getMethod())
                .responseData(ConvertUtils.getJson(result))
                .userId(userDetails.getId())
                .nickname(userDetails.getNickname())
                .ipAddress(userDetails.getIpAddress())
                .ipSource(userDetails.getIpSource())
                .build();
        operationLogMapper.insert(operationLog);
    }

}
