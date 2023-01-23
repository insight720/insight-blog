package pers.project.blog.schedule;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pers.project.blog.entity.UniqueViewEntity;
import pers.project.blog.mapper.UniqueViewMapper;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.util.TimeUtils;

import java.time.temporal.ChronoUnit;

import static pers.project.blog.constant.RedisConstant.UNIQUE_VISITOR;
import static pers.project.blog.constant.RedisConstant.VISITOR_AREA;
import static pers.project.blog.constant.TimeConstant.BEGIN_OF_DAY_CRON;
import static pers.project.blog.constant.TimeConstant.BEIJING_TIME;

/**
 * 访问量统计定时任务
 *
 * @author Luo Fei
 * @date 2023/1/16
 */
@Async
@Component
public class UniqueViewSchedule {

    private final UniqueViewMapper uniqueViewMapper;

    public UniqueViewSchedule(UniqueViewMapper uniqueViewMapper) {
        this.uniqueViewMapper = uniqueViewMapper;
    }

    /**
     * 新的一天开始的时候保存昨日的用户量
     */
    @Scheduled(cron = BEGIN_OF_DAY_CRON, zone = BEIJING_TIME)
    public void saveUniqueView() {
        // 获取每天用户量
        Long count = RedisUtils.sCard(UNIQUE_VISITOR);


        // 获取昨天的日期写入
        UniqueViewEntity uniqueViewEntity = UniqueViewEntity.builder()
                .viewsCount(count.intValue())
                .createTime(TimeUtils.offset(TimeUtils.now(), -1L, ChronoUnit.DAYS))
                .build();
        uniqueViewMapper.insert(uniqueViewEntity);
    }

    // TODO: 2023/1/16 为何删除，目的不明

    /**
     * 每天开始后一分钟清除访问量统计和游客区域统计
     */
    @Scheduled(cron = "0 1 0 * * ?", zone = BEIJING_TIME)
    public void clear() {
        RedisUtils.del(UNIQUE_VISITOR);
        RedisUtils.del(VISITOR_AREA);
    }

}
