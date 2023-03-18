package pers.project.blog.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异步调用和定时任务的配置属性
 *
 * @author Luo Fei
 * @version 2022/12/24
 */
@Data
@ConfigurationProperties(prefix = "blog.async-schedule")
public class AsyncScheduleProperties {

    /**
     * 要保留在池中的核心线程数，即使它们处于空闲状态。
     * <p>
     * （仅用于 TaskExecutor）
     */
    private Integer corePoolSize;

    /**
     * 保留在池中的核心线程数，即使它们处于空闲状态。
     * <p>
     * （仅用于 TaskScheduler）
     */
    private Integer schedulePoolSize;

    /**
     * 当队列中存放的任务数量达到队列容量后，池中的最大线程数量。
     * <p>
     * （仅用于 TaskExecutor）
     */
    private Integer maxPoolSize;

    /**
     * 线程数大于核心线程数时，多余的空闲线程在终止之前等待新任务的最长秒数。
     * <p>
     * （仅用于 TaskExecutor）
     */
    private Integer keepAliveSeconds;

    /**
     * 池中运行的线程数量达到核心线程数后，任务队列的容量。
     * <p>
     * （仅用于 TaskExecutor）
     */
    private Integer queueCapacity;

    /**
     * 线程池在关闭时是否等待执行所有任务。
     * <p>
     * （默认为仅不中断正在运行的任务）
     */
    private Boolean awaitForTasksToCompleteOnShutdown;

    /**
     * 线程池在关闭时应等待任务执行完成的最大秒数。
     */
    private Integer awaitTerminationSeconds;

    /**
     * 异步调用线程名称前缀
     */
    private String executorThreadNamePrefix = "taskExecutor-";

    /**
     * 定时任务线程名称前缀
     */
    private String schedulerThreadNamePrefix = "taskExecutor-";

}
