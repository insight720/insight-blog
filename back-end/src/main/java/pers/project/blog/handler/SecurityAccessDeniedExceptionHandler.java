package pers.project.blog.handler;

import com.alibaba.fastjson2.JSON;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.enumeration.ResultStatusEnum;
import pers.project.blog.dto.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 授权异常处理程序
 *
 * @author Luo Fei
 * @date 2022/12/24
 */
@Component
public class SecurityAccessDeniedExceptionHandler implements AccessDeniedHandler {

    @Override
    @SuppressWarnings("deprecation")
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.getWriter().write(JSON.toJSONString(Result.of(ResultStatusEnum.ACCESS_DENIED, accessDeniedException)));
    }

}
