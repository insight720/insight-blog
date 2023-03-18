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
 * 博客信息服务实现
 *
 * @author Luo Fei
 * @version 2022/12/28
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
        // 统计总访问量和日访问量
        RedisUtils.incr(VISIT);
        RedisUtils.incr(DAILY_VISIT_PREFIX + TimeUtils.today());
        // 获取访客 IP 地址
        HttpServletRequest request = WebUtils.getCurrentRequest();
        String ipAddress = WebUtils.getIpAddress(request);
        // 获取访客代理信息
        UserAgent userAgent = WebUtils.getUserAgent(request);
        String browser = userAgent.getBrowser().getName();
        String os = userAgent.getOperatingSystem().getName();
        // 统计省份访客量
        String visitorId = ipAddress + browser + os;
        String province = WebUtils.getInfo(ipAddress, IpInfo::getProvince);
        if (province != null) {
            RedisUtils.sAdd(PROVINCE, province);
            RedisUtils.pfAdd(VISITOR_PROVINCE_PREFIX + province, visitorId);
        }
    }

    @Override
    public BlogInfoDTO getBlogInfo() {
        // 查询文章数量
        Long articleCount = new LambdaQueryChainWrapper<>(articleMapper)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .eq(Article::getIsDelete, FALSE_OF_INT)
                .count();
        // 查询分类数量
        Long categoryCount = categoryMapper.selectCount(null);
        // 查询标签数量
        Long tagCount = tagService.count();
        // 查询访问量
        String viewsCount = Optional.ofNullable
                ((RedisUtils.get(VISIT))).orElse(ZERO).toString();
        // 查询网站配置
        WebsiteConfig webSiteConfig = getWebSiteConfig();
        // 查询页面图片
        List<PageVO> pageList = pageService.listPages();
        // 封装数据
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
        // 访问量前五的文章数据
        CompletableFuture<List<ArticleRankDTO>> rankFuture
                = AsyncUtils.supplyAsync(() -> {
            Map<Object, Double> articleIdScoreMap = RedisUtils
                    .zRevRangeWithScores(ARTICLE_VIEWS_COUNT, ZERO, FOUR);
            return buildArticleRankDTOs(articleIdScoreMap);
        });
        // 分类数据
        CompletableFuture<List<CategoryDTO>> categoryFuture
                = AsyncUtils.supplyAsync(() -> categoryMapper.listCategories());
        // 每日文章数量统计
        List<DailyArticleDTO> dailyArticleDTOList
                = articleMapper.listDailyArticleDTOs();
        // 访问量计数
        Integer viewsCount = (Integer) Optional.ofNullable
                (RedisUtils.get(VISIT)).orElse(ZERO);
        // 用户计数
        Long userCount = userInfoMapper.selectCount(null);
        // 文章计数
        Long articleCount = new LambdaQueryChainWrapper<>(articleMapper)
                .eq(Article::getIsDelete, FALSE_OF_INT).count();
        // 留言计数
        Long messageCount = messageMapper.selectCount(null);
        // 一周内的访问量统计
        List<DailyVisitDTO> dailyVisitDTOList = VisitCountSchedule.getWeeklyVisit();
        // 标签数据
        List<TagDTO> tagDTOList = tagService.listArticleTags(null);
        // 获取访问量前五的文章查询结果
        List<ArticleRankDTO> articleRankDTOList = AsyncUtils.get
                (rankFuture, "获取访问量前五文章数据异常");
        // 获取分类数据查询结果
        List<CategoryDTO> categoryDTOList = AsyncUtils.get
                (categoryFuture, "获取分类数据异常");
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
            log.error("网站无配置信息，请前往后台管理配置");
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
     * 组装文章排行数据
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
