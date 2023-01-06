package pers.project.blog.handler;

import com.alibaba.fastjson2.JSON;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pers.project.blog.dto.Result;
import pers.project.blog.dto.UserDetailsDTO;
import pers.project.blog.dto.UserInfoDTO;
import pers.project.blog.entity.UserAuthEntity;
import pers.project.blog.mapper.UserAuthMapper;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.SecurityUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Spring Security 认证成功处理程序
 *
 * @author Luo Fei
 * @date 2022/12/23
 */
@Component
public class SecurityAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ThreadPoolTaskExecutor executor;

    private final UserAuthMapper userAuthMapper;

    public SecurityAuthenticationSuccessHandler(ThreadPoolTaskExecutor executor, UserAuthMapper userAuthMapper) {
        this.executor = executor;
        this.userAuthMapper = userAuthMapper;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 返回用户登陆信息
        UserDetailsDTO userDetailsDTO = SecurityUtils.getUserDetails();
        UserInfoDTO userInfoDTO = ConversionUtils.convertObject(userDetailsDTO, UserInfoDTO.class);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.getWriter().write(JSON.toJSONString(Result.ok(userInfoDTO)));

        // TODO: 2022/12/24 可能不适合异步执行

        // 异步更新用户 IP 地址和最近登录时间
        CompletableFuture.runAsync(() -> {
            UserAuthEntity userAuthEntity = UserAuthEntity.builder()
                    .id(userDetailsDTO.getId())
                    .ipAddress(userDetailsDTO.getIpAddress())
                    .ipSource(userDetailsDTO.getIpSource())
                    .lastLoginTime(userDetailsDTO.getLastLoginTime())
                    .build();
            userAuthMapper.updateById(userAuthEntity);
        }, executor);
    }

}
