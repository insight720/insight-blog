package pers.project.blog.handler;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import pers.project.blog.util.PageUtils;
import pers.project.blog.util.StrRegexUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static pers.project.blog.constant.PageConst.*;

/**
 * 分页拦截器
 *
 * @author Luo Fei
 * @version 2022/12/31
 */
@Component
@SuppressWarnings("all")
public class PageHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        String current = request.getParameter(CURRENT);
        if (StrRegexUtils.isNotBlank(current)) {
            String size = Optional.ofNullable
                    (request.getParameter(SIZE)).orElse(DEFAULT_SIZE);
            PageUtils.setPage(Page.of(Long.parseLong(current), Long.parseLong(size)));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        PageUtils.removePage();
    }

}
