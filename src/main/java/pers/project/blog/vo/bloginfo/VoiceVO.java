package pers.project.blog.vo.bloginfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * 音频数据
 *
 * @author Luo Fei
 * @version 2023/1/12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoiceVO {

    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private Integer type;

    /**
     * 文件
     */
    @Schema(description = "文件")
    private MultipartFile file;

    /**
     * 用户 ID
     */
    @Schema(description = "用户 ID")
    private Integer userId;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickname;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private String avatar;

    /**
     * 聊天内容
     */
    @Schema(description = "聊天内容")
    private String content;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 用户登录 IP
     */
    @Schema(description = "用户登录ip")
    private String ipAddress;

    /**
     * IP 来源
     */
    @Schema(description = "ip来源")
    private String ipSource;

}
