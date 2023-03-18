package pers.project.blog.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import pers.project.blog.enums.ResultEnum;
import pers.project.blog.util.Result;
import pers.project.blog.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 授权异常处理程序
 * <p>
 * 如果用户已认证但未被授权，过滤器将应用程序抛出的
 * AccessDeniedException 委托给此类处理。
 * <p>
 * 参见 {@link AccessDeniedHandler} 的 Javadoc。
 *
 * @author Luo Fei
 * @version 2022/12/24
 */
@Slf4j
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        if (log.isDebugEnabled()) {
            String requestURI = request.getRequestURI();
            log.debug("AccessDeniedHandler RequestURI: {}", requestURI);
        }
        WebUtils.renderJson(response, Result.of(ResultEnum.ACCESS_DENIED));
    }

}
