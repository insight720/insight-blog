package pers.project.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.project.blog.dto.UniqueViewDTO;
import pers.project.blog.entity.UniqueViewEntity;

import java.util.List;

/**
 * 针对表【tb_unique_view】的数据库操作 Service
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
public interface UniqueViewService extends IService<UniqueViewEntity> {

    /**
     * 列出 7 天内的的访问量统计
     *
     * @return 访问量信息
     */
    List<UniqueViewDTO> listUniqueViewDTOs();

}
