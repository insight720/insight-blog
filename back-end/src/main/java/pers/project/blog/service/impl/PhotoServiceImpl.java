package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.constant.BooleanConstant;
import pers.project.blog.constant.enumeration.PhotoAlbumStatusEnum;
import pers.project.blog.dto.AdminPhotoDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.PhotoDTO;
import pers.project.blog.entity.PhotoAlbumEntity;
import pers.project.blog.entity.PhotoEntity;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.PhotoMapper;
import pers.project.blog.service.PhotoAlbumService;
import pers.project.blog.service.PhotoService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.DeleteVO;
import pers.project.blog.vo.PhotoUpdateVO;
import pers.project.blog.vo.PhotoVO;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 针对表【tb_photo】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, PhotoEntity> implements PhotoService {

    private final PhotoAlbumService photoAlbumService;

    public PhotoServiceImpl(PhotoAlbumService photoAlbumService) {
        this.photoAlbumService = photoAlbumService;
    }

    @Override
    public PageDTO<AdminPhotoDTO> listAdminPhotos(ConditionVO conditionVO) {
        // 查询符合条件的照片分页数据
        IPage<PhotoEntity> page = lambdaQuery()
                .eq(Objects.nonNull(conditionVO.getAlbumId()),
                        PhotoEntity::getAlbumId, conditionVO.getAlbumId())
                .eq(PhotoEntity::getIsDelete, conditionVO.getIsDelete())
                .orderByDesc(PhotoEntity::getId)
                .orderByDesc(PhotoEntity::getUpdateTime)
                .page(PaginationUtils.getPage());
        List<AdminPhotoDTO> adminPhotoDTOList
                = ConversionUtils.covertList(page.getRecords(), AdminPhotoDTO.class);
        return PageDTO.of(adminPhotoDTOList, (int) page.getTotal());
    }

    @Override
    public void savePhoto(PhotoVO photoVO) {
        // 保存照片列表，以 19 位数字唯一 ID 作为照片名
        Integer albumId = photoVO.getAlbumId();
        List<PhotoEntity> photoEntityList = photoVO
                .getPhotoUrlList()
                .stream()
                .map(photoUrl -> PhotoEntity.builder()
                        .albumId(albumId)
                        .photoName(IdWorker.getIdStr())
                        .photoSrc(photoUrl)
                        .build())
                .collect(Collectors.toList());
        saveBatch(photoEntityList);
    }

    @Override
    public void updatePhoto(PhotoUpdateVO photoUpdateVO) {
        updateById(ConversionUtils.convertObject(photoUpdateVO, PhotoEntity.class));
    }

    @Override
    public void updatePhotoAlbum(PhotoVO photoVO) {
        Integer albumId = photoVO.getAlbumId();
        List<PhotoEntity> photoEntityList = photoVO.getPhotoIdList()
                .stream()
                .map(photoId -> PhotoEntity.builder()
                        .id(photoId)
                        .albumId(albumId)
                        .build())
                .collect(Collectors.toList());
        updateBatchById(photoEntityList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updatePhotoDelete(DeleteVO deleteVO) {
        // 逻辑删除或恢复照片
        Integer isDelete = deleteVO.getIsDelete();
        List<PhotoEntity> photoEntityList = deleteVO.getIdList()
                .stream()
                .map(photoId -> PhotoEntity.builder()
                        .id(photoId)
                        .isDelete(isDelete)
                        .build())
                .collect(Collectors.toList());
        updateBatchById(photoEntityList);

        // 恢复照片的相册已被逻辑删除，则恢复相册
        if (isDelete.equals(BooleanConstant.FALSE)) {
            List<PhotoAlbumEntity> photoAlbumEntityList = lambdaQuery()
                    .select(PhotoEntity::getAlbumId)
                    .in(PhotoEntity::getId, deleteVO.getIdList())
                    .groupBy(PhotoEntity::getAlbumId)
                    .list()
                    .stream()
                    .map(photoEntity -> PhotoAlbumEntity.builder()
                            .id(photoEntity.getAlbumId())
                            .isDelete(BooleanConstant.FALSE)
                            .build())
                    .collect(Collectors.toList());
            photoAlbumService.updateBatchById(photoAlbumEntityList);
        }
    }

    @Override
    public PhotoDTO listPhotosInAlbum(Integer albumId) {
        // 查询相册信息
        PhotoAlbumEntity photoAlbumEntity = Optional
                .ofNullable(photoAlbumService.lambdaQuery()
                        .eq(PhotoAlbumEntity::getId, albumId)
                        .eq(PhotoAlbumEntity::getIsDelete, BooleanConstant.FALSE)
                        .eq(PhotoAlbumEntity::getStatus, PhotoAlbumStatusEnum.PUBLIC.getStatus())
                        .one())
                .orElseThrow(() -> new ServiceException("相册不存在"));

        // 查询照片信息
        List<String> photoUrlList = lambdaQuery()
                .select(PhotoEntity::getPhotoSrc)
                .eq(PhotoEntity::getAlbumId, albumId)
                .eq(PhotoEntity::getIsDelete, BooleanConstant.FALSE)
                .orderByDesc(PhotoEntity::getId)
                .page(PaginationUtils.getPage())
                .getRecords()
                .stream()
                .map(PhotoEntity::getPhotoSrc)
                .collect(Collectors.toList());

        return PhotoDTO.builder()
                .photoAlbumCover(photoAlbumEntity.getAlbumCover())
                .photoAlbumName(photoAlbumEntity.getAlbumName())
                .photoList(photoUrlList)
                .build();
    }

}




