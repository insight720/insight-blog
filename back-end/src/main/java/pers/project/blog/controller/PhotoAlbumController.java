package pers.project.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.annotation.OperationLog;
import pers.project.blog.constant.DirectoryUriConstant;
import pers.project.blog.constant.LogConstant;
import pers.project.blog.dto.AdminPhotoAlbumDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.PhotoAlbumDTO;
import pers.project.blog.dto.Result;
import pers.project.blog.service.PhotoAlbumService;
import pers.project.blog.strategy.context.UploadContext;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.PhotoAlbumVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 相册控制器
 *
 * @author Luo Fei
 * @date 2023/1/10
 */
@Tag(name = "相册模块")
@RestController
public class PhotoAlbumController {

    private final PhotoAlbumService photoAlbumService;

    public PhotoAlbumController(PhotoAlbumService photoAlbumService) {
        this.photoAlbumService = photoAlbumService;
    }

    @Operation(summary = "查看后台相册列表")
    @GetMapping("/admin/photos/albums")
    public Result<PageDTO<AdminPhotoAlbumDTO>> listAdminPhotoAlbums(ConditionVO conditionVO) {
        return Result.ok(photoAlbumService.listAdminPhotoAlbums(conditionVO));
    }

    @OperationLog(type = LogConstant.SAVE_OR_UPDATE)
    @Operation(summary = "保存或更新相册")
    @PostMapping("/admin/photos/albums")
    public Result<?> saveOrUpdatePhotoAlbum(@Valid @RequestBody PhotoAlbumVO photoAlbumVO) {
        photoAlbumService.saveOrUpdatePhotoAlbum(photoAlbumVO);
        return Result.ok();
    }

    @OperationLog(type = LogConstant.UPLOAD)
    @Operation(summary = "上传相册封面")
    @Parameter(name = "file", description = "相册封面", required = true,
            schema = @Schema(type = "MultipartFile"))
    @PostMapping("/admin/photos/albums/cover")
    public Result<String> savePhotoAlbumCover
            (@RequestParam("file") MultipartFile multipartFile) {
        return Result.ok(UploadContext.executeStrategy
                (multipartFile, DirectoryUriConstant.PHOTO));
    }

    @Operation(summary = "获取后台相册列表信息")
    @GetMapping("/admin/photos/albums/info")
    public Result<List<PhotoAlbumDTO>> listAllAdminPhotoAlbums() {
        return Result.ok(photoAlbumService.listAllAdminPhotoAlbums());
    }

    @OperationLog(type = LogConstant.REMOVE)
    @Operation(summary = "根据 ID 删除相册")
    @Parameter(name = "albumId", description = "相册 ID",
            required = true, schema = @Schema(type = "Integer"))
    @DeleteMapping("/admin/photos/albums/{albumId}")
    public Result<?> removePhotoAlbum(@PathVariable("albumId") Integer albumId) {
        photoAlbumService.removePhotoAlbum(albumId);
        return Result.ok();
    }

    @Operation(summary = "根据 ID 获取后台相册信息")
    @Parameter(name = "albumId", description = "相册 ID",
            required = true, schema = @Schema(type = "Integer"))
    @GetMapping("/admin/photos/albums/{albumId}/info")
    public Result<AdminPhotoAlbumDTO> getAdminPhotoAlbumDTO(@PathVariable("albumId") Integer albumId) {
        return Result.ok(photoAlbumService.getAdminPhotoAlbumDTO(albumId));
    }

}
