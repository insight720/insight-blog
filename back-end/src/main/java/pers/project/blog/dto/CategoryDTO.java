package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台目录分类
 *
 * @author Luo Fei
 * @date 2022/12/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    /**
     * ID
     */
    private Integer id;

    /**
     * 分类名
     */
    private String categoryName;

    /**
     * 分类下的文章数量
     */
    private Integer articleCount;

}
