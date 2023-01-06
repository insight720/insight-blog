package pers.project.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志
 *
 * @author Luo Fei
 * @date 2023-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "tb_operation_log")
public class OperationLogEntity {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 操作模块
     */
    private String optModule;

    /**
     * 操作类型
     */
    private String optType;

    // TODO: 2023/1/1 名为 URL，实际上存储了 URI
    /**
     * 操作 URL
     */
    private String optUrl;

    /**
     * 操作方法
     */
    private String optMethod;

    /**
     * 操作描述
     */
    private String optDesc;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 返回数据
     */
    private String responseData;

    /**
     * 用户 ID
     */
    private Integer userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 操作 IP
     */
    private String ipAddress;

    /**
     * 操作地址
     */
    private String ipSource;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

}