package pers.project.blog.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pers.project.blog.configuration.property.AsynchronousProperties;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步调用配置类
 *
 * @author Luo Fei
 * @date 2022/12/24
 */
@Slf4j
@EnableAsync
@Configuration
@EnableConfigurationProperties(AsynchronousProperties.class)
public class AsynchronousConfiguration extends AsyncConfigurerSupport {

    private final AsynchronousProperties properties;

    public AsynchronousConfiguration(AsynchronousProperties properties) {
        this.properties = properties;
    }

    /**
     * 提供异步调用使用的线程池
     *
     * @param properties 参数外部配置
     * @return {@link Executor} 线程池执行器
     */
    @Bean
    @Primary
    public TaskExecutor taskExecutor() {
        // TODO: 2022/12/24 线程池参数要根据服务器设置
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setWaitForTasksToCompleteOnShutdown(properties.getAwaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("@Async 异步调用异常：", ex);
            log.error("调用方法：{}", method.toGenericString());
            log.error("调用参数：{}", Arrays.deepToString(params));
        };
    }
}
