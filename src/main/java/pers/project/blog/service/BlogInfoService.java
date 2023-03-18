package pers.project.blog.service;

import org.springframework.web.multipart.MultipartFile;
import pers.project.blog.dto.bloginfo.AdminBlogInfoDTO;
import pers.project.blog.dto.bloginfo.BlogInfoDTO;
import pers.project.blog.dto.bloginfo.WebsiteConfig;
import pers.project.blog.vo.bloginfo.InfoAboutMeVO;

/**
 * 博客信息服务
 *
 * @author Luo Fei
 * @version 2022/12/28
 */
public interface BlogInfoService {

    /**
     * 更新访问统计信息
     * <p>
     * 统计总访问量、每日访问量和省份的访客量。
     */
    void updateVisitCount();

    /**
     * 获取博客首页数据
     */
    BlogInfoDTO getBlogInfo();

    /**
     * 获取后台管理首页数据
     */
    AdminBlogInfoDTO getBlogBackInfo();

    /**
     * 获取网站配置信息
     */
    WebsiteConfig getWebSiteConfig();

    /**
     * 更新网站配置
     */
    void updateWebSizeConfig(WebsiteConfig websiteConfig);

    /**
     * 获取关于我信息
     */
    String getInfoAboutMe();

    /**
     * 更新关于我的信息
     */
    void updateInfoAboutMe(InfoAboutMeVO infoAboutMeVO);

    /**
     * 上传博客配置图片
     */
    String uploadConfigImages(MultipartFile multipartFile);

}