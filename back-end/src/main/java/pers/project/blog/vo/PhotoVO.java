package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 照片数据
 *
 * @author Luo Fei
 * @date 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "照片数据")
public class PhotoVO {

    /**
     * 相册 ID
     */
    @NotNull(message = "相册 ID 不能为空")
    @Schema(name = "id", title = "相册 ID", type = "Integer")
    private Integer albumId;

    /**
     * 照片 URL 列表
     */
    @Schema(name = "photoUrlList", title = "照片列表", type = "List<String>")
    private List<String> photoUrlList;

    /**
     * 照片 ID 列表
     */
    @Schema(name = "photoIdList", title = "照片 ID 列表", type = "List<Integer>")
    private List<Integer> photoIdList;

}
