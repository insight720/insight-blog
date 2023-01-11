package pers.project.blog.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import pers.project.blog.constant.AddressConstant;
import pers.project.blog.constant.BooleanConstant;
import pers.project.blog.constant.RedisConstant;
import pers.project.blog.constant.WebsiteConstant;
import pers.project.blog.constant.enumeration.ArticleStatusEnum;
import pers.project.blog.dto.*;
import pers.project.blog.entity.ArticleEntity;
import pers.project.blog.entity.WebsiteConfigEntity;
import pers.project.blog.exception.ServiceException;
import pers.project.blog.mapper.*;
import pers.project.blog.service.BlogInfoService;
import pers.project.blog.service.PageService;
import pers.project.blog.service.UniqueViewService;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.IpUtils;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.vo.InfoAboutMeVO;
import pers.project.blog.vo.PageVO;
import pers.project.blog.vo.WebsiteConfigVO;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 博客信息服务实现
 *
 * @author Luo Fei
 * @date 2022/12/28
 */
@Service
public class BlogInfoServiceImpl implements BlogInfoService {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private UniqueViewService uniqueViewService;

    @Autowired
    private WebsiteConfigMapper websiteConfigMapper;

    @Autowired
    private PageService pageService;

    @Override
    public void report() {
        String ipAddress = IpUtils.getIpAddress(httpServletRequest);

        UserAgent userAgent = IpUtils.getUserAgent(httpServletRequest);
        Browser browser = userAgent.getBrowser();
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();

        String uniqueId = ipAddress + browser.getName() + operatingSystem.getName();
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(uniqueId.getBytes());

        // 判断访客是否访问过
        if (RedisUtils.sIsMember(RedisConstant.UNIQUE_VISITOR, md5DigestAsHex)) {
            return;
        }

        // TODO: 2022/12/28 统计的信息格式不明
        // 统计访客地域分布
        String ipSource = IpUtils.getIpSource(ipAddress);
        String ipSourceDigest;
        if (StringUtils.hasText(ipSource)) {
            ipSourceDigest = ipSource.substring(0, 2)
                    .replaceAll(AddressConstant.PROVINCE, "")
                    .replaceAll(AddressConstant.CITY, "");
        } else {
            ipSourceDigest = AddressConstant.UNKNOWN;
        }
        RedisUtils.hIncrBy(RedisConstant.VISITOR_AREA, ipSourceDigest, 1L);

        // 统计博客浏览量
        RedisUtils.incrBy(RedisConstant.BLOG_VIEWS_COUNT, 1L);

        // 保存访客唯一标识
        RedisUtils.sAdd(RedisConstant.UNIQUE_VISITOR, md5DigestAsHex);
    }

    @Autowired
    Executor executor;

    @Override
    public AdminBlogInfoDTO getBlogBackInfo() {
        // TODO: 2022/12/30 接口耗时超过 1 s
        StopWatch stopWatch = new StopWatch("getBlogBackInfo()");

        // 访问计数
        stopWatch.start("访问计数");
        Integer viewsCount = Integer.parseInt
                (Optional.ofNullable(RedisUtils.get(RedisConstant.BLOG_VIEWS_COUNT)).orElse(0).toString());
/*
        CompletableFuture<Integer> viewsCount
                = CompletableFuture.supplyAsync(() -> Integer.parseInt
                        (Optional.ofNullable(RedisUtils.get(RedisConstant.BLOG_VIEWS_COUNT)).orElse(0).toString())
                , executor);
*/
        stopWatch.stop();

        // 留言计数
        stopWatch.start("留言计数");
        Integer messageCount = messageMapper.selectCount(null).intValue();
        stopWatch.stop();

        // 用户计数
        stopWatch.start("用户计数");
        Integer userCount = userInfoMapper.selectCount(null).intValue();
        stopWatch.stop();

        // 文章计数
        stopWatch.start("文章计数");
        Integer articleCount = new LambdaQueryChainWrapper<>(articleMapper)
                .eq(ArticleEntity::getIsDelete, BooleanConstant.FALSE).count().intValue();
        stopWatch.stop();

        // 分类数据
        stopWatch.start("分类数据");
        List<CategoryDTO> categoryDTOList = categoryMapper.listCategoryDTOs();
        stopWatch.stop();

        // 标签数据
        stopWatch.start("标签数据");
        List<TagDTO> tagDTOList = ConversionUtils.covertList(tagMapper.selectList(null), TagDTO.class);
        stopWatch.stop();

        // 文章统计
        stopWatch.start("文章统计");
        List<ArticleStatisticsDTO> articleStatisticsDTOList = articleMapper.listArticleStatisticsDTOs();
        stopWatch.stop();

        // 一周内的访问量统计
        stopWatch.start("一周内的访问量统计");
        List<UniqueViewDTO> uniqueViewDTOList = uniqueViewService.listUniqueViewDTOs();
        stopWatch.stop();

        // 访问量前五的文章
        stopWatch.start("访问量前五的文章");

        List<ArticleRankDTO> articleRankDTOList = listArticleRankDTOs
                (RedisUtils.zRevRangeWithScores(RedisConstant.ARTICLE_VIEWS_COUNT, 0, 4));
/*
        CompletableFuture<List<ArticleRankDTO>> articleRankDTOList =
                CompletableFuture.supplyAsync(() -> listArticleRankDTOs
                        (RedisUtils.zRevRange(RedisConstant.ARTICLE_VIEWS_COUNT, 0, 4)), executor);

*/
        stopWatch.stop();

        stopWatch.start("组装对象");
        AdminBlogInfoDTO build;
        try {
            build = AdminBlogInfoDTO.builder()
                    .viewsCount(viewsCount)
                    .messageCount(messageCount)
                    .userCount(userCount)
                    .articleCount(articleCount)
                    .categoryDTOList(categoryDTOList)
                    .tagDTOList(tagDTOList)
                    .articleStatisticsList(articleStatisticsDTOList)
                    .uniqueViewDTOList(uniqueViewDTOList)
                    .articleRankDTOList(articleRankDTOList)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());

        return build;
    }

