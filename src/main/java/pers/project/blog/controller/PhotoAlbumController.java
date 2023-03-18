package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.annotation.OperatingLog;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.photoalbum.ManageAlbumDTO;
import pers.project.blog.dto.photoalbum.PhotoAlbumDTO;
import pers.project.blog.service.PhotoAlbumService;
import pers.project.blog.util.Result;
import pers.project.blog.vo.photoalbum.PhotoAlbumVO;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static pers.project.blog.enums.OperationLogEnum.*;

/**
 * 相册控制器
 *
 * @author Luo Fei
 * @version 2023/1/10
 */
@Tag(name = "相册模块")
@Validated
@RestController
public class PhotoAlbumController {

    @Resource
    private PhotoAlbumService photoAlbumService;

    @OperatingLog(type = UPLOAD)
    @Operation(summary = "保存相册封面")
    @PostMapping("/admin/photos/albums/cover")
    public Result<String> savePhotoAlbumCover
            (@NotNull @RequestParam("file") MultipartFile multipartFile) {
        return Result.ok(photoAlbumService.savePhotoAlbumCover(multipartFile));
    }

    @OperatingLog(type = SAVE_OR_UPDATE)
    @Operation(summary = "保存或更新相册")
    @PostMapping("/admin/photos/albums")
    public Result<?> saveOrUpdatePhotoAlbum(@Valid @RequestBody PhotoAlbumVO photoAlbumVO) {
        photoAlbumService.saveOrUpdatePhotoAlbum(photoAlbumVO);
        return Result.ok();
    }

    @OperatingLog(type = REMOVE)
    @Operation(summary = "根据 ID 删除相册")
    @DeleteMapping("/admin/photos/albums/{albumId}")
    public Result<?> removePhotoAlbum(@NotNull @PathVariable Integer albumId) {
        photoAlbumService.removePhotoAlbum(albumId);
        return Result.ok();
    }

    @Operation(summary = "查看后台相册管理")
    @GetMapping("/admin/photos/albums")
    public Result<PageDTO<ManageAlbumDTO>> reviewAlbumManagement(
            @RequestParam(required = false) String keywords) {
        return Result.ok(photoAlbumService.listManageAlbums(keywords));
    }

    @Operation(summary = "后台点击相册")
    @GetMapping("/admin/photos/albums/info")
    public Result<List<PhotoAlbumDTO>> whenAdminClicksAlbum() {
        return Result.ok(photoAlbumService.listAdminPhotoAlbums());
    }

    @Operation(summary = "后台根据 ID 获取相册信息")
    @GetMapping("/admin/photos/albums/{albumId}/info")
    public Result<ManageAlbumDTO> whenAdminClicksAlbum(@NotNull @PathVariable
                                                       Integer albumId) {
        return Result.ok(photoAlbumService.getManageAlbum(albumId));
    }

    @Operation(summary = "前台查看娱乐相册")
    @GetMapping("/photos/albums")
    public Result<List<PhotoAlbumDTO>> viewEntertainmentAlbums() {
        return Result.ok(photoAlbumService.listPhotoAlbums());
    }

}
