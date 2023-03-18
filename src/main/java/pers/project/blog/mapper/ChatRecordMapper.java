package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.blog.entity.ChatRecord;

/**
 * 针对表【tb_chat_record】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @version 2023-01-13
 */
@Mapper
public interface ChatRecordMapper extends BaseMapper<ChatRecord> {

}




