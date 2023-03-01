package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.constant.FilePathConst;
import pers.project.blog.constant.GenericConst;
import pers.project.blog.dto.article.*;
import pers.project.blog.dto.bloginfo.WebsiteConfig;
import pers.project.blog.entity.Article;
import pers.project.blog.entity.ArticleTag;
import pers.project.blog.entity.Category;
import pers.project.blog.entity.Tag;
import pers.project.blog.enums.FileExtensionEnum;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.ArticleMapper;
import pers.project.blog.mapper.CategoryMapper;
import pers.project.blog.service.ArticleService;
import pers.project.blog.service.ArticleTagService;
import pers.project.blog.service.TagService;
import pers.project.blog.strategy.context.ImportContext;
import pers.project.blog.strategy.context.SearchContext;
import pers.project.blog.strategy.context.UploadContext;
import pers.project.blog.util.*;
import pers.project.blog.vo.article.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static pers.project.blog.constant.DatabaseConst.LIMIT_1;
import static pers.project.blog.constant.DatabaseConst.LIMIT_5;
import static pers.project.blog.constant.GenericConst.*;
import static pers.project.blog.constant.RedisConst.*;
import static pers.project.blog.constant.WebsiteConst.ARTICLE_SET;
import static pers.project.blog.enums.ArticelStateEnum.DRAFT;
import static pers.project.blog.enums.ArticelStateEnum.PUBLIC;

