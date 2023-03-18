package pers.project.blog.dto.photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 照片数据
 *
 * @author Luo Fei
 * @version 2023/1/10
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoDTO {

    /**
     * 相册封面
     */
    private String photoAlbumCover;

    /**
     * 相册名
     */
    private String photoAlbumName;

    /**
     * 照片列表
     */
    private List<String> photoList;

}
