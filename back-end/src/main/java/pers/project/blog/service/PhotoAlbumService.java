package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.AdminPhotoAlbumDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.PhotoAlbumDTO;
import pers.project.blog.entity.PhotoAlbumEntity;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.PhotoAlbumVO;

import java.util.List;

/**
 * 针对表【tb_photo_album(相册)】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
public interface PhotoAlbumService extends IService<PhotoAlbumEntity> {

    /**
     * 查看后台的分页相册列表
     *
     * @param conditionVO 条件
     * @return {@code  PageDTO<AdminPhotoAlbumDTO>} 相册列表
     */
    PageDTO<AdminPhotoAlbumDTO> listAdminPhotoAlbums(ConditionVO conditionVO);

    /**
     * 保存或更新相册
     *
     * @param photoAlbumVO 相册信息
     */
    void saveOrUpdatePhotoAlbum(PhotoAlbumVO photoAlbumVO);

    /**
     * 获取后台的所有相册列表信息
     *
     * @return {@code  List<PhotoAlbumDTO>} 相册列表信息
     */
    List<PhotoAlbumDTO> listAllAdminPhotoAlbums();

    /**
     * 根据 ID 删除相册
     *
     * @param albumId 相册 ID
     */
    void removePhotoAlbum(Integer albumId);

    /**
     * 根据 ID 获取相册信息
     *
     * @param albumId 相册 ID
     * @return {@code  AdminPhotoAlbumDTO} 相册信息
     */
    AdminPhotoAlbumDTO getAdminPhotoAlbumDTO(Integer albumId);

    /**
     * 获取相册列表
     *
     * @return {@code  List<PhotoAlbumDTO>} 相册列表
     */
    List<PhotoAlbumDTO> listPhotoAlbums();

}
