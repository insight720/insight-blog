package pers.project.blog.dto.talk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台说说
 *
 * @author Luo Fei
 * @date 2023/1/11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTalkDTO {

    /**
     * 说说 ID
     */
    private Integer id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 说说内容
     */
    private String content;

    /**
     * 图片 URL（JSON 数组）
     */
    private String images;

    /**
     * 图片 URL
     */
    private List<String> imgList;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
