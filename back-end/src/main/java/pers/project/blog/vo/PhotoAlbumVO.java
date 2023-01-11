package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 相册数据
 *
 * @author Luo Fei
 * @date 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "相册数据")
public class PhotoAlbumVO {

    /**
     * 相册 ID
     */
    @Schema(name = "id", title = "相册 ID", type = "Integer")
    private Integer id;

    /**
     * 相册名
     */
    @NotBlank(message = "相册名不能为空")
    @Schema(name = "albumName", title = "相册名", type = "String")
    private String albumName;

    /**
     * 相册描述
     */
    @Schema(name = "albumDesc", title = "相册描述", type = "String")
    private String albumDesc;

    /**
     * 相册封面
     */
    @NotBlank(message = "相册封面不能为空")
    @Schema(name = "albumCover", title = "相册封面", type = "String")
    private String albumCover;

    /**
     * 状态值 1 公开 2 私密
     */
    @Schema(name = "status", title = "状态值", type = "Integer")
    private Integer status;

}
