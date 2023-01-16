package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.AdminPhotoDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.PhotoDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.service.PhotoService;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.DeleteVO;
import pers.project.blog.vo.PhotoUpdateVO;
import pers.project.blog.vo.PhotoVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 照片控制器
 *
 * @author Luo Fei
 * @date 2023/1/10
 */
@Tag(name = "照片模块")
@RestController
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @Operation(summary = "根据相册 ID 获取照片列表")
    @GetMapping("/albums/{albumId}/photos")
    public Result<PhotoDTO> listPhotosInAlbum(@PathVariable("albumId") Integer albumId) {
        return Result.ok(photoService.listPhotosInAlbum(albumId));
    }

    @OperationLog(type = LogConstant.SAVE)
    @Operation(summary = "保存照片")
    @PostMapping("/admin/photos")
    public Result<?> savePhoto(@Valid @RequestBody PhotoVO photoVO) {
        photoService.savePhoto(photoVO);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.UPDATE)
    @Operation(summary = "更新照片信息")
    @PutMapping("/admin/photos")
    public Result<?> updatePhoto(@Valid @RequestBody PhotoUpdateVO photoUpdateVO) {
        photoService.updatePhoto(photoUpdateVO);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.REMOVE)
    @Operation(summary = "删除照片")
    @DeleteMapping("/admin/photos")
    public Result<?> removePhotos(@RequestBody List<Integer> photoIdList) {
        photoService.removeBatchByIds(photoIdList);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.UPDATE)
    @Operation(summary = "移动照片相册")
    @PutMapping("/admin/photos/album")
    public Result<?> updatePhotoAlbum(@Valid @RequestBody PhotoVO photoVO) {
        photoService.updatePhotoAlbum(photoVO);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.UPDATE)
    @Operation(summary = "更新照片删除状态")
    @PutMapping("/admin/photos/delete")
    public Result<?> updatePhotoDelete(@Valid @RequestBody DeleteVO deleteVO) {
        photoService.updatePhotoDelete(deleteVO);
        return Result.ok();
    }

    @Operation(summary = "查看后台的分页相册列表")
    @GetMapping("/admin/photos")
    public Result<PageDTO<AdminPhotoDTO>> listAdminPhotos(ConditionVO conditionVO) {
        return Result.ok(photoService.listAdminPhotos(conditionVO));
    }

}
