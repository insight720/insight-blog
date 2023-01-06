package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.OperationLogDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.entity.OperationLogEntity;
import pers.project.blog.vo.ConditionVO;

/**
 * 针对表【tb_operation_log】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2023-01-01
 */
public interface OperationLogService extends IService<OperationLogEntity> {

    /**
     * 查看操作日志
     *
     * @param conditionVO 查询条件
     * @return 分页的操作日志数据
     */
    PageDTO<OperationLogDTO> listOperationLogs(ConditionVO conditionVO);

}
