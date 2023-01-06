package pers.project.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pers.project.blog.dto.UniqueViewDTO;
import pers.project.blog.entity.UniqueViewEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 针对表【tb_unique_view】的数据库操作 Mapper
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
@Mapper
public interface UniqueViewMapper extends BaseMapper<UniqueViewEntity> {

    /**
     * 获取开始时间到结束时间的用户量信息
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 用户量信息
     */
    List<UniqueViewDTO> listUniqueViewDTOs(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

}




