package pers.project.blog.util;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static pers.project.blog.constant.TimeConst.*;

/**
 * 时间工具类
 *
 * @author Luo Fei
 * @version 2022/12/29
 */
@SuppressWarnings("unused")
public abstract class TimeUtils {

    /**
     * 日期时间格式化
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER;

    public static DateTimeFormatter getDateTimeFormatter() {
        return DATE_TIME_FORMATTER;
    }

    /**
     * 日期格式化
     */
    private static final DateTimeFormatter DATE_FORMATTER;

    public static DateTimeFormatter getDateFormatter() {
        return DATE_FORMATTER;
    }

    static {
        DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    }

    /**
     * 获取当前日期时间
     *
     * @return 东八区的当前日期时间
     */
    @NotNull
    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE_ID);
    }

    /**
     * 获取当前日期
     *
     * @return 东八区的当前日期
     */
    @NotNull
    public static LocalDate today() {
        return LocalDate.now(ZONE_ID);
    }

    /**
     * 格式化日期时间对象为字符串
     * <p>
     * <b>注意：传入 Mapper 方法的形参不必格式化</b>
     *
     * @param dateTime 日期时间
     * @return 格式化的字符串，不为空
     */
    @NotNull
    public static String format(@NotNull LocalDateTime dateTime) {
        return DATE_TIME_FORMATTER.format(dateTime);
    }

    /**
     * 格式化日期对象为字符串
     * <p>
     * <b>注意：传入 Mapper 方法的形参不必格式化为字符串</b>
     *
     * @param date 日期
     * @return 格式化的字符串，不为空
     */
    @NotNull
    public static String format(@NotNull LocalDate date) {
        return DATE_FORMATTER.format(date);
    }

    /**
     * 解析时间日期字符串为对象
     * <p>
     * <b>注意：字符串应为 yyyy-MM-dd HH:mm:ss 格式</b>
     *
     * @param dateTimeString 时间日期字符串
     * @return 日期时间对象
     */
    @NotNull
    public static LocalDateTime parse(@NotNull String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
    }

    /**
     * 日期偏移，根据 field 不同加不同值（偏移会修改传入的对象）
     *
     * @param time   日期时间
     * @param number 偏移量，正数为向后偏移，负数为向前偏移
     * @param field  偏移单位
     * @return 偏移后的日期时间
     */
    @NotNull
    public static LocalDateTime offset(@NotNull LocalDateTime time,
                                       long number, @NotNull ChronoUnit field) {
        return time.plus(number, field);
    }

    /**
     * 修改为一天的开始时间，例如：2020-02-02T00:00:00,000
     *
     * @param time 日期时间
     * @return 一天的开始时间
     */
    @NotNull
    public static LocalDateTime beginOfDay(@NotNull LocalDateTime time) {
        return time.with(LocalTime.MIN);
    }

    /**
     * 修改为一天的结束时间，例如：2020-02-02T23:59:59,999
     *
     * @param time 日期时间
     * @return 一天的结束时间
     */
    @NotNull
    public static LocalDateTime endOfDay(@NotNull LocalDateTime time) {
        return time.with(LocalTime.MAX);
    }

}
