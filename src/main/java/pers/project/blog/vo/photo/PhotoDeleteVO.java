package pers.project.blog.vo.photo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 照片删除数据
 *
 * @author Luo Fei
 * @version 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDeleteVO {

    /**
     * ID 列表
     */
    @NotNull(message = "ID 不能为空")
    @Schema(description = "ID 列表")
    private List<Integer> idList;

    /**
     * 状态值
     */
    @NotNull(message = "删除状态不能为空")
    @Schema(description = "删除状态")
    private Integer isDelete;

}
