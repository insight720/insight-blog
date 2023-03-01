package pers.project.blog.dto.userauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微博 Token 信息
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeiboTokenDTO {

    // 不要修改字段名

    /**
     * 微博 uid
     */
    private String uid;

    /**
     * 访问令牌
     */
    private String access_token;

}
