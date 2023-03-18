package pers.project.blog.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.RedisConst;
import pers.project.blog.dto.userauth.UserDetailsDTO;
import pers.project.blog.entity.UserAuth;
import pers.project.blog.mapper.UserAuthMapper;
import pers.project.blog.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 认证成功处理程序
 * <p>
 * 参见 {@link AuthenticationSuccessHandler} 的 Javadoc。
 *
 * @author Luo Fei
 * @version 2022/12/23
 */
@Slf4j
@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Resource
    private UserAuthMapper userAuthMapper;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (log.isDebugEnabled()) {
            Object principal = authentication.getPrincipal();
            log.debug("AuthenticationSuccessHandler Principal:\n{}", principal);
        }
        BlogUserDetails userDetails = (BlogUserDetails) authentication.getPrincipal();
        // 异步更新用户登录信息
        updateLoginInfoAsync(userDetails);
        UserDetailsDTO userDetailsDTO = BeanCopierUtils.copy
                (userDetails, UserDetailsDTO.class);
        // 响应 Spring Security 用户的部分信息
        WebUtils.renderJson(response, Result.ok(userDetailsDTO));
    }

    private void updateLoginInfoAsync(BlogUserDetails userDetails) {
        AsyncUtils.runAsync(() -> {
            UserAuth userAuth = UserAuth.builder()
                    .id(userDetails.getId())
                    .ipAddress(userDetails.getIpAddress())
                    .ipSource(userDetails.getIpSource())
                    .browser(userDetails.getBrowser())
                    .os(userDetails.getOs())
                    .lastLoginTime(userDetails.getLastLoginTime())
                    .build();
            userAuthMapper.updateById(userAuth);
            // 记录在线用户，用于会话控制
            RedisUtils.sAdd(RedisConst.ONLINE_USERNAME, userDetails.getUsername());
        });
    }

}
