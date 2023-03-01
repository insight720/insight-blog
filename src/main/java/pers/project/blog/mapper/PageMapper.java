package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.blog.entity.Page;

/**
 * 针对表【tb_page(页面)】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023-01-06
 */
@Mapper
public interface PageMapper extends BaseMapper<Page> {

}




