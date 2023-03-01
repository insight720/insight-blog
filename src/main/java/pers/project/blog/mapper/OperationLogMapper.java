package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.project.blog.entity.OperationLog;

/**
 * 针对表【tb_operation_log】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2023-01-01
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

}




