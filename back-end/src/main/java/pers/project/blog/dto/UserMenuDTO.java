package pers.project.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户菜单
 *
 * @author Luo Fei
 * @date 2022/12/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMenuDTO {

    /**
     * 菜单名
     */
    private String name;

    /**
     * 菜单路径
     */
    private String path;

    /**
     * 前端组件
     */
    private String component;

    /**
     * 菜单 icon
     */
    private String icon;

    /**
     * 是否隐藏（0 否 1 是）
     */
    private Boolean hidden;

    // TODO: 2022/12/28 列表必须配置 @Schema，否则 API 无法显示
    /**
     * 子菜单列表
     */
    @Schema
    private List<UserMenuDTO> children;

}
