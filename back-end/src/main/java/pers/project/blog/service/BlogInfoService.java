package pers.project.blog.service;

import pers.project.blog.dto.AdminBlogInfoDTO;
import pers.project.blog.dto.BlogInfoDTO;
import pers.project.blog.vo.InfoAboutMeVO;
import pers.project.blog.vo.WebsiteConfigVO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 博客信息服务
 *
 * @author Luo Fei
 * @date 2022/12/28
 */
public interface BlogInfoService {

    /**
     * 上传访客信息
     */
    void report();

    /**
     * 获取后台首页数据
     *
     * @return 博客后台信息
     */
    AdminBlogInfoDTO getBlogBackInfo();

    /**
     * 获取网站配置信息
     *
     * @return 网站配置信息
     */
    @NotNull
    WebsiteConfigVO getWebSiteConfig();

    /**
     * 保存或更新网站配置
     *
     * @param websiteConfigVO 网站配置信息
     */
    void updateWebSizeConfig(WebsiteConfigVO websiteConfigVO);

    /**
     * 获取关于我的信息
     *
     * @return 关于我的信息
     */
    String getInfoAboutMe();

    /**
     * 更新关于我的信息
     *
     * @param infoAboutMeVO 关于我的信息
     */
    void updateInfoAboutMe(@Valid InfoAboutMeVO infoAboutMeVO);

    /**
     * 获取首页数据
     *
     * @return 博客首页信息
     */
    BlogInfoDTO getBlogInfo();

}