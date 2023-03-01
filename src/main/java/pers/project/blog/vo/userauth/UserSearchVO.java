package pers.project.blog.vo.userauth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户搜索数据
 *
 * @author Luo Fei
 * @date 2023/2/1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchVO {

    /**
     * 关键词
     */
    @Schema(description = "搜索内容")
    private String keywords;

    /**
     * 登录类型
     */
    @Schema(description = "登录类型")
    private Integer loginType;

}
