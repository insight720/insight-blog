package pers.project.blog.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pers.project.blog.dto.tag.TagDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 首页文章数据
 *
 * @author Luo Fei
 * @date 2023/1/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomePageArticleDTO {

    /**
     * ID
     */
    private Integer id;

    /**
     * 文章缩略图
     */
    private String articleCover;

    /**
     * 标题
     */
    private String articleTitle;

    /**
     * 内容
     */
    private String articleContent;

    /**
     * 发表时间
     */
    private LocalDateTime createTime;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 文章类型
     */
    private Integer type;

    /**
     * 文章分类 ID
     */
    private Integer categoryId;

    /**
     * 文章分类名
     */
    private String categoryName;

    /**
     * 文章标签
     */
    private List<TagDTO> tagDTOList;

}
