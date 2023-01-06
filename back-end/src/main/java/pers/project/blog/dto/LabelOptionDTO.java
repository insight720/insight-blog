package pers.project.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 标签选项
 *
 * @author Luo Fei
 * @date 2023/1/2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelOptionDTO {

    /**
     * 选项 ID
     */
    private Integer id;

    /**
     * 选项名
     */
    private String label;

    /**
     * 子选项
     */
    @Schema
    private List<LabelOptionDTO> children;

}
