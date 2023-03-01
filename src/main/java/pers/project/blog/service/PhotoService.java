package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.photo.AdminPhotoDTO;
import pers.project.blog.dto.photo.PhotoDTO;
import pers.project.blog.entity.Photo;
import pers.project.blog.vo.photo.*;

import java.util.List;

/**
 * 针对表【tb_photo(照片)】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
public interface PhotoService extends IService<Photo> {

    /**
     * 保存照片
     */
    void savePhoto(PhotoVO photoVO);

    /**
     * 删除照片（物理删除）
     */
    void removePhotos(List<Integer> photoIdList);

    /**
     * 更新照片删除状态（逻辑删除和恢复）
     */
    void updatePhotoDelete(PhotoDeleteVO photoDeleteVO);

    /**
     * 更新照片信息
     */
    void updatePhoto(PhotoUpdateVO photoUpdateVO);

    /**
     * 移动照片相册
     */
    void updatePhotoAlbum(MovePhotoVO movePhotoVO);

    /**
     * 获取后台查看相册照片或回收站照片的分页数据
     */
    PageDTO<AdminPhotoDTO> listAdminPhotos(PhotoReviewVO photoReviewVO);

    /**
     * 根据相册 ID 获取照片列表
     */
    PhotoDTO listPhotosInAlbum(Integer albumId);

}
