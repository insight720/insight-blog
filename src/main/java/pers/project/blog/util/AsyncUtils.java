package pers.project.blog.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * 异步工具类
 *
 * @author Luo Fei
 * @date 2023/1/27
 */
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AsyncUtils {

    private static TaskExecutor taskExecutor;

    @Autowired
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        AsyncUtils.taskExecutor = taskExecutor;
    }

    /**
     * 返回一个新的 CompletableFuture，在默认执行器中运行给定异步任务结束后完成。
     *
     * @param runnable 完成返回的 CompletableFuture 之前需要进行的操作
     * @return 新的 CompletableFuture
     */
    @NotNull
    @SuppressWarnings("all")
    public static CompletableFuture<Void> runAsync(@NotNull Runnable runnable) {
        return CompletableFuture.runAsync(runnable, taskExecutor)
                .exceptionally(throwable -> {
                    throw new RuntimeException("异步调用异常", throwable);
                });
    }

    /**
     * 返回一个新的 CompletableFuture，在默认执行器中运行给定异步任务结束后完成，
     * 该任务的值是通过调用给定的 Supplier 获得的。
     *
     * @param supplier 完成返回的 CompletableFuture 之前需要提供返回值的 Supplier
     * @return 新的 CompletableFuture
     */
    @NotNull
    public static <T> CompletableFuture<T> supplyAsync(@NotNull Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, taskExecutor);
    }

    /**
     * 等待 CompletableFuture 完成，然后返回其结果。
     *
     * @param future  异步任务
     * @param message 异常消息
     * @return 任务结果
     */
    public static <T> T get(@NotNull CompletableFuture<T> future, @NotNull String message) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException cause) {
            throw new RuntimeException(message, cause);
        }
    }

    /**
     * 等待 CompletableFuture 完成，然后返回其结果。
     *
     * @param future 异步任务
     * @return 任务结果
     */
    public static <T> T get(@NotNull CompletableFuture<T> future) {
        return get(future, "异步调用获取结果异常");
    }

}
