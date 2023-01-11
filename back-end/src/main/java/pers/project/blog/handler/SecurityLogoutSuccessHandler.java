package pers.project.blog.handler;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import pers.project.blog.dto.Result;
import pers.project.blog.util.ConversionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 注销成功处理程序
 *
 * @author Luo Fei
 * @date 2022/12/24
 */
@Component
public class SecurityLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    @SuppressWarnings("deprecation")
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.getWriter().write(ConversionUtils.getJson(Result.ok()));
    }

}
