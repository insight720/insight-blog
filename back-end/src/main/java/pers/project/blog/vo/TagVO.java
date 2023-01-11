package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 标签
 *
 * @author Luo Fei
 * @date 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "标签")
public class TagVO {

    /**
     * ID
     */
    @Schema(name = "id", title = "标签 ID", type = "Integer")
    private Integer id;

    /**
     * 标签名
     */
    @NotBlank(message = "标签名不能为空")
    @Schema(name = "categoryName", title = "标签名", type = "String")
    private String tagName;

}
