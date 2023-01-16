package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pers.project.blog.constant.BooleanConstant;
import pers.project.blog.constant.enumeration.PhotoAlbumStatusEnum;
import pers.project.blog.dto.AdminPhotoAlbumDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.PhotoAlbumDTO;
import pers.project.blog.entity.PhotoAlbumEntity;
import pers.project.blog.entity.PhotoEntity;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.PhotoAlbumMapper;
import pers.project.blog.mapper.PhotoMapper;
import pers.project.blog.service.PhotoAlbumService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.PhotoAlbumVO;

import java.util.List;

/**
 * 针对表【tb_photo_album(相册)】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-10
 */
@Service
public class PhotoAlbumServiceImpl extends ServiceImpl<PhotoAlbumMapper, PhotoAlbumEntity> implements PhotoAlbumService {

    private final PhotoMapper photoMapper;

    public PhotoAlbumServiceImpl(PhotoMapper photoMapper) {
        this.photoMapper = photoMapper;
    }

    @Override
    public PageDTO<AdminPhotoAlbumDTO> listAdminPhotoAlbums(ConditionVO conditionVO) {
        // 查询符合条件的相册量
        Long adminPhotoAlbumsCount = lambdaQuery()
                .eq(PhotoAlbumEntity::getIsDelete, BooleanConstant.FALSE)
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()),
                        PhotoAlbumEntity::getAlbumName, conditionVO.getKeywords())
                .count();
        if (adminPhotoAlbumsCount == 0) {
            return new PageDTO<>();
        }

        // 查询符合条件的相册信息列表
        IPage<Object> page = PaginationUtils.getPage();
        List<AdminPhotoAlbumDTO> adminPhotoAlbumList = baseMapper
                .listAdminPhotoAlbumDTOs(page.offset(), page.getSize(), conditionVO);

        return PageDTO.of(adminPhotoAlbumList, adminPhotoAlbumsCount.intValue());
    }

    @Override
    public void saveOrUpdatePhotoAlbum(PhotoAlbumVO photoAlbumVO) {
        // TODO: 2023/1/10 更新逻辑不对，相册名且 ID 相同，有可能更新其他信息
        // 判断相册名是否存在
        PhotoAlbumEntity photoAlbumEntity = lambdaQuery()
                .select(PhotoAlbumEntity::getId)
                .eq(PhotoAlbumEntity::getAlbumName, photoAlbumVO.getAlbumName())
                .one();
        if (photoAlbumEntity != null && photoAlbumEntity.getId().equals(photoAlbumVO.getId())) {
            throw new ServiceException("相册名已存在");
        }

        saveOrUpdate(ConversionUtils.convertObject(photoAlbumVO, PhotoAlbumEntity.class));
    }

    @Override
    public List<PhotoAlbumDTO> listAllAdminPhotoAlbums() {
        List<PhotoAlbumEntity> photoAlbumEntityList = lambdaQuery()
                .eq(PhotoAlbumEntity::getIsDelete, BooleanConstant.FALSE)
                .list();
        return ConversionUtils.covertList(photoAlbumEntityList, PhotoAlbumDTO.class);
    }

    @Override
    public void removePhotoAlbum(Integer albumId) {
        // 判断相册下是否有照片，无照片则物理删除相册
        Long photosCount = new LambdaQueryChainWrapper<>(photoMapper)
                .eq(PhotoEntity::getAlbumId, albumId)
                .count();
        if (photosCount == 0) {
            removeById(albumId);
            return;
        }

        // 相册下有照片则逻辑删除相册和照片
        lambdaUpdate()
                .set(PhotoAlbumEntity::getIsDelete, BooleanConstant.TRUE)
                .eq(PhotoAlbumEntity::getId, albumId)
                .update();
        new LambdaUpdateChainWrapper<>(photoMapper)
                .set(PhotoEntity::getIsDelete, BooleanConstant.TRUE)
                .eq(PhotoEntity::getAlbumId, albumId)
                .update();
    }

    @Override
    public AdminPhotoAlbumDTO getAdminPhotoAlbumDTO(Integer albumId) {
        // 查询相册信息
        PhotoAlbumEntity photoAlbumEntity = getById(albumId);
        AdminPhotoAlbumDTO adminPhotoAlbumDTO = ConversionUtils.convertObject
                (photoAlbumEntity, AdminPhotoAlbumDTO.class);

        // 查询相册的照片数量
        Long photosCount = new LambdaQueryChainWrapper<>(photoMapper)
                .eq(PhotoEntity::getIsDelete, BooleanConstant.FALSE)
                .eq(PhotoEntity::getAlbumId, albumId)
                .count();
        adminPhotoAlbumDTO.setPhotoCount(photosCount.intValue());

        return adminPhotoAlbumDTO;
    }

    @Override
    public List<PhotoAlbumDTO> listPhotoAlbums() {
        List<PhotoAlbumEntity> photoAlbumEntityList = lambdaQuery()
                .eq(PhotoAlbumEntity::getStatus, PhotoAlbumStatusEnum.PUBLIC.getStatus())
                .eq(PhotoAlbumEntity::getIsDelete, BooleanConstant.FALSE)
                .orderByDesc(PhotoAlbumEntity::getId)
                .list();
        return ConversionUtils.covertList(photoAlbumEntityList, PhotoAlbumDTO.class);
    }

}




