package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.AdminPhotoAlbumDTO;
import pers.project.blog.entity.PhotoAlbumEntity;
import pers.project.blog.vo.ConditionVO;

import java.util.List;

/**
 * 针对表【tb_photo_album(相册)】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
@Mapper
public interface PhotoAlbumMapper extends BaseMapper<PhotoAlbumEntity> {

    /**
     * 查询后台的分页相册信息列表
     *
     * @param offset      条数偏移量
     * @param size        页面最大条数
     * @param conditionVO 条件
     * @return {@code List<AdminPhotoAlbumDTO>} 相册信息列表
     */
    List<AdminPhotoAlbumDTO> listAdminPhotoAlbumDTOs(@Param("offset") long offset,
                                                     @Param("size") long size,
                                                     @Param("conditionVO") ConditionVO conditionVO);

}




