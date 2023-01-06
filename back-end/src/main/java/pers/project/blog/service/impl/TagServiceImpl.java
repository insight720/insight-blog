package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.blog.entity.TagEntity;
import pers.project.blog.mapper.TagMapper;
import pers.project.blog.service.TagService;

/**
 * 针对表【tb_tag】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-30
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, TagEntity> implements TagService {

}




