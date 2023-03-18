package pers.project.blog.vo.photo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 照片更新数据
 *
 * @author Luo Fei
 * @version 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoUpdateVO {

    /**
     * 照片 ID
     */
    @NotNull(message = "照片 ID 不能为空")
    @Schema(description = "照片 ID")
    private Integer id;

    /**
     * 照片名
     */
    @NotBlank(message = "照片名不能为空")
    @Schema(description = "照片名")
    private String photoName;

    /**
     * 照片描述
     */
    @Schema(description = "照片描述")
    private String photoDesc;

}