    // TODO: 2023/1/3 库表只有一条数据，且网站必须有默认配置信息

    @Override
    @NotNull
    public WebsiteConfigVO getWebSiteConfig() {
        // 从缓存获取配置信息，没有则从数据库查询并更新缓存
        String webSiteConfig
                = Optional.ofNullable((String) RedisUtils.get(RedisConstant.WEBSITE_CONFIG))
                .orElseGet(() -> {
                    String config;
                    try {
                        config = websiteConfigMapper
                                .selectById(WebsiteConstant.DEFAULT_CONFIG_ID)
                                .getConfig();
                        RedisUtils.set(RedisConstant.WEBSITE_CONFIG, config);
                        return config;
                    } catch (Exception cause) {
                        throw new ServiceException("网站无默认配置信息", cause);
                    }
                });

        return ConversionUtils.parseJson(webSiteConfig, WebsiteConfigVO.class);
    }

    @Override
    public void updateWebSizeConfig(WebsiteConfigVO websiteConfigVO) {
        new LambdaUpdateChainWrapper<>(websiteConfigMapper)
                .eq(WebsiteConfigEntity::getId, WebsiteConstant.DEFAULT_CONFIG_ID)
                .set(WebsiteConfigEntity::getConfig, ConversionUtils.getJson(websiteConfigVO))
                .update();

        RedisUtils.del(RedisConstant.WEBSITE_CONFIG);
    }

    @Override
    public String getInfoAboutMe() {
        return Optional
                .ofNullable((String) RedisUtils.get(RedisConstant.ABOUT))
                .orElse("");
    }

    @Override
    public void updateInfoAboutMe(InfoAboutMeVO infoAboutMeVO) {
        RedisUtils.set(RedisConstant.ABOUT, infoAboutMeVO.getAboutContent());
    }

    @Override
    public BlogInfoDTO getBlogInfo() {
        // 查询文章数量
        Long articleCount = new LambdaQueryChainWrapper<>(articleMapper)
                .eq(ArticleEntity::getStatus, ArticleStatusEnum.PUBLIC.getStatus())
                .eq(ArticleEntity::getIsDelete, BooleanConstant.FALSE)
                .count();
        // 查询分类数量
        Long categoryCount = new LambdaQueryChainWrapper<>(categoryMapper).count();
        // 查询标签数量
        Long tagCount = new LambdaQueryChainWrapper<>(tagMapper).count();
        // 查询访问量
        String viewsCount = Optional
                .ofNullable(RedisUtils.get(RedisConstant.BLOG_VIEWS_COUNT))
                .map(String::valueOf)
                .orElse("0");
        // 查询网站配置
        WebsiteConfigVO webSiteConfig = getWebSiteConfig();
        // 查询页面图片
        List<PageVO> pageList = pageService.listPages();
        // 封装数据
        return BlogInfoDTO.builder()
                .articleCount(articleCount.intValue())
                .categoryCount(categoryCount.intValue())
                .tagCount(tagCount.intValue())
                .viewsCount(viewsCount)
                .websiteConfig(webSiteConfig)
                .pageList(pageList)
                .build();
    }

    /**
     * 查询文章排行信息
     *
     * @param articleIdScoreMap 文章 ID 与访问量的映射
     * @return 文章排行信息
     */
    private List<ArticleRankDTO> listArticleRankDTOs(Map<Object, Double> articleIdScoreMap) {
        if (CollectionUtils.isEmpty(articleIdScoreMap)) {
            return new ArrayList<>();
        }

        return new LambdaQueryChainWrapper<>(articleMapper)
                .select(ArticleEntity::getId, ArticleEntity::getArticleTitle)
                .in(ArticleEntity::getId, articleIdScoreMap.keySet())
                .list()
                .stream()
                .map(articleEntity -> ArticleRankDTO.builder()
                        .articleTitle(articleEntity.getArticleTitle())
                        .viewsCount(articleIdScoreMap.get(articleEntity.getId()).intValue())
                        .build())
                .collect(Collectors.toList());
    }

}