/**
 * 针对表【tb_article】的数据库操作 Service 实现
 *
 * @author Luo Fei
 * @date 2022-12-29
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private TagService tagService;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private ArticleTagService articleTagService;

    @Override
    public PageDTO<AdminArticleDTO> listAdminArticles(ArticleSearchVO articleSearchVO) {
        // 查询符合条件的后台文章总量
        Long adminArticlesCount = baseMapper.countAdminArticles(articleSearchVO);
        if (adminArticlesCount.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        // 查询分页的后台文章
        long offset = PageUtils.offset(), size = PageUtils.size();
        CompletableFuture<List<AdminArticleDTO>> future = AsyncUtils.supplyAsync
                (() -> baseMapper.listAdminArticles(offset, size, articleSearchVO));
        // 查询并设置文章浏览量和点赞量
        Map<Object, Double> articleIdViewsCountMap = RedisUtils
                .zRangeWithScores(ARTICLE_VIEWS_COUNT, ZERO, -ONE);
        Map<String, Object> articleIdLikeCountMap = RedisUtils
                .hGetAll(ARTICLE_LIKE_COUNT);
        // 获取文章列表数据查询结果
        List<AdminArticleDTO> adminArticleList = AsyncUtils.get
                (future, "查询分页的后台文章列表数据异常");
        // 封装文章数据
        for (AdminArticleDTO adminArticle : adminArticleList) {
            Integer articleId = adminArticle.getId();
            Double viewsCount = articleIdViewsCountMap.get(articleId);
            if (viewsCount != null) {
                adminArticle.setViewsCount(viewsCount.intValue());
            }
            Integer likeCount = (Integer) articleIdLikeCountMap.get(articleId.toString());
            adminArticle.setLikeCount(likeCount);
        }
        return PageUtils.build(adminArticleList, adminArticlesCount);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void saveOrUpdateArticle(ArticleVO articleVO) {
        Article article = ConvertUtils.convert(articleVO, Article.class);
        // 设置文章所属用户信息 ID
        article.setUserId(SecurityUtils.getUserInfoId());
        // 设置文章分类，草稿无分类
        Category category = getCategory(articleVO);
        if (category != null) {
            article.setCategoryId(category.getId());
        }
        // 文章无封面则设置为默认封面
        if (StringUtils.isBlank(article.getArticleCover())) {
            String articleCover = ConfigUtils.getCache(WebsiteConfig::getArticleCover);
            article.setArticleCover(articleCover);
        }
        // 保存或更新文章
        saveOrUpdate(article);
        // 保存或更新文章和标签的映射
        saveOrUpdateArticleTagMap(articleVO, article.getId());
    }

    @Override
    public void recoverOrRemoveArticlesLogically(TableLogicVO tableLogicVO) {
        Integer isDelete = tableLogicVO.getIsDelete();
        List<Article> articleList = tableLogicVO.getIdList()
                .stream()
                .map(articleId -> Article.builder()
                        .id(articleId)
                        .isDelete(isDelete)
                        .build())
                .collect(Collectors.toList());
        updateBatchById(articleList);
    }

    @Override
    public void updateArticleTop(ArticleTopVO articleTopVO) {
        Article article = Article.builder()
                .id(articleTopVO.getId())
                .isTop(articleTopVO.getIsTop())
                .build();
        updateById(article);
    }

    @Override
    public ArticleVO getAdminArticle(Integer articleId) {
        // 获取文章数据
        Article article = baseMapper.selectById(articleId);
        ArticleVO articleVO = ConvertUtils.convert
                (article, ArticleVO.class);
        // 获取分类数据
        Category category = categoryMapper.selectById
                (article.getCategoryId());
        if (category != null) {
            articleVO.setCategoryName(category.getCategoryName());
        }
        // 获取标签数据
        List<String> tagNameList = tagService.listArticleTagNames(articleId);
        articleVO.setTagNameList(tagNameList);
        return articleVO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void removeArticlesPhysically(List<Integer> articleIdList) {
        articleTagService.lambdaUpdate()
                .in(ArticleTag::getArticleId, articleIdList)
                .remove();
        baseMapper.deleteBatchIds(articleIdList);
    }

    @Override
    public List<String> exportArticles(List<Integer> articleIdList) {
        // 查询文章信息，上传文章，并获取文章 URL 列表
        return lambdaQuery()
                .select(Article::getArticleTitle, Article::getArticleContent)
                .in(Article::getId, articleIdList)
                .list().stream()
                .map(article -> {
                    byte[] contentBytes = article.getArticleContent().getBytes();
                    String fileName = article.getArticleTitle() + FilePathConst.DOT
                            + FileExtensionEnum.MD.getExtensionName();
                    try (ByteArrayInputStream inputStream
                                 = new ByteArrayInputStream(contentBytes)) {
                        return UploadContext.executeStrategy
                                (inputStream, FilePathConst.MARKDOWN_DIR, fileName);
                    } catch (IOException cause) {
                        throw new ServiceException("文章导出失败", cause);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public String uploadArticleImages(MultipartFile multipartFile) {
        return UploadContext.executeStrategy
                (multipartFile, FilePathConst.ARTICLE_DIR);
    }

    @Override
    public void importArticle(MultipartFile multipartFile, String strategyName) {
        ImportContext.executeStrategy(multipartFile, strategyName);
    }

    @Override
    public List<HomePageArticleDTO> listHomePageArticles() {
        return baseMapper.listHomePageArticles(PageUtils.offset(), PageUtils.size());
    }

    @Override
    public PageDTO<ArticleArchiveDTO> listArticleArchives() {
        IPage<Article> page = lambdaQuery()
                .select(Article::getId,
                        Article::getArticleTitle,
                        Article::getCreateTime)
                .eq(Article::getIsDelete, GenericConst.FALSE_OF_INT)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .orderByDesc(Article::getCreateTime)
                .page(PageUtils.getPage());
        List<ArticleArchiveDTO> articleArchiveList
                = ConvertUtils.convertList(page.getRecords(), ArticleArchiveDTO.class);
        return PageUtils.build(articleArchiveList, page.getTotal());
    }

    @Override
    public ArticleDTO getArticle(Integer articleId) {
        // 更新文章浏览量
        HttpSession session = WebUtils.getCurrentSession();
        AsyncUtils.runAsync(() -> updateArticleViewsCount(session, articleId));
        // 查询 6 篇推荐文章
        CompletableFuture<List<ArticleRecommendDTO>> recommendFuture = AsyncUtils
                .supplyAsync(() -> baseMapper.listArticleRecommendArticles(articleId));
        // 查询 5 篇最新文章
        CompletableFuture<List<ArticleRecommendDTO>> newestFuture
                = AsyncUtils.supplyAsync(() -> {
            List<Article> articleList = lambdaQuery()
                    .select(Article::getId,
                            Article::getArticleTitle,
                            Article::getArticleCover,
                            Article::getCreateTime)
                    .eq(Article::getStatus, PUBLIC.getStatus())
                    .orderByDesc(Article::getId)
                    .last(LIMIT_5)
                    .list();
            return ConvertUtils.convertList(articleList, ArticleRecommendDTO.class);
        });
        // 查询 ID 对应的文章数据
        ArticleDTO articleDTO = baseMapper.getArticle(articleId);
        // 查询上一篇和下一篇文章数据
        Article previousArticle = lambdaQuery()
                .select(Article::getId,
                        Article::getArticleTitle,
                        Article::getArticleCover)
                .eq(Article::getIsDelete, FALSE_OF_INT)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .lt(Article::getId, articleId)
                .orderByDesc(Article::getId)
                // 不能少
                .last(LIMIT_1)
                .one();
        Article nextArticle = lambdaQuery()
                .select(Article::getId,
                        Article::getArticleTitle,
                        Article::getArticleCover)
                .eq(Article::getIsDelete, FALSE_OF_INT)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .gt(Article::getId, articleId)
                .last(LIMIT_1)
                .one();
        articleDTO.setLastArticle
                (ConvertUtils.convert(previousArticle, ArticlePaginationDTO.class));
        articleDTO.setNextArticle
                (ConvertUtils.convert(nextArticle, ArticlePaginationDTO.class));
        // 查询点赞量和浏览量
        Integer viewsCount = Optional
                .ofNullable(RedisUtils.zScore(ARTICLE_VIEWS_COUNT, articleId))
                .map(Double::intValue)
                .orElse(ZERO);
        Integer likesCount = Optional
                .ofNullable((Integer) RedisUtils.hGet
                        (ARTICLE_LIKE_COUNT, articleId.toString()))
                .orElse(ZERO);
        articleDTO.setViewsCount(viewsCount);
        articleDTO.setLikeCount(likesCount);
        // 获取推荐文章和最新文章查询结果
        List<ArticleRecommendDTO> recommendArticleList
                = AsyncUtils.get(recommendFuture, "查询 6 篇推荐文章异常");
        List<ArticleRecommendDTO> newestArticleList
                = AsyncUtils.get(newestFuture, "查询 5 篇最新文章异常");
        articleDTO.setNewestArticleList(newestArticleList);
        articleDTO.setRecommendArticleList(recommendArticleList);
        return articleDTO;
    }

    @Override
    public ArticlePreviewDTO getArticlePreview(ArticlePreviewVO articlePreviewVO) {
        // 获取分类或标签文章预览数据
        long offset = PageUtils.offset(), size = PageUtils.size();
        CompletableFuture<List<PreviewDTO>> previewFuture = AsyncUtils.supplyAsync
                (() -> baseMapper.listPreviewDTOs(offset, size, articlePreviewVO));
        // 获取分类名或标签名）
        String name = Optional.ofNullable(articlePreviewVO.getCategoryId())
                .map(categoryId -> new LambdaQueryChainWrapper<>(categoryMapper)
                        .select(Category::getCategoryName)
                        .eq(Category::getId, articlePreviewVO.getCategoryId())
                        .one()
                        .getCategoryName())
                .orElseGet(() -> tagService.lambdaQuery()
                        .select(Tag::getTagName)
                        .eq(Tag::getId, articlePreviewVO.getTagId())
                        .one()
                        .getTagName());
        // 获取文章预览数据查询结果
        List<PreviewDTO> previewDTOList = AsyncUtils.get
                (previewFuture, "获取分类或标签文章预览数据异常");
        return ArticlePreviewDTO.builder()
                .articlePreviewDTOList(previewDTOList)
                .name(name)
                .build();
    }

    @Override
    public List<ArticleSearchDTO> listArticlesBySearch(String keywords) {
        return SearchContext.executeStrategy(keywords);
    }

    @Override
    public void likeArticle(Integer articleId) {
        // 判断是否点过赞来区分点赞和取消赞
        RedisUtils.likeOrUnlike(ARTICLE_LIKE_PREFIX, ARTICLE_LIKE_COUNT, articleId);
    }

    /**
     * 保存或更新文章和标签的映射
     * <p>
     * <b>注意：此方法尝试全程使用 Optional，可读性比较差，且不好维护，不建议这样写。</b>
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
                        .in(Tag::getTagName, tagNameSet)
                        .list().stream()
                        .collect(Collectors.toMap
                                (Tag::getId, Tag::getTagName,
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
                    List<Tag> newTagList = tagService.saveAndGetNewTags(nameSet);
                    List<Integer> newTagIdList = newTagList.stream()
                            .map(Tag::getId)
                            .collect(Collectors.toList());
                    tagIdSetOption.ifPresent(tagIdSet -> tagIdSet.addAll(newTagIdList));
                });
        // 更新操作，删除原有的文章和标签映射
        Optional.ofNullable(articleVO.getId())
                .ifPresent(previousArticleId -> articleTagService
                        .lambdaUpdate()
                        .eq(ArticleTag::getArticleId, previousArticleId)
                        .remove());
        // 保存新的文章和标签映射
        tagIdSetOption
                .map(Collection::stream)
                .map(tagIdStream -> tagIdStream
                        .map(tagId -> ArticleTag.builder()
                                .articleId(articleId)
                                .tagId(tagId)
                                .build())
                        .collect(Collectors.toList()))
                .ifPresent(articleTagService::saveBatch);
    }

    /**
     * 更新文章浏览量
     * <p>
     * <b>注意：此更新操作用到了与请求线程绑定的 SESSION</b>
     *
     * @param articleId 文章 ID
     */
    private void updateArticleViewsCount(HttpSession session, Integer articleId) {
        // 判断是否第一次访问，是则增加浏览量
        Set<Integer> articleSet = ConvertUtils.castSet
                (Optional.ofNullable(session.getAttribute(ARTICLE_SET))
                        .orElseGet(HashSet::new));
        if (articleSet.add(articleId)) {
            session.setAttribute(ARTICLE_SET, articleSet);
            RedisUtils.zIncyBy(ARTICLE_VIEWS_COUNT, articleId, ONE);
        }
    }

    /**
     * 获取文章分类
     *
     * @param articleVO 文章数据
     * @return 文章分类，不存在分类的草稿返回 null
     */
    private Category getCategory(ArticleVO articleVO) {
        // 判断分类是否存在，存在则返回该分类，不存在且文章不是草稿则创建新分类
        Category category = new LambdaQueryChainWrapper<>(categoryMapper)
                .eq(Category::getCategoryName, articleVO.getCategoryName())
                .one();
        if (category == null) {
            if (DRAFT.getStatus().equals(articleVO.getStatus())) {
                return null;
            }
            category = Category.builder()
                    .categoryName(articleVO.getCategoryName())
                    .build();
            categoryMapper.insert(category);
        }
        return category;
    }

}




