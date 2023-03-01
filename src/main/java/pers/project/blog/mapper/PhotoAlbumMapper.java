package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.photoalbum.ManageAlbumDTO;
import pers.project.blog.entity.PhotoAlbum;

import java.util.List;

/**
 * 针对表【tb_photo_album(相册)】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
@Mapper
public interface PhotoAlbumMapper extends BaseMapper<PhotoAlbum> {

    /**
     * 查看分页的后台相册列表数据
     */
    List<ManageAlbumDTO> listAdminPhotoAlbums(@Param("offset") long offset,
                                              @Param("size") long size,
                                              @Param("keywords") String keywords);

}




