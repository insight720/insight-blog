package pers.project.blog.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pers.project.blog.enums.ResultEnum;
import pers.project.blog.util.Result;
import pers.project.blog.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 身份验证入口点
 * <p>
 * 如果用户没有被认证，或者应用程序抛出 AuthenticationException，那么进入
 * 此类启动身份验证方案。
 * <p>
 * 参见 <a href="https://springdoc.cn/spring-security/servlet/authentication/architecture.html#servlet-authentication-authenticationentrypoint">
 * AuthenticationEntryPoint</a> 文档。
 *
 * @author Luo Fei
 * @date 2022/12/24
 */
@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        if (log.isDebugEnabled()) {
            String requestURI = request.getRequestURI();
            log.debug("AuthenticationEntryPoint RequestURI: {}", requestURI);
        }
        WebUtils.renderJson(response, Result.of(ResultEnum.AUTHENTICATION_REQUIRED));
    }

}
