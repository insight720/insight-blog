package pers.project.blog.vo.photo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 后台查看相册照片或回收站照片的数据
 *
 * @author Luo Fei
 * @date 2023/2/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoReviewVO {

    /**
     * 相册 ID
     */
    @Schema(description = "相册 ID")
    private Integer albumId;

    /**
     * 是否删除（0 否 1 是）
     */
    @NotNull(message = "照片删除状态不能为 null")
    @Schema(description = "是否删除")
    private Integer isDelete;

}
