package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.dto.article.PageDTO;
import pers.project.blog.dto.tag.ManageTagDTO;
import pers.project.blog.dto.tag.TagDTO;
import pers.project.blog.entity.ArticleTag;
import pers.project.blog.entity.Tag;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.ArticleTagMapper;
import pers.project.blog.mapper.TagMapper;
import pers.project.blog.service.TagService;
import pers.project.blog.util.ConvertUtils;
import pers.project.blog.util.PageUtils;
import pers.project.blog.util.StrRegexUtils;
import pers.project.blog.vo.tag.TagVO;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static pers.project.blog.constant.CacheConst.TAG;
import static pers.project.blog.constant.GenericConst.ZERO_L;

/**
 * 针对表【tb_tag】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-30
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Resource
    private ArticleTagMapper articleTagMapper;

    @Override
    @Cacheable(cacheNames = TAG, key = "#root.methodName", sync = true,
            condition = "T(pers.project.blog.util.StrRegexUtils).isBlank(#keywords)")
    public List<TagDTO> listArticleTags(String keywords) {
        List<Tag> tagList = lambdaQuery()
                .select(Tag::getId, Tag::getTagName)
                .like(StrRegexUtils.isNotBlank(keywords),
                        Tag::getTagName, keywords)
                .orderByDesc(Tag::getId)
                .list();
        return ConvertUtils.convertList(tagList, TagDTO.class);
    }

    @Override
    public PageDTO<ManageTagDTO> listManageTags(String keywords) {
        // 查询符合条件的标签数量
        Long adminTagCount = lambdaQuery()
                .like(StrRegexUtils.isNotBlank(keywords),
                        Tag::getTagName, keywords)
                .count();
        if (adminTagCount.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        // 查询符合条件的分页标签列表
        List<ManageTagDTO> adminTagList = baseMapper
                .listAdminTags(PageUtils.offset(), PageUtils.size(), keywords);
        return PageUtils.build(adminTagList, adminTagCount);
    }

    @Override
    @CacheEvict(cacheNames = TAG, allEntries = true)
    public void saveOrUpdateTag(TagVO tagVO) {
        // 新建标签时，查询标签名是否已存在
        boolean save = tagVO.getId() == null;
        if (save) {
            boolean exists = lambdaQuery()
                    .eq(Tag::getTagName, tagVO.getTagName())
                    .exists();
            if (exists) {
                throw new ServiceException("标签名已存在");
            }
        }
        saveOrUpdate(ConvertUtils.convert(tagVO, Tag.class));
    }

    @Override
    @CacheEvict(cacheNames = TAG, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public void removeTags(List<Integer> tagIdList) {
        // 查询标签下是否有文章
        boolean exists = new LambdaQueryChainWrapper<>(articleTagMapper)
                .in(ArticleTag::getTagId, tagIdList)
                .exists();
        if (exists) {
            throw new ServiceException("删除失败，该标签下存在文章");
        }
        removeBatchByIds(tagIdList);
    }

    @Override
    @Cacheable(cacheNames = TAG, key = "#root.methodName", sync = true)
    public PageDTO<TagDTO> listTags() {
        Long tagCount = lambdaQuery().count();
        if (tagCount.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        List<Tag> tagList = lambdaQuery().list();
        List<TagDTO> tagDTOList = ConvertUtils.convertList(tagList, TagDTO.class);
        return PageUtils.build(tagDTOList, tagCount);
    }

    @Override
    @CacheEvict(cacheNames = TAG, allEntries = true)
    @Transactional(rollbackFor = Throwable.class)
    public List<Tag> saveAndGetNewTags(Collection<String> newTagNames) {
        List<Tag> newTagList = newTagNames
                .stream()
                .map(tagName -> Tag.builder()
                        .tagName(tagName)
                        .build())
                .collect(Collectors.toList());
        saveBatch(newTagList);
        return newTagList;
    }

    @Override
    public List<String> listArticleTagNames(Integer articleId) {
        return baseMapper.listArticleTagNames(articleId);
    }

}




