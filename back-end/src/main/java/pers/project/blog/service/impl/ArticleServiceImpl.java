package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.project.blog.constant.*;
import pers.project.blog.constant.enumeration.ArticelStatusEnum;
import pers.project.blog.constant.enumeration.ArticleStatusEnum;
import pers.project.blog.constant.enumeration.FileExtensionEnum;
import pers.project.blog.dto.*;
import pers.project.blog.entity.ArticleEntity;
import pers.project.blog.entity.ArticleTagEntity;
import pers.project.blog.entity.CategoryEntity;
import pers.project.blog.entity.TagEntity;
import pers.project.blog.exception.FileUploadException;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.ArticleMapper;
import pers.project.blog.mapper.CategoryMapper;
import pers.project.blog.service.ArticleService;
import pers.project.blog.service.ArticleTagService;
import pers.project.blog.service.BlogInfoService;
import pers.project.blog.strategy.context.SearchContext;
import pers.project.blog.strategy.context.UploadContext;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.PaginationUtils;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.util.SecurityUtils;
import pers.project.blog.vo.ArticleTopVO;
import pers.project.blog.vo.ArticleVO;
import pers.project.blog.vo.ConditionVO;
import pers.project.blog.vo.TableLogicVO;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 针对表【tb_article】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleEntity> implements ArticleService {

    private final CategoryMapper categoryMapper;

    private final BlogInfoService blogInfoService;

    private final ArticleTagService articleTagService;

    private final TagServiceImpl tagService;

    private final TaskExecutor taskExecutor;

    private final HttpSession httpSession;

    public ArticleServiceImpl(CategoryMapper categoryMapper,
                              BlogInfoService blogInfoService,
                              ArticleTagService articleTagService,
                              TagServiceImpl tagService,
                              TaskExecutor taskExecutor,
                              HttpSession httpSession) {
        this.categoryMapper = categoryMapper;
        this.blogInfoService = blogInfoService;
        this.articleTagService = articleTagService;
        this.tagService = tagService;
        this.taskExecutor = taskExecutor;
        this.httpSession = httpSession;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdateArticle(ArticleVO articleVO) {
        ArticleEntity articleEntity
                = ConversionUtils.convertObject(articleVO, ArticleEntity.class);

        articleEntity.setUserId(SecurityUtils.getUserDetails().getUserInfoId());

        // 设置文章分类，草稿无分类
        CategoryEntity category = getCategory(articleVO);
        if (category != null) {
            articleEntity.setCategoryId(category.getId());
        }

        // 文章无封面则设置为默认封面
        if (StringUtils.isBlank(articleEntity.getArticleCover())) {
            String articleCover = blogInfoService.getWebSiteConfig().getArticleCover();
            articleEntity.setArticleCover(articleCover);
        }

        saveOrUpdate(articleEntity);

        saveOrUpdateArticleTagMap(articleVO, articleEntity.getId());
    }

    @Override
    public PageDTO<AdminArticleDTO> listAdminArticles(ConditionVO conditionVO) {
        // 查询后台文章总量
        Integer adminArticlesCount = baseMapper.countAdminArticles(conditionVO);
        if (adminArticlesCount.equals(0)) {
            return new PageDTO<>();
        }

        // 查询分页的后台文章
        IPage<ArticleEntity> page = PaginationUtils.getPage();
        List<AdminArticleDTO> adminArticleList = baseMapper
                .listAdminArticles(page.offset(), page.getSize(), conditionVO);

        // 查询并设置文章浏览量和点赞量
        Map<Object, Double> articleIdViewsCountMap = RedisUtils
                .zRangeWithScores(RedisConstant.ARTICLE_VIEWS_COUNT, 0, -1);
        Map<String, Object> articleIdLikeCountMap = RedisUtils
                .hGetAll(RedisConstant.ARTICLE_LIKE_COUNT);
        adminArticleList.forEach(adminArticle -> {
            Integer articleId = adminArticle.getId();
            Double viewsCount = articleIdViewsCountMap.get(articleId);
            if (viewsCount != null) {
                adminArticle.setViewsCount(viewsCount.intValue());
            }
            Integer likeCount = (Integer) articleIdLikeCountMap.get(articleId.toString());
            adminArticle.setLikeCount(likeCount);
        });

        return PageDTO.of(adminArticleList, adminArticlesCount);
    }

    @Override
    public void recoverOrRemoveArticleLogically(TableLogicVO tableLogicVO) {
        Integer isDelete = tableLogicVO.getIsDelete();
        List<ArticleEntity> articleList = tableLogicVO.getIdList()
                .stream()
                .map(articleId -> ArticleEntity.builder()
                        .id(articleId)
                        .isDelete(isDelete)
                        .build())
                .collect(Collectors.toList());
        updateBatchById(articleList);
    }

    @Override
    public void updateArticleTop(ArticleTopVO articleTopVO) {
        ArticleEntity article = ArticleEntity.builder()
                .id(articleTopVO.getId())
                .isTop(articleTopVO.getIsTop())
                .build();
        updateById(article);
    }

    @Override
    public ArticleVO getAdminArticle(Integer articleId) {
        ArticleEntity articleEntity = baseMapper.selectById(articleId);
        ArticleVO articleVO = ConversionUtils.convertObject
                (articleEntity, ArticleVO.class);

        CategoryEntity category = categoryMapper.selectById
                (articleEntity.getCategoryId());
        if (category != null) {
            articleVO.setCategoryName(category.getCategoryName());
        }

        List<String> tagNameList = tagService
                .getBaseMapper().listTagNamesByArticleId(articleId);
        articleVO.setTagNameList(tagNameList);

        return articleVO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void removeArticlePhysically(List<Integer> articleIdList) {
        articleTagService.lambdaUpdate()
                .in(ArticleTagEntity::getArticleId, articleIdList)
                .remove();

        baseMapper.deleteBatchIds(articleIdList);
    }

    @Override
    public List<HomePageArticleDTO> listHomePageArticles() {
        IPage<ArticleEntity> page = PaginationUtils.getPage();
        return baseMapper.listHomePageArticleDTOs(page.offset(), page.getSize());
    }

    @Override
    public ArticleDTO getArticle(Integer articleId) {
        // TODO: 2023/1/12 使用异步
        // 查询 6 篇推荐文章
        CompletableFuture<List<ArticleRecommendDTO>> recommendArticleList
                = CompletableFuture.supplyAsync(() ->
                baseMapper.listArticleRecommendArticles(articleId), taskExecutor);

        // 查询 5 篇最新文章
        CompletableFuture<List<ArticleRecommendDTO>> newestArticleList
                = CompletableFuture.supplyAsync(() -> {
            List<ArticleEntity> articleEntityList = lambdaQuery()
                    .select(ArticleEntity::getId,
                            ArticleEntity::getArticleTitle,
                            ArticleEntity::getArticleCover,
                            ArticleEntity::getCreateTime)
                    .eq(ArticleEntity::getStatus, ArticleStatusEnum.PUBLIC.getStatus())
                    .orderByDesc(ArticleEntity::getId)
                    .last("LIMIT 5")
                    .list();
            return ConversionUtils.covertList(articleEntityList, ArticleRecommendDTO.class);
        }, taskExecutor);

        // 查询 ID 对应文章
        ArticleDTO articleDTO = Optional.ofNullable(baseMapper.getArticleDTO(articleId))
                .orElseThrow(() -> new ServiceException("文章不存在"));

        // 更新文章浏览量
        updateArticleViewsCount(articleId);

        // 查询上一篇和下一篇文章
        ArticleEntity previousArticle = lambdaQuery()
                .select(ArticleEntity::getId,
                        ArticleEntity::getArticleTitle,
                        ArticleEntity::getArticleCover)
                .eq(ArticleEntity::getIsDelete, BooleanConstant.FALSE)
                .eq(ArticleEntity::getStatus, ArticleStatusEnum.PUBLIC.getStatus())
                .lt(ArticleEntity::getId, articleId)
                .orderByDesc(ArticleEntity::getId)
                .last("LIMIT 1")
                .one();
        ArticleEntity nextArticle = lambdaQuery()
                .select(ArticleEntity::getId,
                        ArticleEntity::getArticleTitle,
                        ArticleEntity::getArticleCover)
                .eq(ArticleEntity::getIsDelete, BooleanConstant.FALSE)
                .eq(ArticleEntity::getStatus, ArticleStatusEnum.PUBLIC.getStatus())
                .gt(ArticleEntity::getId, articleId)
                .last("LIMIT 1")
                .one();
        articleDTO.setLastArticle
                (ConversionUtils.convertObject(previousArticle, ArticlePaginationDTO.class));
        articleDTO.setNextArticle
                (ConversionUtils.convertObject(nextArticle, ArticlePaginationDTO.class));

        // 查询点赞量和浏览量
        Integer viewsCount = Optional
                .ofNullable(RedisUtils.zScore
                        (RedisConstant.ARTICLE_VIEWS_COUNT, articleId))
                .map(Double::intValue)
                .orElse(null);
        Integer likesCount = (Integer) RedisUtils.hGet
                (RedisConstant.ARTICLE_LIKE_COUNT, articleId.toString());
        articleDTO.setViewsCount(viewsCount);
        articleDTO.setLikeCount(likesCount);

        try {
            articleDTO.setNewestArticleList(newestArticleList.get());
            articleDTO.setRecommendArticleList(recommendArticleList.get());
        } catch (Exception cause) {
            // TODO: 2023/1/12 异常处理
            throw new RuntimeException("异步任务异常", cause);
        }

        return articleDTO;
    }

    @Override
    public List<String> exportArticles(List<Integer> articleIdList) {
        // 查询文章信息，上传文章，并获取文章 URL 列表
        return lambdaQuery()
                .select(ArticleEntity::getArticleTitle, ArticleEntity::getArticleContent)
                .in(ArticleEntity::getId, articleIdList)
                .list()
                .stream()
                .map(article -> {
                    byte[] contentBytes = article.getArticleContent().getBytes();
                    String fileName = article.getArticleTitle() + FileIoConstant.DOT
                            + FileExtensionEnum.MD.getExtensionName();
                    try (ByteArrayInputStream inputStream
                                 = new ByteArrayInputStream(contentBytes)) {
                        return UploadContext.executeStrategy
                                (inputStream, DirectoryUriConstant.MARKDOWN, fileName);
                    } catch (IOException cause) {
                        throw new FileUploadException("文章导出失败", cause);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public PageDTO<ArticleArchiveDTO> listArticleArchives() {
        IPage<ArticleEntity> page = lambdaQuery()
                .select(ArticleEntity::getId,
                        ArticleEntity::getArticleTitle,
                        ArticleEntity::getCreateTime)
                .eq(ArticleEntity::getIsDelete, BooleanConstant.FALSE)
                .eq(ArticleEntity::getStatus, ArticelStatusEnum.PUBLIC.getStatus())
                .orderByDesc(ArticleEntity::getCreateTime)
                .page(PaginationUtils.getPage());
        List<ArticleArchiveDTO> articleArchiveList
                = ConversionUtils.covertList(page.getRecords(), ArticleArchiveDTO.class);
        return PageDTO.of(articleArchiveList, (int) page.getTotal());
    }

    @Override
    public ArticlePreviewDTO getArticlePreview(ConditionVO conditionVO) {
        // 按条件查询文章数据
        IPage<ArticleEntity> page = PaginationUtils.getPage();
        List<PreviewDTO> previewList = baseMapper.listPreviewDTOs
                (page.offset(), page.getSize(), conditionVO);

        // 按对应条件搜索（分类名或标签名）
        String name = Optional.ofNullable(conditionVO.getCategoryId())
                .map(categoryId -> new LambdaQueryChainWrapper<>(categoryMapper)
                        .select(CategoryEntity::getCategoryName)
                        .eq(CategoryEntity::getId, conditionVO.getCategoryId())
                        .one()
                        .getCategoryName())
                .orElseGet(() -> tagService.lambdaQuery()
                        .select(TagEntity::getTagName)
                        .eq(TagEntity::getId, conditionVO.getTagId())
                        .one()
                        .getTagName());

        return ArticlePreviewDTO.builder()
                .articlePreviewDTOList(previewList)
                .name(name)
                .build();
    }

    @Override
    public List<ArticleSearchDTO> listArticlesBySearch(ConditionVO conditionVO) {
        return SearchContext.executeStrategy(conditionVO.getKeywords());
    }

    @Override
    public void saveArticleLike(Integer articleId) {
        // 判断是否点过赞来区分点赞和取消赞
        String articleLikeKey = RedisConstant.ARTICLE_USER_LIKE + SecurityUtils.getUserDetails().getUserInfoId();
        if (RedisUtils.sIsMember(articleLikeKey, articleId)) {
            RedisUtils.sRem(articleLikeKey, articleId);
            RedisUtils.hIncrBy(RedisConstant.ARTICLE_LIKE_COUNT, articleId.toString(), -1L);
        } else {
            RedisUtils.sAdd(articleLikeKey, articleId);
            RedisUtils.hIncrBy(RedisConstant.ARTICLE_LIKE_COUNT, articleId.toString(), 1L);
        }
    }

    /**
     * 更新文章浏览量
     *
     * @param articleId 文章 ID
     */
    @SuppressWarnings("unchecked")
    private void updateArticleViewsCount(Integer articleId) {
        // TODO: 2023/1/12 逻辑不明
        // 判断是否第一次访问，是则增加浏览量
        Set<Integer> articleSet = (Set<Integer>) Optional
                .ofNullable(httpSession.getAttribute(WebsiteConstant.ARTICLE_SET))
                .orElseGet(HashSet::new);
        if (articleSet.add(articleId)) {
            httpSession.setAttribute(WebsiteConstant.ARTICLE_SET, articleSet);
            RedisUtils.zIncyBy(RedisConstant.ARTICLE_VIEWS_COUNT, articleId, 1D);
        }
    }

    /**
     * 保存或更新文章和标签的映射
     *
     * @param articleVO 文章信息 <p><b>注意：ID 若不为空则与 {@code articleId} 相同，是更新操作</b>
     * @param articleId 文章 ID，保存或更新的文章 ID
     */
    private void saveOrUpdateArticleTagMap(ArticleVO articleVO, Integer articleId) {
        // 请求携带的标签名
        Optional<HashSet<String>> tagNameSetOption = Optional
                .ofNullable(articleVO.getTagNameList())
                .map(HashSet::new)
                .filter(CollectionUtils::isNotEmpty);

        // 原有的并且标签名由请求携带的标签 ID 和 标签名映射
        Optional<HashMap<Integer, String>> previousTagIdNameMapOption = tagNameSetOption
                .map(tagNameSet -> tagService.lambdaQuery()
                        .in(TagEntity::getTagName, tagNameSet)
                        .list().stream()
                        .collect(Collectors.toMap
                                (TagEntity::getId, TagEntity::getTagName,
                                        (tagName, sameTagName) -> tagName, HashMap::new)))
                .filter(CollectionUtils::isNotEmpty);

        // 新的标签名
        tagNameSetOption.ifPresent(tagNameSet -> tagNameSet.removeAll
                (previousTagIdNameMapOption.map(HashMap::values).orElseGet(HashSet::new)));

        // 原有的标签 ID
        Optional<Set<Integer>> tagIdSetOption = previousTagIdNameMapOption.map(HashMap::keySet);

        // 如果有新的标签名，保存新标签，并加入新标签的 ID
        tagNameSetOption
                .ifPresent(nameSet -> {
                    List<TagEntity> newTagList = nameSet
                            .stream()
                            .map(tagName -> TagEntity.builder()
                                    .tagName(tagName)
                                    .build())
                            .collect(Collectors.toList());
                    tagService.saveBatch(newTagList);

                    List<Integer> newTagIdList = newTagList.stream()
                            .map(TagEntity::getId)
                            .collect(Collectors.toList());
                    tagIdSetOption.ifPresent(tagIdSet -> tagIdSet.addAll(newTagIdList));
                });

        // 更新操作，删除原有的文章和标签映射
        Optional.ofNullable(articleVO.getId())
                .ifPresent(previousArticleId -> articleTagService
                        .lambdaUpdate()
                        .eq(ArticleTagEntity::getArticleId, previousArticleId)
                        .remove());

        // 保存新的文章和标签映射
        tagIdSetOption
                .map(Collection::stream)
                .map(tagIdStream -> tagIdStream
                        .map(tagId -> ArticleTagEntity.builder()
                                .articleId(articleId)
                                .tagId(tagId)
                                .build())
                        .collect(Collectors.toList()))
                .ifPresent(articleTagService::saveBatch);
    }

    /**
     * 获取文章分类
     *
     * @param articleVO 文章数据
     * @return 文章分类，不存在分类的草稿返回 null
     */
    @Nullable
    private CategoryEntity getCategory(@NotNull ArticleVO articleVO) {
        // 判断分类是否存在，存在则返回该分类，不存在且文章不是草稿则创建新分类
        return Optional
                .ofNullable(new LambdaQueryChainWrapper<>(categoryMapper)
                        .eq(CategoryEntity::getCategoryName, articleVO.getCategoryName())
                        .one())
                .orElseGet(() -> {
                    if (articleVO.getStatus().equals(ArticelStatusEnum.DRAFT.getStatus())) {
                        return null;
                    }
                    CategoryEntity category = CategoryEntity.builder()
                            .categoryName(articleVO.getCategoryName())
                            .build();
                    categoryMapper.insert(category);
                    return category;
                });
    }

}




