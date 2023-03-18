package pers.project.blog.listener;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;
import pers.project.blog.constant.RedisConst;
import pers.project.blog.security.BlogUserDetails;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.util.SecurityUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security 会话事件监听器
 *
 * @author Luo Fei
 * @version 2023/2/7
 */
@Component
public class SessionEventListener {

    /**
     * 会话销毁时移除用于会话控制的用户信息
     * <p>
     * 用户下线可能不经过注销成功处理程序，所以在会话销毁事件发生时确保移除用户信息。
     */
    @Async
    @EventListener
    public void onSessionDestroyed(SessionDestroyedEvent destroyedEvent) {
        // 获取会话所属用户的用户名
        List<String> principalNames = destroyedEvent
                .getSecurityContexts()
                .stream()
                .map(securityContext -> (BlogUserDetails) securityContext
                        .getAuthentication().getPrincipal())
                // 确保用户没有其他在线会话
                .filter(SecurityUtils::noOtherSessions)
                .map(BlogUserDetails::getUsername)
                .collect(Collectors.toList());
        // 删除下线的用户信息，用于会话控制
        if (CollectionUtils.isNotEmpty(principalNames)) {
            RedisUtils.sRem(RedisConst.ONLINE_USERNAME, principalNames.toArray());
        }
    }

}
