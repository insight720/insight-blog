package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.blog.entity.CategoryEntity;
import pers.project.blog.mapper.CategoryMapper;
import pers.project.blog.service.CategoryService;

/**
 * 针对表【tb_category】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-30
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

}




