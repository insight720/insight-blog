package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.project.blog.entity.ArticleTagEntity;
import pers.project.blog.mapper.ArticleTagMapper;
import pers.project.blog.service.ArticleTagService;

/**
 * 针对表【tb_article_tag】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2023-01-05
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTagEntity> implements ArticleTagService {

}




