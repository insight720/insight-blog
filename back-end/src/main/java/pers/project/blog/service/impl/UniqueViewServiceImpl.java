package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.blog.constant.TimeConstant;
import pers.project.blog.dto.UniqueViewDTO;
import pers.project.blog.entity.UniqueViewEntity;
import pers.project.blog.mapper.UniqueViewMapper;
import pers.project.blog.service.UniqueViewService;
import pers.project.blog.util.TimeUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 针对表【tb_unique_view】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
@Service
public class UniqueViewServiceImpl extends ServiceImpl<UniqueViewMapper, UniqueViewEntity> implements UniqueViewService {

    @Override
    public List<UniqueViewDTO> listUniqueViewDTOs() {
        LocalDateTime currentTime = TimeUtils.now();
        LocalDateTime beginTime = TimeUtils.beginOfDay
                (TimeUtils.offset(currentTime, TimeConstant.OFFSET, ChronoUnit.DAYS));
        LocalDateTime endTime = TimeUtils.endOfDay(currentTime);
        return baseMapper.listUniqueViewDTOs(beginTime, endTime);
    }

}




