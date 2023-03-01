package pers.project.blog.vo.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 资源数据
 *
 * @author Luo Fei
 * @date 2023/1/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceVO {

    /**
     * 资源 ID
     */
    @Schema(description = "资源 ID")
    private Integer id;

    /**
     * 资源名
     */
    @NotBlank(message = "资源名不能为空")
    @Schema(description = "资源名")
    private String resourceName;

    /**
     * 资源 URI
     */
    @Schema(description = "资源路径")
    private String url;

    /**
     * 请求方式
     */
    @Schema(description = "资源路径")
    private String requestMethod;

    /**
     * 父资源 ID
     */
    @Schema(description = "父资源 ID")
    private Integer parentId;

    /**
     * 是否匿名访问（0 否 1 是）
     */
    @Schema(description = "是否匿名访问")
    private Integer isAnonymous;

}
