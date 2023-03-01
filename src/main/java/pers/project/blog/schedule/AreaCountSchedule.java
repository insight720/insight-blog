package pers.project.blog.schedule;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import net.dreamlu.mica.ip2region.core.IpInfo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pers.project.blog.dto.userauth.AreaCountDTO;
import pers.project.blog.entity.UserAuth;
import pers.project.blog.mapper.UserAuthMapper;
import pers.project.blog.util.ConvertUtils;
import pers.project.blog.util.RedisUtils;
import pers.project.blog.util.StrRegexUtils;
import pers.project.blog.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pers.project.blog.constant.RedisConst.*;
import static pers.project.blog.constant.TimeConst.*;

/**
 * 地域分布统计定时任务
 *
 * @author Luo Fei
 * @date 2023/1/16
 */
@Component
public class AreaCountSchedule {

    public static List<AreaCountDTO> getVisitorAreaCount() {
        return ConvertUtils.castList(RedisUtils.get(VISITOR_AREA));
    }

    public static List<AreaCountDTO> getUserAreaCount() {
        return ConvertUtils.castList(RedisUtils.get(USER_AREA));
    }

    @Resource
    private UserAuthMapper userAuthMapper;


    /**
     * 避免内部封装的 RedisTemplate 为 null
     */
    @Resource
    private RedisUtils redisUtils;
    /**
     * 避免内部封装的 Ip2regionSearcher 为 null
     */
    @Resource
    private WebUtils webUtils;

    @PostConstruct
    private void initialize() {
        updateVisitorAreaCount();
        updateUserAreaCount();
    }

    @Async
    @Scheduled(cron = BEGIN_OF_HOUR_CRON, zone = BEIJING_TIME)
    @SuppressWarnings("all")
    public void updateVisitorAreaCount() {
        List<AreaCountDTO> areaCountDTOList = redisUtils.sMembers(PROVINCE)
                .stream()
                .map(province -> AreaCountDTO.builder()
                        .name((String) province)
                        .value(redisUtils.pfCount(VISITOR_PROVINCE_PREFIX + province))
                        .build())
                .collect(Collectors.toList());
        redisUtils.set(VISITOR_AREA, areaCountDTOList);
    }

    @Async
    @Scheduled(cron = EVERY_TWO_HOURS_CRON, zone = BEIJING_TIME)
    @SuppressWarnings("all")
    public void updateUserAreaCount() {
        // 统计用户地域分布数据
        Map<String, Long> userAreaCountMap = new LambdaQueryChainWrapper<>(userAuthMapper)
                .select(UserAuth::getIpAddress)
                .list().stream()
                // 根据 IP 获取省份，不能直接获取 IP 来源
                .map(userAuth -> webUtils.getInfo
                        (userAuth.getIpAddress(), IpInfo::getProvince))
                .filter(StrRegexUtils::isNotBlank)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        // 转换数据格式并保存
        List<AreaCountDTO> areaCountDTOList = userAreaCountMap
                .entrySet().stream()
                .map(entry -> AreaCountDTO.builder()
                        .name(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .collect(Collectors.toList());
        redisUtils.set(USER_AREA, areaCountDTOList);
    }

}
