package pers.project.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import pers.project.blog.exception.GlobalExceptionHandler;
import pers.project.blog.property.AsyncScheduleProperties;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步调用和定时任务配置类
 *
 * @author Luo Fei
 * @version 2022/12/24
 */
@Slf4j
@Configuration
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
@EnableConfigurationProperties(AsyncScheduleProperties.class)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class AsyncScheduleConfig
        extends AsyncConfigurerSupport implements SchedulingConfigurer {

    @Resource
    private AsyncScheduleProperties properties;

    /**
     * 提供异步调用使用的线程池
     */
    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(properties.getExecutorThreadNamePrefix());
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setWaitForTasksToCompleteOnShutdown
                (properties.getAwaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 提供定时任务使用的线程池
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setThreadNamePrefix(properties.getSchedulerThreadNamePrefix());
        executor.setPoolSize(properties.getSchedulePoolSize());
        executor.setWaitForTasksToCompleteOnShutdown
                (properties.getAwaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 设置异步调用的默认线程池
     */
    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    /**
     * 设置异步未捕获异常处理程序
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (Throwable ex, Method method, Object[] params) -> {
            log.error("异步调用异常: ", ex);
            log.error("调用方法: {}", method.toGenericString());
            log.error("方法参数: {}", Arrays.deepToString(params));
            GlobalExceptionHandler.tryNotify(ex);
        };
    }

    /**
     * 设置定时任务的默认线程池
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
    }

}
