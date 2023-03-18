package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.operationlog.OperationLogDTO;
import pers.project.blog.entity.OperationLog;
import pers.project.blog.mapper.OperationLogMapper;
import pers.project.blog.service.OperationLogService;
import pers.project.blog.util.ConvertUtils;
import pers.project.blog.util.PageUtils;
import pers.project.blog.util.StrRegexUtils;

import java.util.List;

/**
 * 针对表【tb_operation_log】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @version 2023-01-01
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public PageDTO<OperationLogDTO> listOperationLogs(String keywords) {
        // 查询符合条件的分页操作日志
        IPage<OperationLog> operationLogPage = lambdaQuery()
                .like(StrRegexUtils.isNotBlank(keywords),
                        OperationLog::getOptModule, keywords)
                .or().like(StrRegexUtils.isNotBlank(keywords),
                        OperationLog::getOptDesc, keywords)
                .orderByDesc(OperationLog::getId)
                .page(PageUtils.getPage());
        // 组装分页数据
        List<OperationLogDTO> operationLogDTOList = ConvertUtils.convertList
                (operationLogPage.getRecords(), OperationLogDTO.class);
        return PageUtils.build(operationLogDTOList, operationLogPage.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void removeOperationLogs(List<Integer> operationLogIdList) {
        removeBatchByIds(operationLogIdList);
    }

}




