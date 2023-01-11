package pers.project.blog.annotation.aspect;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.dto.UserDetailsDTO;
import pers.project.blog.entity.OperationLogEntity;
import pers.project.blog.mapper.OperationLogMapper;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.IpUtils;
import pers.project.blog.util.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * {@link OperationLog} 注解功能的切面实现类
 *
 * @author Luo Fei
 * @date 2023/1/1
 */
@Aspect
@Component
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;

    public OperationLogAspect(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    /**
     * 切入点
     */
    @Pointcut("@annotation(pers.project.blog.annotation.OperationLog)")
    public void pointCut() {
    }

    /**
     * 正常返回通知
     *
     * @param joinPoint 切入点
     * @param result    返回结果
     */
    @AfterReturning(pointcut = "pointCut()", returning = "result")
    public void saveOperationLog(JoinPoint joinPoint, Object result) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = (HttpServletRequest) Objects.requireNonNull(requestAttributes)
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Class<?> declaringType = methodSignature.getDeclaringType();
        Tag tag = declaringType.getAnnotation(Tag.class);
        Operation operation = method.getAnnotation(Operation.class);
        OperationLog operationLog = method.getAnnotation(OperationLog.class);

        UserDetailsDTO userDetailsDTO = SecurityUtils.getUserDetails();

        // TODO: 2023/1/1 IP 内容可以通过 userDetailsDTO 获取
        String ipAddress = IpUtils.getIpAddress(httpServletRequest);
        String ipSource = IpUtils.getIpSource(ipAddress);

        OperationLogEntity operationLogEntity = OperationLogEntity.builder()
                .optModule(tag.name())
                .optType(operationLog.type())
                .optUrl(Objects.requireNonNull(httpServletRequest).getRequestURI())
                .optMethod(method.getName())
                .optDesc(operation.summary())
                .requestParam(ConversionUtils.getJson(joinPoint.getArgs()))
                .requestMethod(httpServletRequest.getMethod())
                .responseData(ConversionUtils.getJson(result))
                .userId(userDetailsDTO.getId())
                .nickname(userDetailsDTO.getNickname())
                .ipAddress(ipAddress)
                .ipSource(ipSource)
                .build();
        operationLogMapper.insert(operationLogEntity);
    }

}
