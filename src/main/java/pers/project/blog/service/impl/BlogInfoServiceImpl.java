package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.ip2region.core.IpInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.constant.FilePathConst;
import pers.project.blog.dto.article.ArticleRankDTO;
import pers.project.blog.dto.article.DailyArticleDTO;
import pers.project.blog.dto.bloginfo.AdminBlogInfoDTO;
import pers.project.blog.dto.bloginfo.BlogInfoDTO;
import pers.project.blog.dto.bloginfo.DailyVisitDTO;
import pers.project.blog.dto.bloginfo.WebsiteConfig;
import pers.project.blog.dto.category.CategoryDTO;
import pers.project.blog.dto.tag.TagDTO;
import pers.project.blog.entity.Article;
import pers.project.blog.mapper.ArticleMapper;
import pers.project.blog.mapper.CategoryMapper;
import pers.project.blog.mapper.MessageMapper;
import pers.project.blog.mapper.UserInfoMapper;
import pers.project.blog.schedule.VisitCountSchedule;
import pers.project.blog.service.BlogInfoService;
import pers.project.blog.service.PageService;
import pers.project.blog.service.TagService;
import pers.project.blog.strategy.context.UploadContext;
import pers.project.blog.util.AsyncUtils;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.util.TimeUtils;
import pers.project.blog.util.WebUtils;
import pers.project.blog.vo.bloginfo.InfoAboutMeVO;
import pers.project.blog.vo.page.PageVO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static pers.project.blog.constant.GenericConst.*;
import static pers.project.blog.constant.RedisConst.*;
import static pers.project.blog.enums.ArticelStateEnum.PUBLIC;

/**
 * ????????????????????????
 *
 * @author Luo Fei
 * @date 2022/12/28
 */
@Slf4j
@Service
public class BlogInfoServiceImpl implements BlogInfoService {

    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private PageService pageService;
    @Resource
    private TagService tagService;

    @Override
    public void updateVisitCount() {
        // ?????????????????????????????????
        RedisUtils.incr(VISIT);
        RedisUtils.incr(DAILY_VISIT_PREFIX + TimeUtils.today());
        // ???????????? IP ??????
        HttpServletRequest request = WebUtils.getCurrentRequest();
        String ipAddress = WebUtils.getIpAddress(request);
        // ????????????????????????
        UserAgent userAgent = WebUtils.getUserAgent(request);
        String browser = userAgent.getBrowser().getName();
        String os = userAgent.getOperatingSystem().getName();
        // ?????????????????????
        String visitorId = ipAddress + browser + os;
        String province = WebUtils.getInfo(ipAddress, IpInfo::getProvince);
        if (province != null) {
            RedisUtils.sAdd(PROVINCE, province);
            RedisUtils.pfAdd(VISITOR_PROVINCE_PREFIX + province, visitorId);
        }
    }

    @Override
    public BlogInfoDTO getBlogInfo() {
        // ??????????????????
        Long articleCount = new LambdaQueryChainWrapper<>(articleMapper)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .eq(Article::getIsDelete, FALSE_OF_INT)
                .count();
        // ??????????????????
        Long categoryCount = categoryMapper.selectCount(null);
        // ??????????????????
        Long tagCount = tagService.count();
        // ???????????????
        String viewsCount = Optional.ofNullable
                ((RedisUtils.get(VISIT))).orElse(ZERO).toString();
        // ??????????????????
        WebsiteConfig webSiteConfig = getWebSiteConfig();
        // ??????????????????
        List<PageVO> pageList = pageService.listPages();
        // ????????????
        return BlogInfoDTO.builder()
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagCount(tagCount)
                .viewsCount(viewsCount)
                .websiteConfig(webSiteConfig)
                .pageList(pageList)
                .build();
    }

