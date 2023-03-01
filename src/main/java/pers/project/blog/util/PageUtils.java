package pers.project.blog.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.project.blog.dto.article.PageDTO;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 分页工具类
 *
 * @author Luo Fei
 * @date 2022/12/31
 */
public abstract class PageUtils {

    /**
     * 存放当前分页请求的 MyBatis-Plus 的分页对象
     */
    private static final ThreadLocal<IPage<?>> PAGE_HOLDER = new ThreadLocal<>();

    public static void setPage(IPage<?> currentPage) {
        PAGE_HOLDER.set(currentPage);
    }

    public static void removePage() {
        PAGE_HOLDER.remove();
    }

    /**
     * 获取分页对象
     *
     * @return MyBatis-Plus 的分页对象
     */
    @SuppressWarnings("unchecked")
    public static <T> IPage<T> getPage() {
        return (IPage<T>) PAGE_HOLDER.get();
    }

    /**
     * 获取当前分页偏移量
     *
     * @return 分页偏移量
     */
    public static long offset() {
        return PAGE_HOLDER.get().offset();
    }

    /**
     * 获取每页显示条数
     *
     * @return 每页显示条数
     */
    public static long size() {
        return PAGE_HOLDER.get().getSize();
    }

    /**
     * 组装分页数据
     *
     * @param page MyBatis-Plus 的分页对象
     * @return 分页数据
     */
    @NotNull
    public static <T> PageDTO<T> build(@NotNull IPage<T> page) {
        return PageDTO.<T>builder()
                .recordList(page.getRecords())
                .count(page.getTotal()).build();
    }

    /**
     * 组装分页数据
     *
     * @param tList 数据列表
     * @param count 数据总数
     * @return 分页数据
     */
    @NotNull
    public static <T> PageDTO<T> build(@NotNull List<T> tList, @NotNull Long count) {
        return PageDTO.<T>builder()
                .recordList(tList)
                .count(count).build();
    }

}
