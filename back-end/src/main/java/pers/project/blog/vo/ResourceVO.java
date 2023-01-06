package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 资源
 *
 * @author Luo Fei
 * @date 2023/1/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "资源")
public class ResourceVO {

    /**
     * 资源 ID
     */
    @Schema(name = "id", title = "资源 ID", type = "Integer")
    private Integer id;

    /**
     * 资源名
     */
    @NotBlank(message = "资源名不能为空")
    @Schema(name = "resourceName", title = "资源名", type = "String")
    private String resourceName;

    /**
     * 路径
     */
    @Schema(name = "url", title = "资源路径", type = "String")
    private String url;

    /**
     * 请求方式
     */
    @Schema(name = "url", title = "资源路径", type = "String")
    private String requestMethod;

    /**
     * 父资源 ID
     */
    @Schema(name = "parentId", title = "父资源 ID", type = "Integer")
    private Integer parentId;

    /**
     * 是否匿名访问
     */
    @Schema(name = "isAnonymous", title = "是否匿名访问", type = "Integer")
    private Integer isAnonymous;

}
