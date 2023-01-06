package pers.project.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访问量信息
 *
 * @author Luo Fei
 * @date 2022/12/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UniqueViewDTO {

    /**
     * 日期
     */
    private String day;

    /**
     * 访问量
     */
    private Integer viewsCount;

}
