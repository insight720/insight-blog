package pers.project.blog.handler;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.enumeration.ResultStatusEnum;
import pers.project.blog.dto.Result;
import pers.project.blog.util.ConversionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 认证异常处理程序
 *
 * @author Luo Fei
 * @date 2022/12/24
 */
@Component
public class SecurityAuthenticationExceptionHandler implements AuthenticationEntryPoint {

    // TODO: 2023/1/6 write 可以不写 JSON

    @Override
    @SuppressWarnings("deprecation")
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.getWriter().write(ConversionUtils.getJson(Result.of(ResultStatusEnum.AUTHENTICATION_REQUIRED)));
    }

}
