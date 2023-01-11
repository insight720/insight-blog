package pers.project.blog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 查询条件
 *
 * @author Luo Fei
 * @date 2022/12/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "查询条件")
public class ConditionVO {

    // TODO: 2022/12/31 页码接收但没有使用
    /**
     * 页码
     */
    @Schema(name = "current", title = "页码", type = "Long")
    private Long current;

    /**
     * 条数
     */
    @Schema(name = "size", title = "条数", type = "Long")
    private Long size;

    /**
     * 搜索内容
     */
    @Schema(name = "keywords", title = "搜索内容", type = "String")
    private String keywords;

    /**
     * 分类 ID
     */
    @Schema(name = "categoryId", title = "分类 ID", type = "Integer")
    private Integer categoryId;

    /**
     * 标签 ID
     */
    @Schema(name = "tagId", title = "标签 ID", type = "Integer")
    private Integer tagId;

    /**
     * 相册 ID
     */
    @Schema(name = "albumId", title = "相册 ID", type = "Integer")
    private Integer albumId;

    /**
     * 登录类型
     */
    @Schema(name = "type", title = "登录类型", type = "Integer")
    private Integer loginType;

    /**
     * 类型
     */
    @Schema(name = "type", title = "类型", type = "Integer")
    private Integer type;

    /**
     * 状态
     */
    @Schema(name = "status", title = "状态", type = "Integer")
    private Integer status;

    /**
     * 开始时间
     */
    @Schema(name = "startTime", title = "开始时间", type = "LocalDateTime")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Schema(name = "endTime", title = "结束时间", type = "LocalDateTime")
    private LocalDateTime endTime;

    /**
     * 是否删除（0 否 1 是）
     */
    @Schema(name = "isDelete", title = "是否删除", type = "Integer")
    private Integer isDelete;

    /**
     * 是否审核
     */
    @Schema(name = "isReview", title = "是否审核", type = "Integer")
    private Integer isReview;

}
