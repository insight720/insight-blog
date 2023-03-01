package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.photo.AdminPhotoDTO;
import pers.project.blog.dto.photo.PhotoDTO;
import pers.project.blog.service.PhotoService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.photo.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.*;

/**
 * 照片控制器
 *
 * @author Luo Fei
 * @date 2023/1/10
 */
@Tag(name = "照片模块")
@Validated
@RestController
public class PhotoController {

    @Resource
    private PhotoService photoService;

    @OperatingLog(type = SAVE)
    @Operation(summary = "保存相册照片")
    @PostMapping("/admin/photos")
    public Result<?> savePhoto(@Valid @RequestBody PhotoVO photoVO) {
        photoService.savePhoto(photoVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "删除相册照片")
    @DeleteMapping("/admin/photos")
    public Result<?> removePhotos(@NotEmpty @RequestBody List<Integer> photoIdList) {
        photoService.removePhotos(photoIdList);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "更新相照片信息")
    @PutMapping("/admin/photos")
    public Result<?> updatePhoto(@Valid @RequestBody PhotoUpdateVO photoUpdateVO) {
        photoService.updatePhoto(photoUpdateVO);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "移动照片相册")
    @PutMapping("/admin/photos/album")
    public Result<?> updatePhotoAlbum(@Valid @RequestBody MovePhotoVO movePhotoVO) {
        photoService.updatePhotoAlbum(movePhotoVO);
        return Result.ok();
    }

    @OperatingLog(type = UPDATE)
    @Operation(summary = "更新照片删除状态")
    @PutMapping("/admin/photos/delete")
    public Result<?> updatePhotoDelete(@Valid @RequestBody PhotoDeleteVO photoDeleteVO) {
        photoService.updatePhotoDelete(photoDeleteVO);
        return Result.ok();
    }

    @Operation(summary = "后台查看相册照片或回收站照片")
    @GetMapping("/admin/photos")
    public Result<PageDTO<AdminPhotoDTO>> reviewPhotoManagement
            (@Valid PhotoReviewVO photoReviewVO) {
        return Result.ok(photoService.listAdminPhotos(photoReviewVO));
    }

    @Operation(summary = "前台点击查看一个相册")
    @GetMapping("/albums/{albumId}/photos")
    public Result<PhotoDTO> listPhotosInAlbum(@NotNull @PathVariable Integer albumId) {
        return Result.ok(photoService.listPhotosInAlbum(albumId));
    }

}
