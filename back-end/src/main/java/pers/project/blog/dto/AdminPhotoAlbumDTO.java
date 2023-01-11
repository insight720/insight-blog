package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台相册信息
 *
 * @author Luo Fei
 * @date 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPhotoAlbumDTO {

    /**
     * 相册 ID
     */
    private Integer id;

    /**
     * 相册名
     */
    private String albumName;

    /**
     * 相册描述
     */
    private String albumDesc;

    /**
     * 相册封面
     */
    private String albumCover;

    /**
     * 照片数量
     */
    private Integer photoCount;

    /**
     * 状态值 1 公开 2 私密
     */
    private Integer status;

}
