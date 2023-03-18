package pers.project.blog.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文章搜索配置属性
 *
 * @author Luo Fei
 * @version 2023/2/6
 */
@Data
@ConfigurationProperties(prefix = "blog.search")
public class SearchProperties {

    /**
     * 搜索策略
     * <pre>
     * mysql MySQL 搜索（默认）
     * elasticsearch ES 搜索
     * </pre>
     */
    private String strategy = "mysql";

}
