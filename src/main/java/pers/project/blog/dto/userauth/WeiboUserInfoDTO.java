package pers.project.blog.dto.userauth;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微博用户信息
 *
 * @author Luo Fei
 * @version 2023/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeiboUserInfoDTO {

    /**
     * 昵称
     */
    @JSONField(alternateNames = {"nickname", "screen_name"})
    private String nickname;

    /**
     * 头像
     */
    @JSONField(alternateNames = {"avatar", "avatar_hd"})
    private String avatar;

}
