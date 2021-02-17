package com.meidianyi.shop.thread.es;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * async thread pool config
 * @author 卢光耀
 * @date 2019/10/15 10:07 上午
 *
*/
@Configuration
@EnableAsync
@Slf4j
public class AsyncTaskConfig {




    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_SIZE = Math.max(2,Math.min(CPU_COUNT,4));

    private static final int MAX_POOL_SIZE = CPU_COUNT*2+1;

    private static final int KEEP_ALIVE = 30;

    @Bean(name="esAsyncExecutor")
    public Executor esAsyncExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();

        pool.setCorePoolSize(CORE_SIZE);
        log.info("\nCPU number:{}",CPU_COUNT);
        pool.setMaxPoolSize(MAX_POOL_SIZE);

        pool.setQueueCapacity(10);

        pool.setThreadNamePrefix("ES-Thread-");

        pool.setKeepAliveSeconds(KEEP_ALIVE);

        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        pool.setWaitForTasksToCompleteOnShutdown(true);

        return pool;

    }
}
