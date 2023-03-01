package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.constant.FilePathConst;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.photoalbum.ManageAlbumDTO;
import pers.project.blog.dto.photoalbum.PhotoAlbumDTO;
import pers.project.blog.entity.Photo;
import pers.project.blog.entity.PhotoAlbum;
import pers.project.blog.enums.AlbumStatusEnum;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.PhotoAlbumMapper;
import pers.project.blog.mapper.PhotoMapper;
import pers.project.blog.service.PhotoAlbumService;
import pers.project.blog.strategy.context.UploadContext;
import pers.project.blog.util.ConvertUtils;
import pers.project.blog.util.PageUtils;
import pers.project.blog.vo.photoalbum.PhotoAlbumVO;

import javax.annotation.Resource;
import java.util.List;

import static pers.project.blog.constant.CacheConst.PHOTO_ALBUM;
import static pers.project.blog.constant.GenericConst.*;

/**
 * 针对表【tb_photo_album(相册)】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
@Service
public class PhotoAlbumServiceImpl extends ServiceImpl<PhotoAlbumMapper, PhotoAlbum> implements PhotoAlbumService {

    @Resource
    private PhotoMapper photoMapper;

    @Override
    @CacheEvict(cacheNames = PHOTO_ALBUM, allEntries = true)
    public String savePhotoAlbumCover(MultipartFile multipartFile) {
        return UploadContext.executeStrategy
                (multipartFile, FilePathConst.PHOTO_DIR);
    }

    @Override
    @CacheEvict(cacheNames = PHOTO_ALBUM, allEntries = true)
    public void saveOrUpdatePhotoAlbum(PhotoAlbumVO photoAlbumVO) {
        // 新建相册时，判断相册名是否存在
        boolean save = photoAlbumVO.getId() == null;
        if (save) {
            boolean exists = lambdaQuery()
                    .eq(PhotoAlbum::getAlbumName, photoAlbumVO.getAlbumName())
                    .exists();
            if (exists) {
                throw new ServiceException("相册名已存在");
            }
        }
        saveOrUpdate(ConvertUtils.convert(photoAlbumVO, PhotoAlbum.class));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = PHOTO_ALBUM, allEntries = true)
    public void removePhotoAlbum(Integer albumId) {
        // 判断相册下是否有照片，无照片则物理删除相册
        boolean exists = new LambdaQueryChainWrapper<>(photoMapper)
                .eq(Photo::getAlbumId, albumId)
                .exists();
        if (!exists) {
            removeById(albumId);
            return;
        }
        // 相册下有照片则逻辑删除相册和照片
        lambdaUpdate()
                .set(PhotoAlbum::getIsDelete, TRUE_OF_INT)
                .eq(PhotoAlbum::getId, albumId)
                .update();
        new LambdaUpdateChainWrapper<>(photoMapper)
                .set(Photo::getIsDelete, TRUE_OF_INT)
                .eq(Photo::getAlbumId, albumId)
                .update();
    }

    @Override
    public PageDTO<ManageAlbumDTO> listManageAlbums(String keywords) {
        // 查询符合条件的相册量
        Long count = lambdaQuery()
                .eq(PhotoAlbum::getIsDelete, FALSE_OF_INT)
                .like(StringUtils.isNotBlank(keywords),
                        PhotoAlbum::getAlbumName, keywords)
                .count();
        if (count.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        // 查询符合条件的分页相册数据列表
        List<ManageAlbumDTO> adminPhotoAlbumList = baseMapper
                .listAdminPhotoAlbums(PageUtils.offset(), PageUtils.size(), keywords);
        return PageUtils.build(adminPhotoAlbumList, count);
    }

    @Override
    @Cacheable(cacheNames = PHOTO_ALBUM, key = "#root.methodName", sync = true)
    public List<PhotoAlbumDTO> listAdminPhotoAlbums() {
        List<PhotoAlbum> photoAlbumList = lambdaQuery()
                .eq(PhotoAlbum::getIsDelete, FALSE_OF_INT)
                .list();
        return ConvertUtils.convertList(photoAlbumList, PhotoAlbumDTO.class);
    }

    @Override
    public ManageAlbumDTO getManageAlbum(Integer albumId) {
        // 查询相册信息
        PhotoAlbum photoAlbum = getById(albumId);
        ManageAlbumDTO manageAlbumDTO = ConvertUtils.convert
                (photoAlbum, ManageAlbumDTO.class);
        // 查询相册的照片数量
        Long photosCount = new LambdaQueryChainWrapper<>(photoMapper)
                .eq(Photo::getIsDelete, FALSE_OF_INT)
                .eq(Photo::getAlbumId, albumId)
                .count();
        manageAlbumDTO.setPhotoCount(photosCount.intValue());
        return manageAlbumDTO;
    }

    @Override
    @Cacheable(cacheNames = PHOTO_ALBUM, key = "#root.methodName", sync = true)
    public List<PhotoAlbumDTO> listPhotoAlbums() {
        List<PhotoAlbum> photoAlbumList = lambdaQuery()
                .eq(PhotoAlbum::getStatus, AlbumStatusEnum.PUBLIC.getStatus())
                .eq(PhotoAlbum::getIsDelete, FALSE_OF_INT)
                .orderByDesc(PhotoAlbum::getId)
                .list();
        return ConvertUtils.convertList(photoAlbumList, PhotoAlbumDTO.class);
    }

}




