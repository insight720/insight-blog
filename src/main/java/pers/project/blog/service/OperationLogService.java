package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.operationlog.OperationLogDTO;
import pers.project.blog.entity.OperationLog;

import java.util.List;

/**
 * 针对表【tb_operation_log】的数据库操作 Service
 *
 * @author Luo Fei
 * @version 2023-01-01
 */
public interface OperationLogService extends IService<OperationLog> {

    /**
     * 获取分页的操作日志数据
     */
    PageDTO<OperationLogDTO> listOperationLogs(String keywords);

    /**
     * 删除操作日志
     */
    void removeOperationLogs(List<Integer> operationLogIdList);

}
