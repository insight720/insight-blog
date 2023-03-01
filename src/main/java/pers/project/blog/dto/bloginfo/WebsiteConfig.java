package pers.project.blog.dto.bloginfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网站配置信息
 *
 * @author Luo Fei
 * @date 2023/1/3
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebsiteConfig {

    /**
     * 网站头像
     */
    @Schema(description = "网站头像")
    private String websiteAvatar;

    /**
     * 网站名称
     */
    @Schema(description = "网站名称")
    private String websiteName;

    /**
     * 网站作者
     */
    @Schema(description = "网站作者")
    private String websiteAuthor;

    /**
     * 网站介绍
     */
    @Schema(description = "网站介绍")
    private String websiteIntro;

    /**
     * 网站公告
     */
    @Schema(description = "网站公告")
    private String websiteNotice;

    /**
     * 网站创建时间
     */
    @Schema(description = "网站创建时间")
    private String websiteCreateTime;

    /**
     * 网站备案号
     */
    @Schema(description = "网站备案号")
    private String websiteRecordNo;

    /**
     * 社交登录列表
     */
    @Schema(description = "社交登录列表")
    private List<String> socialLoginList;

    /**
     * 社交 URL 列表
     */
    @Schema(description = "社交 URL 列表")
    private List<String> socialUrlList;

    /**
     * QQ
     */
    @Schema(description = "QQ")
    private String qq;

    /**
     * GitHub
     */
    @Schema(description = "GitHub")
    private String github;

    /**
     * Gitee
     */
    @Schema(description = "Gitee")
    private String gitee;

    /**
     * 游客头像
     */
    @Schema(description = "游客头像")
    private String touristAvatar;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private String userAvatar;

    /**
     * 是否评论审核
     */
    @Schema(description = "是否评论审核")
    private Integer isCommentReview;

    /**
     * 是否留言审核
     */
    @Schema(description = "是否留言审核")
    private Integer isMessageReview;

    /**
     * 是否邮箱通知
     */
    @Schema(description = "是否邮箱通知")
    private Integer isEmailNotice;

    /**
     * 是否打赏
     */
    @Schema(description = "是否打赏")
    private Integer isReward;

    /**
     * 微信二维码
     */
    @Schema(description = "微信二维码")
    private String weiXinQRCode;

    /**
     * 支付宝二维码
     */
    @Schema(description = "支付宝二维码")
    private String alipayQRCode;

    /**
     * 文章封面
     */
    @Schema(description = "文章封面")
    private String articleCover;

    /**
     * 是否开启聊天室
     */
    @Schema(description = "是否打赏")
    private Integer isChatRoom;

    /**
     * WebSocket 地址
     */
    @Schema(description = "WebSocket 地址")
    private String websocketUrl;

    /**
     * 是否开启音乐
     */
    @Schema(description = "是否开启音乐")
    private Integer isMusicPlayer;

    /**
     * 是否通知异常
     */
    @Schema(description = "是否通知异常")
    private Integer notifyError;

    /**
     * 博客网址（不以 / 结尾）
     */
    @Schema(description = "博客网址（不以 / 结尾）")
    private String url;

    /**
     * 博主邮箱
     */
    @Schema(description = "博主邮箱")
    private String email;

}
