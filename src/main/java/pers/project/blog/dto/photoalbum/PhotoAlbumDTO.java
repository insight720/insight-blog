package pers.project.blog.dto.photoalbum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 相册数据
 *
 * @author Luo Fei
 * @version 2023/1/10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoAlbumDTO {

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

}
