package pers.project.blog.dto.userauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社交登录 Token 信息
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialTokenDTO {

    /**
     * 开放 ID
     */
    private String openId;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 登录类型
     */
    private Integer loginType;

    /**
     * 社交用户信息
     */
    private SocialUserInfoDTO socialUserInfo;

}
