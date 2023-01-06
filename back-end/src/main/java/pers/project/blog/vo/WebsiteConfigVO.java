package pers.project.blog.vo;

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
@Schema(name = "网站配置信息")
public class WebsiteConfigVO {

    /**
     * 网站头像
     */
    @Schema(name = "websiteAvatar", title = "网站头像", type = "String")
    private String websiteAvatar;

    /**
     * 网站名称
     */
    @Schema(name = "websiteName", title = "网站名称", type = "String")
    private String websiteName;

    /**
     * 网站作者
     */
    @Schema(name = "websiteAuthor", title = "网站作者", type = "String")
    private String websiteAuthor;

    /**
     * 网站介绍
     */
    @Schema(name = "websiteIntro", title = "网站介绍", type = "String")
    private String websiteIntro;

    /**
     * 网站公告
     */
    @Schema(name = "websiteNotice", title = "网站公告", type = "String")
    private String websiteNotice;

    /**
     * 网站创建时间
     */
    @Schema(name = "websiteCreateTime", title = "网站创建时间", type = "LocalDateTime")
    private String websiteCreateTime;

    /**
     * 网站备案号
     */
    @Schema(name = "websiteRecordNo", title = "网站备案号", type = "String")
    private String websiteRecordNo;

    /**
     * 社交登录列表
     */
    @Schema(name = "socialLoginList", title = "社交登录列表", type = "List<String>")
    private List<String> socialLoginList;

    /**
     * 社交 URL 列表
     */
    @Schema(name = "socialUrlList", title = "社交 URL 列表", type = "List<String>")
    private List<String> socialUrlList;

    /**
     * QQ
     */
    @Schema(name = "qq", title = "QQ", type = "String")
    private String qq;

    /**
     * GitHub
     */
    @Schema(name = "github", title = "GitHub", type = "String")
    private String github;

    /**
     * gitee
     */
    @Schema(name = "gitee", title = "Gitee", type = "String")
    private String gitee;

    /**
     * 游客头像
     */
    @Schema(name = "touristAvatar", title = "游客头像", type = "String")
    private String touristAvatar;

    /**
     * 用户头像
     */
    @Schema(name = "userAvatar", title = "用户头像", type = "String")
    private String userAvatar;

    /**
     * 是否评论审核
     */
    @Schema(name = "isCommentReview", title = "是否评论审核", type = "Integer")
    private Integer isCommentReview;

    /**
     * 是否留言审核
     */
    @Schema(name = "isMessageReview", title = "是否留言审核", type = "Integer")
    private Integer isMessageReview;

    /**
     * 是否邮箱通知
     */
    @Schema(name = "isEmailNotice", title = "是否邮箱通知", type = "Integer")
    private Integer isEmailNotice;

    /**
     * 是否打赏
     */
    @Schema(name = "isReward", title = "是否打赏", type = "Integer")
    private Integer isReward;

    /**
     * 微信二维码
     */
    @Schema(name = "weiXinQRCode", title = "微信二维码", type = "String")
    private String weiXinQRCode;

    /**
     * 支付宝二维码
     */
    @Schema(name = "alipayQRCode", title = "支付宝二维码", type = "String")
    private String alipayQRCode;

    /**
     * 文章封面
     */
    @Schema(name = "articleCover", title = "文章封面", type = "String")
    private String articleCover;

    /**
     * 是否开启聊天室
     */
    @Schema(name = "isReward", title = "是否打赏", type = "Integer")
    private Integer isChatRoom;

    /**
     * WebSocket 地址
     */
    @Schema(name = "websocketUrl", title = "WebSocket 地址", type = "String")
    private String websocketUrl;

    /**
     * 是否开启音乐
     */
    @Schema(name = "isMusicPlayer", title = "是否开启音乐", type = "Integer")
    private Integer isMusicPlayer;

}
