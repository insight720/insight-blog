package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pers.project.blog.dto.AdminTagDTO;
import pers.project.blog.dto.PageDTO;
import pers.project.blog.dto.TagDTO;
import pers.project.blog.entity.ArticleTagEntity;
import pers.project.blog.entity.TagEntity;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.ArticleTagMapper;
import pers.project.blog.mapper.TagMapper;
import pers.project.blog.service.TagService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.TagVO;

import java.util.List;

/**
 * 针对表【tb_tag】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-30
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, TagEntity> implements TagService {

    private final ArticleTagMapper articleTagMapper;

    public TagServiceImpl(ArticleTagMapper articleTagMapper) {
        this.articleTagMapper = articleTagMapper;
    }

    @Override
    public List<TagDTO> listTagsBySearch(ConditionVO conditionVO) {
        List<TagEntity> tagEntityList = lambdaQuery()
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()),
                        TagEntity::getTagName, conditionVO.getKeywords())
                .orderByDesc(TagEntity::getId)
                .list();
        return ConversionUtils.covertList(tagEntityList, TagDTO.class);
    }

    @Override
    public PageDTO<AdminTagDTO> listAdminTags(ConditionVO conditionVO) {
        // 查询符合条件的标签数量
        Long adminTagCount = lambdaQuery()
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()),
                        TagEntity::getTagName, conditionVO.getKeywords())
                .count();
        if (adminTagCount == 0) {
            return new PageDTO<>();
        }

        // 查询符合条件的分页标签列表
        IPage<TagEntity> page = PaginationUtils.getPage();
        List<AdminTagDTO> adminTagDTOList = baseMapper
                .listAdminTags(page.offset(), page.getSize(), conditionVO);

        return PageDTO.of(adminTagDTOList, adminTagCount.intValue());
    }

    @Override
    public void saveOrUpdateTag(TagVO tagVO) {
        // 查询标签名是否已存在
        TagEntity tagEntity = lambdaQuery()
                .select(TagEntity::getId)
                .eq(TagEntity::getTagName, tagVO.getTagName())
                .one();
        if (tagEntity != null && !tagEntity.getId().equals(tagVO.getId())) {
            throw new ServiceException("标签名已存在");
        }

        saveOrUpdate(ConversionUtils.convertObject(tagVO, TagEntity.class));
    }

    @Override
    public void removeTags(List<Integer> tagIdList) {
        // 查询标签下是否有文章
        Long articleCount = new LambdaQueryChainWrapper<>(articleTagMapper)
                .in(ArticleTagEntity::getTagId, tagIdList)
                .count();
        if (articleCount > 0) {
            throw new ServiceException("删除失败，该标签下存在文章");
        }

        removeBatchByIds(tagIdList);
    }

    @Override
    public PageDTO<TagDTO> listTags() {
        List<TagEntity> tagEntityList = lambdaQuery().list();
        int tagCount = lambdaQuery().count().intValue();
        List<TagDTO> tagDTOList = ConversionUtils.covertList(tagEntityList, TagDTO.class);
        return PageDTO.of(tagDTOList, tagCount);
    }

}




