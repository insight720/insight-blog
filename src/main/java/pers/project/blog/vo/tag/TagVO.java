package pers.project.blog.vo.tag;

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
 * @version 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagVO {

    /**
     * ID
     */
    @Schema(description = "标签 ID")
    private Integer id;

    /**
     * 标签名
     */
    @NotBlank(message = "标签名不能为空")
    @Schema(description = "标签名")
    private String tagName;

}
