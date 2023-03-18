package pers.project.blog.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文章预览数据集
 *
 * @author Luo Fei
 * @version 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticlePreviewDTO {

    /**
     * 文章预览数据列表
     */
    private List<PreviewDTO> articlePreviewDTOList;

    /**
     * 条件名
     */
    private String name;

}
