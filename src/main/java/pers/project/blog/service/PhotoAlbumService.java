package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.photoalbum.ManageAlbumDTO;
import pers.project.blog.dto.photoalbum.PhotoAlbumDTO;
import pers.project.blog.entity.PhotoAlbum;
import pers.project.blog.vo.photoalbum.PhotoAlbumVO;

import java.util.List;

/**
 * 针对表【tb_photo_album(相册)】的数据库操作 Service
 *
 * @author Luo Fei
 * @version 2023-01-10
 */
public interface PhotoAlbumService extends IService<PhotoAlbum> {

    /**
     * 保存相册封面
     */
    String savePhotoAlbumCover(MultipartFile multipartFile);

    /**
     * 保存或更新相册
     */
    void saveOrUpdatePhotoAlbum(PhotoAlbumVO photoAlbumVO);

    /**
     * 根据 ID 删除相册（逻辑删除或物理删除）
     */
    void removePhotoAlbum(Integer albumId);

    /**
     * 查看后台相册列表数据
     */
    PageDTO<ManageAlbumDTO> listManageAlbums(String keywords);

    /**
     * 获取后台点击相册时所需的所有相册列表数据
     */
    List<PhotoAlbumDTO> listAdminPhotoAlbums();

    /**
     * 后台点击相册时根据 ID 获取相册信息
     */
    ManageAlbumDTO getManageAlbum(Integer albumId);

    /**
     * 前台获取相册列表数据
     */
    List<PhotoAlbumDTO> listPhotoAlbums();

}
