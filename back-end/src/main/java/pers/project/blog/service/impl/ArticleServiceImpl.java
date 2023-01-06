package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.blog.entity.ArticleEntity;
import pers.project.blog.mapper.ArticleMapper;
import pers.project.blog.service.ArticleService;

/**
 * 针对表【tb_article】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleEntity> implements ArticleService {

}




