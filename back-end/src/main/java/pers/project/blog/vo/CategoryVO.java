package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 分类数据
 *
 * @author Luo Fei
 * @date 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "分类数据")
public class CategoryVO {

    /**
     * ID
     */
    @Schema(name = "id", title = "分类 ID", type = "Integer")
    private Integer id;

    /**
     * 分类名
     */
    @NotBlank(message = "分类名不能为空")
    @Schema(name = "categoryName", title = "分类名", type = "String")
    private String categoryName;

}
