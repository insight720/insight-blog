package pers.project.blog.handler;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import pers.project.blog.constant.PaginationConstant;
import pers.project.blog.util.PaginationUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 分页拦截器
 *
 * @author Luo Fei
 * @date 2022/12/31
 */
@Component
public class PaginationHandlerInterceptor implements HandlerInterceptor {

    // TODO: 2022/12/31 可以用控制器直接接受请求参数，不一定要用拦截器

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String current = request.getParameter(PaginationConstant.CURRENT);
        String size = Optional.ofNullable(request.getParameter(PaginationConstant.SIZE))
                .orElse(PaginationConstant.DEFAULT_SIZE);
        if (StringUtils.hasText(current)) {
            PaginationUtils.setPage(Page.of(Long.parseLong(current), Long.parseLong(size)));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        PaginationUtils.removePage();
    }

}
