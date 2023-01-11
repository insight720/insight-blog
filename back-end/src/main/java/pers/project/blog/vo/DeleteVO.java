package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 删除状态信息
 *
 * @author Luo Fei
 * @date 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "删除状态信息")
public class DeleteVO {

    /**
     * ID 列表
     */
    @NotNull(message = "ID 不能为空")
    @Schema(name = "idList", description = "ID 列表", type = "List<Integer>")
    private List<Integer> idList;

    /**
     * 状态值
     */
    @NotNull(message = "状态值不能为空")
    @Schema(name = "isDelete", description = "删除状态", type = "Integer")
    private Integer isDelete;

}
