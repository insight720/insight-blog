package pers.project.blog.util;

import pers.project.blog.constant.TimeConstant;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * LocalDateTime 工具类
 *
 * @author Luo Fei
 * @date 2022/12/29
 */
public abstract class TimeUtils {

    /**
     * 日期时间格式化
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(TimeConstant.PATTERN);

    /**
     * 获取当前时间日期
     *
     * @return 东八区的当前日期时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(TimeConstant.ZONE_ID);
    }

    /**
     * 格式化日期时间对象为字符串
     * <p>
     * <b>注意：传入 Mapper 方法的形参不必格式化为字符串</b>
     *
     * @param dateTime 日期时间
     * @return 格式化的字符串，不为空
     */
    public static String format(LocalDateTime dateTime) {
        return FORMATTER.format(dateTime);
    }

    /**
     * 解析时间日期字符串为对象
     * <p>
     * <b>注意：字符串应为 yyyy-MM-dd HH:mm:ss 格式</b>
     *
     * @param dateTimeString 时间日期字符串
     * @return 日期时间对象
     */
    public static LocalDateTime parse(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, FORMATTER);
    }

    /**
     * 日期偏移，根据 field 不同加不同值（偏移会修改传入的对象）
     *
     * @param time   日期时间
     * @param number 偏移量，正数为向后偏移，负数为向前偏移
     * @param field  偏移单位，见 {@link ChronoUnit}，不能为 null
     * @return 偏移后的日期时间
     */
    public static LocalDateTime offset(LocalDateTime time, long number, TemporalUnit field) {
        return time.plus(number, field);
    }

    /**
     * 修改为一天的开始时间，例如：2020-02-02T00:00:00,000
     *
     * @param time 日期时间
     * @return 一天的开始时间
     */
    public static LocalDateTime beginOfDay(LocalDateTime time) {
        return time.with(LocalTime.MIN);
    }

    /**
     * 修改为一天的结束时间，例如：2020-02-02T23:59:59,999
     *
     * @param time 日期时间
     * @return 一天的结束时间
     */
    public static LocalDateTime endOfDay(LocalDateTime time) {
        return time.with(LocalTime.MAX);
    }

}
