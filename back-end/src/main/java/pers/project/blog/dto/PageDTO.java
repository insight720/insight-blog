package pers.project.blog.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页视图对象
 *
 * @author Luo Fei
 * @date 2022/12/31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {

    /**
     * 分页列表
     */
    @Schema(name = "recordList", description = "分页列表", type = "List<T>")
    private List<T> recordList;

    /**
     * 总数
     */
    @Schema(name = "count", description = "总数", type = "Integer")
    private Integer count;

    /**
     * 由分页 Page 对象返回 PageVO
     *
     * @param page 分页 Page 对象
     * @return PageVO
     */
    public static <T> PageDTO<T> of(IPage<T> page) {
        return PageDTO.<T>builder()
                .recordList(page.getRecords())
                .count((int) page.getTotal()).build();
    }

    /**
     * 由分页数据返回 PageVO
     *
     * @param tList 分页数据列表
     * @param total 符合条件的条数
     * @return PageVO
     */
    public static <T> PageDTO<T> of(List<T> tList, Integer total) {
        return PageDTO.<T>builder()
                .recordList(tList)
                .count(total).build();
    }

}
