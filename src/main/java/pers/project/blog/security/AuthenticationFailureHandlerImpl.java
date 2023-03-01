package pers.project.blog.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import pers.project.blog.util.Result;
import pers.project.blog.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static pers.project.blog.enums.ResultEnum.*;

/**
 * Spring Security 认证失败处理程序
 * <p>
 * 参见 {@link AuthenticationFailureHandler} 的 Javadoc。
 *
 * @author Luo Fei
 * @date 2022/12/23
 */
@Slf4j
@Component
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        if (log.isDebugEnabled()) {
            String message = exception.getMessage();
            log.debug("AuthenticationFailureHandler Message: {}", message);
        }
        if (exception instanceof DisabledException) {
            // 用户被禁用
            WebUtils.renderJson(response, Result.of(AUTHENTICATION_DISABLED));
        } else if (exception instanceof LockedException) {
            // 用户无可用权限角色
            WebUtils.renderJson(response, Result.of(AUTHENTICATION_LOCKED));
        } else {
            // BadCredentialsException
            // 用户名或密码错误
            WebUtils.renderJson(response, Result.of(AUTHENTICATION_FAILURE));
        }
    }

}