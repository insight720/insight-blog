package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.blog.vo.PageVO;
import pers.project.blog.vo.WebsiteConfigVO;

import java.util.List;

/**
 * 博客首页信息
 *
 * @author Luo Fei
 * @date 2023/1/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogInfoDTO {

    /**
     * 文章数量
     */
    private Integer articleCount;

    /**
     * 分类数量
     */
    private Integer categoryCount;

    /**
     * 标签数量
     */
    private Integer tagCount;

    /**
     * 访问量
     */
    private String viewsCount;

    /**
     * 网站配置
     */
    private WebsiteConfigVO websiteConfig;

    /**
     * 页面列表
     */
    private List<PageVO> pageList;

}
