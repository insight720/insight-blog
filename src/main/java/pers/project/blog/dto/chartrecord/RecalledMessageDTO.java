package pers.project.blog.dto.chartrecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 撤回的消息数据
 *
 * @author Luo Fei
 * @version 2023/1/13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecalledMessageDTO {

    /**
     * 消息 ID
     */
    private Integer id;

    /**
     * 是否为语音
     */
    private Boolean isVoice;

}
