package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 用户禁用状态
 *
 * @author Luo Fei
 * @date 2023/1/1
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "用户禁用状态")
public class UserDisableVO {

    /**
     * ID
     */
    @NotNull(message = "用户 ID 不能为空")
    @Schema(name = "id", title = "用户 ID", type = "Integer")
    private Integer id;

    /**
     * 置顶状态
     */
    @NotNull(message = "置顶状态不能为空")
    @Schema(name = "isDisable", title = "置顶状态", type = "Integer")
    private Integer isDisable;

}
