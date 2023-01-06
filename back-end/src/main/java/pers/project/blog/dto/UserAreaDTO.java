package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户 IP 属地
 *
 * @author Luo Fei
 * @date 2022/12/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAreaDTO {

    /**
     * 地区名
     */
    private String name;

    /**
     * 数量
     */
    private Long value;

}
