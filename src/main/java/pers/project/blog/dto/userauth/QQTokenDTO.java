package pers.project.blog.dto.userauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QQ Token 信息
 *
 * @author Luo Fei
 * @date 2023/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QQTokenDTO {

    // 不要改字段名

    /**
     * openId
     */
    private String openid;

    /**
     * 客户端 ID
     */
    private String client_id;

}
