package pers.project.blog.schedule;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pers.project.blog.dto.UserAreaDTO;
import pers.project.blog.entity.UserAuthEntity;
import pers.project.blog.mapper.UserAuthMapper;
import pers.project.blog.util.ConversionUtils;
import pers.project.blog.util.RedisUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pers.project.blog.constant.AddressConstant.*;
import static pers.project.blog.constant.RedisConstant.USER_AREA;
import static pers.project.blog.constant.TimeConstant.BEGIN_OF_HOUR_CRON;
import static pers.project.blog.constant.TimeConstant.BEIJING_TIME;

/**
 * 用户相关定时任务
 *
 * @author Luo Fei
 * @date 2023/1/16
 */
@Async
@Component
public class UserAuthSchedule {

    private final UserAuthMapper userAuthMapper;

    public UserAuthSchedule(UserAuthMapper userAuthMapper) {
        this.userAuthMapper = userAuthMapper;
    }

    /**
     * 每小时统计用户区域分布
     */
    @Scheduled(cron = BEGIN_OF_HOUR_CRON, zone = BEIJING_TIME)
    public void getUserAreaStatistic() {
        // 统计用户地域分布
        Map<String, Long> userAreaCountMap = new LambdaQueryChainWrapper<>(userAuthMapper)
                .select(UserAuthEntity::getIpSource)
                .list()
                .stream()
                .map(UserAuthEntity::getIpSource)
                .map(ipSource -> {
                    if (StringUtils.isNotBlank(ipSource)) {
                        return ipSource.substring(0, 2).replaceAll(PROVINCE, "")
                                .replaceAll(CITY, "");
                    }
                    return UNKNOWN;
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // 转换格式
        List<UserAreaDTO> userAreaDTOList = userAreaCountMap.entrySet()
                .stream()
                .map(entry -> UserAreaDTO.builder()
                        .name(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .collect(Collectors.toList());
        RedisUtils.set(USER_AREA, ConversionUtils.getJson(userAreaDTOList));
    }

}
