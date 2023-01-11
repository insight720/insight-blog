package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 文章置顶
 *
 * @author Luo Fei
 * @date 2023/1/9
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "文章置顶")
public class ArticleTopVO {

    /**
     * ID
     */
    @NotNull(message = "ID 不能为空")
    private Integer id;

    /**
     * 置顶状态不能为空
     */
    @NotNull(message = "置顶状态不能为空")
    private Integer isTop;

}
