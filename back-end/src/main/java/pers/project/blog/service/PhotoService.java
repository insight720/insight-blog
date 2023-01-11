package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.AdminPhotoDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.PhotoDTO;
import pers.project.blog.entity.PhotoEntity;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.DeleteVO;
import pers.project.blog.vo.PhotoUpdateVO;
import pers.project.blog.vo.PhotoVO;

/**
 * 针对表【tb_photo(照片)】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
public interface PhotoService extends IService<PhotoEntity> {

    /**
     * 查询后台的分页相册列表
     *
     * @param conditionVO 查询条件
     * @return {@code PageDTO<AdminPhotoDTO>} 分页相册列表
     */
    PageDTO<AdminPhotoDTO> listAdminPhotos(ConditionVO conditionVO);

    /**
     * 保存照片
     *
     * @param photoVO 照片信息
     */
    void savePhoto(PhotoVO photoVO);

    /**
     * 更新照片信息
     *
     * @param photoUpdateVO 照片更新信息
     */
    void updatePhoto(PhotoUpdateVO photoUpdateVO);

    /**
     * 移动照片相册
     *
     * @param photoVO 照片信息
     */
    void updatePhotoAlbum(PhotoVO photoVO);

    /**
     * 更新照片删除状态
     *
     * @param deleteVO 删除信息
     */
    void updatePhotoDelete(DeleteVO deleteVO);

    /**
     * 根据相册 ID 获取照片列表
     *
     * @param albumId 相册 ID
     * @return {@link PhotoDTO} 照片列表信息
     */
    PhotoDTO listPhotosInAlbum(Integer albumId);

}
