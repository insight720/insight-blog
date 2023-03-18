package pers.project.blog.dto.chartrecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket 消息数据
 *
 * @author Luo Fei
 * @version 2023/1/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageDTO {

    /**
     * 类型
     */
    private Integer type;

    /**
     * 数据
     */
    private Object data;

}
