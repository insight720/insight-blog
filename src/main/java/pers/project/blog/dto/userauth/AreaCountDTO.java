package pers.project.blog.dto.userauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地域分布数据
 *
 * @author Luo Fei
 * @version 2022/12/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaCountDTO {

    /**
     * 地区名
     */
    private String name;

    /**
     * 数量
     */
    private Long value;

}
