package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.blog.entity.Photo;

/**
 * 针对表【tb_photo(照片)】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @version 2023-01-10
 */
@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {

}




