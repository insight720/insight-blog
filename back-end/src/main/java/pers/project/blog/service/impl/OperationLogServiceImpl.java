package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pers.project.blog.dto.OperationLogDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.entity.OperationLogEntity;
import pers.project.blog.mapper.OperationLogMapper;
import pers.project.blog.service.OperationLogService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.vo.ConditionVO;

import java.util.List;

/**
 * 针对表【tb_operation_log】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-01
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLogEntity> implements OperationLogService {

    @Override
    public PageDTO<OperationLogDTO> listOperationLogs(ConditionVO conditionVO) {
        // 查询符合条件的分页操作日志
        String keywords = conditionVO.getKeywords();
        IPage<OperationLogEntity> operationLogPage = lambdaQuery()
                .like(StringUtils.hasText(keywords),
                        OperationLogEntity::getOptModule, keywords)
                .or().like(StringUtils.hasText(keywords),
                        OperationLogEntity::getOptDesc, keywords)
                .orderByDesc(OperationLogEntity::getId)
                .page(PaginationUtils.getPage());

        // 组装分页数据
        List<OperationLogDTO> operationLogDTOList = ConversionUtils.covertList
                (operationLogPage.getRecords(), OperationLogDTO.class);
        return PageDTO.of(operationLogDTOList, (int) operationLogPage.getTotal());
    }

}




