package pers.project.blog.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章列表分类选项数据
 *
 * @author Luo Fei
 * @date 2023/1/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryOptionDTO {

    /**
     * 分类 ID
     */
    private Integer id;

    /**
     * 分类名
     */
    private String categoryName;

}
