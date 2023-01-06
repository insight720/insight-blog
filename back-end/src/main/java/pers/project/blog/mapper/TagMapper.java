package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.blog.entity.TagEntity;

/**
 * 针对表【tb_tag】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2022-12-30
 */
@Mapper
public interface TagMapper extends BaseMapper<TagEntity> {

}




