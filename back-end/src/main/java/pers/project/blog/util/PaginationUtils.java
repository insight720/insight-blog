package pers.project.blog.util;

import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 分页工具类
 *
 * @author Luo Fei
 * @date 2022/12/31
 */
public abstract class PaginationUtils {

    // TODO: 2023/1/3 分页工具可以不用

    /**
     * 存放用户请求的 Page 对象
     */
    private static final ThreadLocal<IPage<?>> PAGE_HOLDER = new ThreadLocal<>();

    public static void setPage(IPage<?> currentPage) {
        PAGE_HOLDER.set(currentPage);
    }

    @SuppressWarnings("unchecked")
    public static <T> IPage<T> getPage() {
        return (IPage<T>) PAGE_HOLDER.get();
    }

    public static void removePage() {
        PAGE_HOLDER.remove();
    }

}
