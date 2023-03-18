package pers.project.blog.vo.photoalbum;

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
 * @version 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoAlbumVO {

    /**
     * 相册 ID
     */
    @Schema(description = "相册 ID")
    private Integer id;

    /**
     * 相册名
     */
    @NotBlank(message = "相册名不能为空")
    @Schema(description = "相册名")
    private String albumName;

    /**
     * 相册描述
     */
    @Schema(description = "相册描述")
    private String albumDesc;

    /**
     * 相册封面
     */
    @NotBlank(message = "相册封面不能为空")
    @Schema(description = "相册封面")
    private String albumCover;

    /**
     * 状态值 1 公开 2 私密
     */
    @Schema(description = "状态值")
    private Integer status;

}
