package pers.project.blog.schedule;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pers.project.blog.dto.bloginfo.DailyVisitDTO;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.util.TimeUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static pers.project.blog.constant.GenericConst.*;
import static pers.project.blog.constant.RedisConst.DAILY_VISIT_PREFIX;
import static pers.project.blog.constant.TimeConst.BEGIN_OF_DAY_CRON;
import static pers.project.blog.constant.TimeConst.BEIJING_TIME;

/**
 * 访问量统计定时任务
 *
 * @author Luo Fei
 * @date 2023/1/16
 */
@Component
public class VisitCountSchedule {

    /**
     * 周访问量数据的本地缓存
     */
    private static final LinkedList<DailyVisitDTO> WEEKLY_VISIT = new LinkedList<>();

    /**
     * 避免内部封装的 RedisTemplate 为 null
     */
    @Resource
    private RedisUtils redisUtils;

    @SuppressWarnings("all")
    @PostConstruct
    private void initialize() {
        LocalDate date = TimeUtils.today().minusDays(SEVEN);
        for (int i = ZERO; i < SEVEN; i++) {
            String dayKey = DAILY_VISIT_PREFIX + date;
            Object viewsCount = Optional.ofNullable
                    (redisUtils.get(dayKey)).orElse(ZERO);
            DailyVisitDTO dailyVisit = DailyVisitDTO.builder()
                    .day(date.toString())
                    .viewsCount((Integer) viewsCount)
                    .build();
            WEEKLY_VISIT.addLast(dailyVisit);
            date = date.plusDays(ONE);
        }
    }

    public static List<DailyVisitDTO> getWeeklyVisit() {
        return Collections.unmodifiableList(WEEKLY_VISIT);
    }

    @Async
    @Scheduled(cron = BEGIN_OF_DAY_CRON, zone = BEIJING_TIME)
    public void updateWeeklyVisitCount() {
        LocalDate yesterday = TimeUtils.today().minusDays(ONE);
        String yesterdayKey = DAILY_VISIT_PREFIX + yesterday;
        Object viewsCount = Optional.ofNullable
                (RedisUtils.get(yesterdayKey)).orElse(ZERO);
        DailyVisitDTO dailyVisit = DailyVisitDTO.builder()
                .day(yesterday.toString())
                .viewsCount((Integer) viewsCount)
                .build();
        WEEKLY_VISIT.removeFirst();
        WEEKLY_VISIT.addLast(dailyVisit);
    }

}
