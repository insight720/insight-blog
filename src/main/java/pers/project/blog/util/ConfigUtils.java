package pers.project.blog.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pers.project.blog.dto.bloginfo.WebsiteConfig;
import pers.project.blog.service.BlogInfoService;

import java.util.function.Function;

/**
 * 网站配置工具类
 *
 * @author Luo Fei
 * @date 2023/2/4
 */
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigUtils {

    private static BlogInfoService blogInfoService;

    @Autowired
    public void setBlogInfoService(BlogInfoService blogInfoService) {
        ConfigUtils.blogInfoService = blogInfoService;
    }

    /**
     * 获取缓存中的网站配置信息
     *
     * @param function getter 方法引用
     * @return 配置信息
     */
    @NotNull
    public static <T> T getCache(@NotNull Function<WebsiteConfig, T> function) {
        T t = function.apply(blogInfoService.getWebSiteConfig());
        if (t == null) {
            throw new RuntimeException("缺少网站配置信息");
        }
        return t;
    }

}
