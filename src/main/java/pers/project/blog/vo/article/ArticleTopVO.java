package pers.project.blog.vo.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 文章置顶状态数据
 *
 * @author Luo Fei
 * @version 2023/1/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTopVO {

    /**
     * ID
     */
    @NotNull(message = "ID 不能为空")
    private Integer id;

    /**
     * 置顶状态
     */
    @NotNull(message = "置顶状态不能为空")
    private Integer isTop;

}
