package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签
 *
 * @author Luo Fei
 * @date 2022/12/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {

    /**
     * ID
     */
    private Integer id;

    /**
     * 标签名
     */
    private String tagName;

}
