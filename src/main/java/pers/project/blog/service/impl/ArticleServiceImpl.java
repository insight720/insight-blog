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
 * ????????????tb_article????????????????????? Service ??????
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
        // ???????????????????????????????????????
        Long adminArticlesCount = baseMapper.countAdminArticles(articleSearchVO);
        if (adminArticlesCount.equals(ZERO_L)) {
            return new PageDTO<>();
        }
        // ???????????????????????????
        long offset = PageUtils.offset(), size = PageUtils.size();
        CompletableFuture<List<AdminArticleDTO>> future = AsyncUtils.supplyAsync
                (() -> baseMapper.listAdminArticles(offset, size, articleSearchVO));
        // ??????????????????????????????????????????
        Map<Object, Double> articleIdViewsCountMap = RedisUtils
                .zRangeWithScores(ARTICLE_VIEWS_COUNT, ZERO, -ONE);
        Map<String, Object> articleIdLikeCountMap = RedisUtils
                .hGetAll(ARTICLE_LIKE_COUNT);
        // ????????????????????????????????????
        List<AdminArticleDTO> adminArticleList = AsyncUtils.get
                (future, "?????????????????????????????????????????????");
        // ??????????????????
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
        // ?????????????????????????????? ID
        article.setUserId(SecurityUtils.getUserInfoId());
        // ????????????????????????????????????
        Category category = getCategory(articleVO);
        if (category != null) {
            article.setCategoryId(category.getId());
        }
        // ???????????????????????????????????????
        if (StringUtils.isBlank(article.getArticleCover())) {
            String articleCover = ConfigUtils.getCache(WebsiteConfig::getArticleCover);
            article.setArticleCover(articleCover);
        }
        // ?????????????????????
        saveOrUpdate(article);
        // ???????????????????????????????????????
        saveOrUpdateArticleTagMap(articleVO, article.getId());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
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
        // ??????????????????
        Article article = baseMapper.selectById(articleId);
        ArticleVO articleVO = ConvertUtils.convert
                (article, ArticleVO.class);
        // ??????????????????
        Category category = categoryMapper.selectById
                (article.getCategoryId());
        if (category != null) {
            articleVO.setCategoryName(category.getCategoryName());
        }
        // ??????????????????
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
        // ??????????????????????????????????????????????????? URL ??????
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
                        throw new ServiceException("??????????????????", cause);
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
        // ?????????????????????
        HttpSession session = WebUtils.getCurrentSession();
        AsyncUtils.runAsync(() -> updateArticleViewsCount(session, articleId));
        // ?????? 6 ???????????????
        CompletableFuture<List<ArticleRecommendDTO>> recommendFuture = AsyncUtils
                .supplyAsync(() -> baseMapper.listArticleRecommendArticles(articleId));
        // ?????? 5 ???????????????
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
        // ?????? ID ?????????????????????
        ArticleDTO articleDTO = baseMapper.getArticle(articleId);
        // ???????????????????????????????????????
        Article previousArticle = lambdaQuery()
                .select(Article::getId,
                        Article::getArticleTitle,
                        Article::getArticleCover)
                .eq(Article::getIsDelete, FALSE_OF_INT)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .lt(Article::getId, articleId)
                .orderByDesc(Article::getId)
                // ?????????
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
        // ???????????????????????????
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
        // ?????????????????????????????????????????????
        List<ArticleRecommendDTO> recommendArticleList
                = AsyncUtils.get(recommendFuture, "?????? 6 ?????????????????????");
        List<ArticleRecommendDTO> newestArticleList
                = AsyncUtils.get(newestFuture, "?????? 5 ?????????????????????");
        articleDTO.setNewestArticleList(newestArticleList);
        articleDTO.setRecommendArticleList(recommendArticleList);
        return articleDTO;
    }

    @Override
    public ArticlePreviewDTO getArticlePreview(ArticlePreviewVO articlePreviewVO) {
        // ???????????????????????????????????????
        long offset = PageUtils.offset(), size = PageUtils.size();
        CompletableFuture<List<PreviewDTO>> previewFuture = AsyncUtils.supplyAsync
                (() -> baseMapper.listPreviewDTOs(offset, size, articlePreviewVO));
        // ??????????????????????????????
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
        // ????????????????????????????????????
        List<PreviewDTO> previewDTOList = AsyncUtils.get
                (previewFuture, "?????????????????????????????????????????????");
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
        // ????????????????????????????????????????????????
        RedisUtils.likeOrUnlike(ARTICLE_LIKE_PREFIX, ARTICLE_LIKE_COUNT, articleId);
    }

    /**
     * ???????????????????????????????????????
     * <p>
     * <b>???????????????????????????????????? Optional???????????????????????????????????????????????????????????????</b>
     *
     * @param articleVO ???????????? <p><b>?????????ID ?????????????????? {@code articleId} ????????????????????????</b>
     * @param articleId ?????? ID??????????????????????????? ID
     */
    private void saveOrUpdateArticleTagMap(ArticleVO articleVO, Integer articleId) {
        // ????????????????????????
        Optional<HashSet<String>> tagNameSetOption = Optional
                .ofNullable(articleVO.getTagNameList())
                .map(HashSet::new)
                .filter(CollectionUtils::isNotEmpty);
        // ???????????????????????????????????????????????? ID ??? ???????????????
        Optional<HashMap<Integer, String>> previousTagIdNameMapOption = tagNameSetOption
                .map(tagNameSet -> tagService.lambdaQuery()
                        .in(Tag::getTagName, tagNameSet)
                        .list().stream()
                        .collect(Collectors.toMap
                                (Tag::getId, Tag::getTagName,
                                        (tagName, sameTagName) -> tagName, HashMap::new)))
                .filter(CollectionUtils::isNotEmpty);
        // ???????????????
        tagNameSetOption.ifPresent(tagNameSet -> tagNameSet.removeAll
                (previousTagIdNameMapOption.map(HashMap::values).orElseGet(HashSet::new)));
        // ??????????????? ID
        Optional<Set<Integer>> tagIdSetOption = previousTagIdNameMapOption.map(HashMap::keySet);
        // ?????????????????????????????????????????????????????????????????? ID
        tagNameSetOption
                .ifPresent(nameSet -> {
                    List<Tag> newTagList = tagService.saveAndGetNewTags(nameSet);
                    List<Integer> newTagIdList = newTagList.stream()
                            .map(Tag::getId)
                            .collect(Collectors.toList());
                    tagIdSetOption.ifPresent(tagIdSet -> tagIdSet.addAll(newTagIdList));
                });
        // ???????????????????????????????????????????????????
        Optional.ofNullable(articleVO.getId())
                .ifPresent(previousArticleId -> articleTagService
                        .lambdaUpdate()
                        .eq(ArticleTag::getArticleId, previousArticleId)
                        .remove());
        // ?????????????????????????????????
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
     * ?????????????????????
     * <p>
     * <b>????????????????????????????????????????????????????????? SESSION</b>
     *
     * @param articleId ?????? ID
     */
    private void updateArticleViewsCount(HttpSession session, Integer articleId) {
        // ???????????????????????????????????????????????????
        Set<Integer> articleSet = ConvertUtils.castSet
                (Optional.ofNullable(session.getAttribute(ARTICLE_SET))
                        .orElseGet(HashSet::new));
        if (articleSet.add(articleId)) {
            session.setAttribute(ARTICLE_SET, articleSet);
            RedisUtils.zIncyBy(ARTICLE_VIEWS_COUNT, articleId, ONE);
        }
    }

    /**
     * ??????????????????
     *
     * @param articleVO ????????????
     * @return ????????????????????????????????????????????? null
     */
    private Category getCategory(ArticleVO articleVO) {
        // ??????????????????????????????????????????????????????????????????????????????????????????????????????
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




