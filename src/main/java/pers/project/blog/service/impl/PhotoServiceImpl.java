package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.photo.AdminPhotoDTO;
import pers.project.blog.dto.photo.PhotoDTO;
import pers.project.blog.entity.Photo;
import pers.project.blog.entity.PhotoAlbum;
import pers.project.blog.enums.AlbumStatusEnum;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.PhotoMapper;
import pers.project.blog.service.PhotoAlbumService;
import pers.project.blog.service.PhotoService;
import pers.project.blog.util.BeanCopierUtils;
import pers.project.blog.util.ConvertUtils;
import pers.project.blog.util.PageUtils;
import pers.project.blog.util.SecurityUtils;
import pers.project.blog.vo.photo.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static pers.project.blog.constant.CacheConst.PHOTO_ALBUM;
import static pers.project.blog.constant.GenericConst.FALSE_OF_INT;

/**
 * 针对表【tb_photo】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {

    @Resource
    private PhotoAlbumService photoAlbumService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void savePhoto(PhotoVO photoVO) {
        Integer albumId = photoVO.getAlbumId();
        List<Photo> photoList = photoVO
                .getPhotoUrlList()
                .stream()
                .map(photoUrl -> Photo.builder()
                        .albumId(albumId)
                        .photoName(SecurityUtils.getUniqueName())
                        .photoSrc(photoUrl)
                        .build())
                .collect(Collectors.toList());
        saveBatch(photoList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void removePhotos(List<Integer> photoIdList) {
        removeBatchByIds(photoIdList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(cacheNames = PHOTO_ALBUM, allEntries = true)
    public void updatePhotoDelete(PhotoDeleteVO photoDeleteVO) {
        // 逻辑删除或恢复照片
        Integer isDelete = photoDeleteVO.getIsDelete();
        List<Integer> photoIdList = photoDeleteVO.getIdList();
        List<Photo> photoList = photoIdList
                .stream()
                .map(photoId -> Photo.builder()
                        .id(photoId)
                        .isDelete(isDelete)
                        .build())
                .collect(Collectors.toList());
        updateBatchById(photoList);
        // 要恢复照片的相册已被逻辑删除，则恢复相册
        if (isDelete.equals(FALSE_OF_INT)) {
            List<PhotoAlbum> photoAlbumList = lambdaQuery()
                    .select(Photo::getAlbumId)
                    .in(Photo::getId, photoIdList)
                    // GROUP BY 可以去重
                    .groupBy(Photo::getAlbumId)
                    .list()
                    .stream()
                    .map(Photo::getAlbumId)
                    .map(albumId -> PhotoAlbum.builder()
                            .id(albumId)
                            .isDelete(FALSE_OF_INT)
                            .build())
                    .collect(Collectors.toList());
            photoAlbumService.updateBatchById(photoAlbumList);
        }
    }

    @Override
    public void updatePhoto(PhotoUpdateVO photoUpdateVO) {
        updateById(BeanCopierUtils.copy(photoUpdateVO, Photo.class));
    }

    @Override
    public PageDTO<AdminPhotoDTO> listAdminPhotos(PhotoReviewVO photoReviewVO) {
        // 查询符合条件的照片分页数据
        IPage<Photo> page = lambdaQuery()
                .eq(Objects.nonNull(photoReviewVO.getAlbumId()),
                        Photo::getAlbumId, photoReviewVO.getAlbumId())
                .eq(Photo::getIsDelete, photoReviewVO.getIsDelete())
                .orderByDesc(Photo::getId)
                .orderByDesc(Photo::getUpdateTime)
                .page(PageUtils.getPage());
        List<AdminPhotoDTO> adminPhotoDTOList
                = ConvertUtils.convertList(page.getRecords(), AdminPhotoDTO.class);
        return PageUtils.build(adminPhotoDTOList, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updatePhotoAlbum(MovePhotoVO movePhotoVO) {
        Integer albumId = movePhotoVO.getAlbumId();
        List<Photo> photoList = movePhotoVO.getPhotoIdList()
                .stream()
                .map(photoId -> Photo.builder()
                        .id(photoId)
                        .albumId(albumId)
                        .build())
                .collect(Collectors.toList());
        updateBatchById(photoList);
    }

    @Override
    public PhotoDTO listPhotosInAlbum(Integer albumId) {
        // 查询相册信息
        PhotoAlbum photoAlbum = Optional
                .ofNullable(photoAlbumService.lambdaQuery()
                        .select(PhotoAlbum::getAlbumCover, PhotoAlbum::getAlbumName)
                        .eq(PhotoAlbum::getId, albumId)
                        .eq(PhotoAlbum::getIsDelete, FALSE_OF_INT)
                        .eq(PhotoAlbum::getStatus, AlbumStatusEnum.PUBLIC.getStatus())
                        .one())
                .orElseThrow(() -> new ServiceException("相册不存在"));
        // 查询照片信息
        List<String> photoUrlList = lambdaQuery()
                .select(Photo::getPhotoSrc)
                .eq(Photo::getAlbumId, albumId)
                .eq(Photo::getIsDelete, FALSE_OF_INT)
                .orderByDesc(Photo::getId)
                .page(PageUtils.getPage())
                .getRecords()
                .stream()
                .map(Photo::getPhotoSrc)
                .collect(Collectors.toList());
        return PhotoDTO.builder()
                .photoAlbumCover(photoAlbum.getAlbumCover())
                .photoAlbumName(photoAlbum.getAlbumName())
                .photoList(photoUrlList)
                .build();
    }

}