    @Override
    public AdminBlogInfoDTO getBlogBackInfo() {
        // ??????????????????????????????
        CompletableFuture<List<ArticleRankDTO>> rankFuture
                = AsyncUtils.supplyAsync(() -> {
            Map<Object, Double> articleIdScoreMap = RedisUtils
                    .zRevRangeWithScores(ARTICLE_VIEWS_COUNT, ZERO, FOUR);
            return buildArticleRankDTOs(articleIdScoreMap);
        });
        // ????????????
        CompletableFuture<List<CategoryDTO>> categoryFuture
                = AsyncUtils.supplyAsync(() -> categoryMapper.listCategories());
        // ????????????????????????
        List<DailyArticleDTO> dailyArticleDTOList
                = articleMapper.listDailyArticleDTOs();
        // ???????????????
        Integer viewsCount = (Integer) Optional.ofNullable
                (RedisUtils.get(VISIT)).orElse(ZERO);
        // ????????????
        Long userCount = userInfoMapper.selectCount(null);
        // ????????????
        Long articleCount = new LambdaQueryChainWrapper<>(articleMapper)
                .eq(Article::getIsDelete, FALSE_OF_INT).count();
        // ????????????
        Long messageCount = messageMapper.selectCount(null);
        // ???????????????????????????
        List<DailyVisitDTO> dailyVisitDTOList = VisitCountSchedule.getWeeklyVisit();
        // ????????????
        List<TagDTO> tagDTOList = tagService.listArticleTags(null);
        // ??????????????????????????????????????????
        List<ArticleRankDTO> articleRankDTOList = AsyncUtils.get
                (rankFuture, "???????????????????????????????????????");
        // ??????????????????????????????
        List<CategoryDTO> categoryDTOList = AsyncUtils.get
                (categoryFuture, "????????????????????????");
        return AdminBlogInfoDTO.builder()
                .viewsCount(viewsCount)
                .messageCount(messageCount)
                .userCount(userCount)
                .articleCount(articleCount)
                .categoryDTOList(categoryDTOList)
                .tagDTOList(tagDTOList)
                .articleStatisticsList(dailyArticleDTOList)
                .dailyVisitDTOList(dailyVisitDTOList)
                .articleRankDTOList(articleRankDTOList)
                .build();
    }

    @Override
    public WebsiteConfig getWebSiteConfig() {
        WebsiteConfig websiteConfig = (WebsiteConfig) RedisUtils.get(WEBSITE_CONFIG);
        if (websiteConfig == null) {
            log.error("???????????????????????????????????????????????????");
            websiteConfig = new WebsiteConfig();
        }
        return websiteConfig;
    }

    @Override
    public void updateWebSizeConfig(WebsiteConfig websiteConfig) {
        RedisUtils.set(WEBSITE_CONFIG, websiteConfig);
    }

    @Override
    public String getInfoAboutMe() {
        return Optional.ofNullable((String) RedisUtils.get(INFO_ABOUT_ME))
                .orElse(EMPTY_STR);
    }

    @Override
    public void updateInfoAboutMe(InfoAboutMeVO infoAboutMeVO) {
        RedisUtils.set(INFO_ABOUT_ME, infoAboutMeVO.getAboutContent());
    }

    @Override
    public String uploadConfigImages(MultipartFile multipartFile) {
        return UploadContext.executeStrategy
                (multipartFile, FilePathConst.CONFIG_DIR);
    }

    /**
     * ????????????????????????
     */
    private List<ArticleRankDTO> buildArticleRankDTOs(Map<Object, Double> articleIdScoreMap) {
        if (CollectionUtils.isEmpty(articleIdScoreMap)) {
            return new ArrayList<>();
        }
        return new LambdaQueryChainWrapper<>(articleMapper)
                .select(Article::getId, Article::getArticleTitle)
                .in(Article::getId, articleIdScoreMap.keySet())
                .list().stream()
                .map(article -> ArticleRankDTO.builder()
                        .articleTitle(article.getArticleTitle())
                        .viewsCount(articleIdScoreMap.get(article.getId()).intValue())
                        .build())
                .collect(Collectors.toList());
    }

}
