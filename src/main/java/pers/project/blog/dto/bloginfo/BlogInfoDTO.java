package pers.project.blog.dto.bloginfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.blog.vo.page.PageVO;

import java.util.List;

/**
 * 博客首页信息
 *
 * @author Luo Fei
 * @version 2023/1/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogInfoDTO {

    /**
     * 文章数量
     */
    private Long articleCount;

    /**
     * 分类数量
     */
    private Long categoryCount;

    /**
     * 标签数量
     */
    private Long tagCount;

    /**
     * 访问量
     */
    private String viewsCount;

    /**
     * 网站配置
     */
    private WebsiteConfig websiteConfig;

    /**
     * 页面列表
     */
    private List<PageVO> pageList;

}
