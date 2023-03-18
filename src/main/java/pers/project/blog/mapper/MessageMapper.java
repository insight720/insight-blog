package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.blog.entity.Message;

/**
 * 针对表【tb_message】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @version 2022-12-29
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}




