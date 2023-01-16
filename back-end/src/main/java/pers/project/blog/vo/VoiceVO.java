package pers.project.blog.vo;

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
 * @date 2023/1/12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "音频数据")
public class VoiceVO {

    /**
     * 消息类型
     */
    @Schema(name = "type", description = "消息类型", type = "Integer")
    private Integer type;

    /**
     * 文件
     */
    @Schema(name = "file", description = "文件", type = "MultipartFile")
    private MultipartFile file;

    /**
     * 用户 ID
     */
    @Schema(name = "userId", description = "用户 ID", type = "Integer")
    private Integer userId;

    /**
     * 用户昵称
     */
    @Schema(name = "nickname", description = "用户昵称", type = "String")
    private String nickname;

    /**
     * 用户头像
     */
    @Schema(name = "avatar", description = "用户头像", type = "String")
    private String avatar;

    /**
     * 聊天内容
     */
    @Schema(name = "content", description = "聊天内容", type = "String")
    private String content;

    /**
     * 创建时间
     */
    @Schema(name = "createTime", description = "创建时间", type = "Date")
    private Date createTime;

    /**
     * 用户登录 IP
     */
    @Schema(name = "ipAddress", description = "用户登录ip", type = "String")
    private String ipAddress;

    /**
     * IP 来源
     */
    @Schema(name = "ipSource", description = "ip来源", type = "String")
    private String ipSource;

}
