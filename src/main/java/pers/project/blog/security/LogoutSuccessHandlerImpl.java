package pers.project.blog.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import pers.project.blog.util.Result;
import pers.project.blog.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 注销成功处理程序
 * <p>
 * 参见 <a href="https://springdoc.cn/spring-security/servlet/authentication/logout.html#jc-logout-success-handler">
 * LogoutSuccessHandler</a> 的文档。
 *
 * @author Luo Fei
 * @date 2022/12/24
 */
@Slf4j
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {
        if (log.isDebugEnabled() && authentication != null) {
            Object principal = authentication.getPrincipal();
            log.debug("LogoutSuccessHandler Principal: {}", principal);
        }
        WebUtils.renderJson(response, Result.ok());
    }

}
