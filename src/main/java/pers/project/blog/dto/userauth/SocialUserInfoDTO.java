package pers.project.blog.dto.userauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社交用户信息
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserInfoDTO {

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

}
