package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QQ 用户信息
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QQUserInfoDTO {

    /**
     * 昵称
     */
    private String nickname;

    // TODO: 2023/1/15 这名改一改
    /**
     * qq头像
     */
    private String figureurl_qq_1;

}
