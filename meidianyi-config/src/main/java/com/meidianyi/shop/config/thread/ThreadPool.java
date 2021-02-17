package com.meidianyi.shop.config.thread;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author luguangyao
 */
@Component
@EnableAsync
@Slf4j
public class ThreadPool {

    /**
     * cpu的核心数
     */
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 核心线程数
     */
    private static final int CORE_SIZE = Math.max(2,Math.min(CPU_COUNT,4));
    /**
     * 最大线程数
     */
    private static final int MAX_POOL_SIZE = CPU_COUNT*2+1;
    /**
     * 线程活跃时间
     */
    private static final int KEEP_ALIVE = 30;

    @Bean(name="vpuThreadPool")
    public ThreadPoolTaskExecutor esAsyncExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();

        pool.setCorePoolSize(CORE_SIZE);
        log.info("\nCPU number:{}",CPU_COUNT);
        pool.setMaxPoolSize(MAX_POOL_SIZE);

        pool.setQueueCapacity(1000);

        pool.setThreadNamePrefix("【ThreadPool】-");

        pool.setKeepAliveSeconds(KEEP_ALIVE);

        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        pool.setWaitForTasksToCompleteOnShutdown(true);

        pool.initialize();

        return pool;
    }
}
