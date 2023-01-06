package pers.project.blog.constant;

import java.time.ZoneId;

/**
 * 时间相关常量
 *
 * @author Luo Fei
 * @date 2022/12/29
 */
public class TimeConstant {

    /**
     * 时间日期格式
     */
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 北京时间（东八区）
     */
    public static final ZoneId ZONE_ID = ZoneId.of("GMT+08:00");

    /**
     * 日期偏移量，例如：-6 表示 7 天
     */
    public static final Integer OFFSET = -6;

}
