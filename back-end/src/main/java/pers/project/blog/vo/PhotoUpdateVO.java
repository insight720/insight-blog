package pers.project.blog.vo;

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
 * @date 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "照片更新数据")
public class PhotoUpdateVO {

    /**
     * 照片 ID
     */
    @NotNull(message = "照片 ID 不能为空")
    @Schema(name = "id", title = "照片 ID", type = "Integer")
    private Integer id;

    /**
     * 照片名
     */
    @NotBlank(message = "照片名不能为空")
    @Schema(name = "photoName", title = "照片名", type = "String")
    private String photoName;

    /**
     * 照片描述
     */
    @Schema(name = "photoDesc", title = "照片描述", type = "String")
    private String photoDesc;

}
