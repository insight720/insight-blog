package pers.project.blog.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pers.project.blog.configuration.property.AsynchronousProperties;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步调用配置类
 *
 * @author Luo Fei
 * @date 2022/12/24
 */
@Configuration
@EnableConfigurationProperties(AsynchronousProperties.class)
public class AsynchronousConfiguration {

    /**
     * 提供异步调用使用的线程池
     *
     * @param properties 参数外部配置
     * @return {@link Executor} 线程池执行器
     */
    @Bean
    public Executor executor(AsynchronousProperties properties) {
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

}
