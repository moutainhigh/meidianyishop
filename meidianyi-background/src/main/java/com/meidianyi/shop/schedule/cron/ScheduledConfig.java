package com.meidianyi.shop.schedule.cron;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * The type Scheduled config.
 *
 * @author liufei
 * @date 12 /18/19
 */
@Configuration
@EnableScheduling
@EnableAsync
@EnableRetry
public class ScheduledConfig implements SchedulingConfigurer {
    @Autowired
    private ApplicationContext context;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 项目启动加载db中定时任务注册到spring中
        /*cronRepository.findAll().forEach((r) -> {
            try {
                Class<?> clazz = Class.forName(r.getClassName());
                Runnable task = (Runnable) context.getBean(clazz);
                Assert.isAssignable(CronRunnable.class, task.getClass(), "定时任务类必须实现ScheduledOfTask接口");
                taskRegistrar.addTriggerTask(task,
                    triggerContext -> new CronTrigger(r.getExpression()).nextExecutionTime(triggerContext)
                );
            } catch (ClassNotFoundException e) {
                cronRepository.failedRecord(r.getId(), (byte) (r.getRetriesNum() +BYTE_ONE), "未找到定时任务执行类"  + r.getClassName());
                return;
            } catch (BeansException e) {
                cronRepository.failedRecord(r.getId(), (byte) (r.getRetriesNum() +BYTE_ONE), r.getClassName() + "未纳入到spring管理");
            }
        });*/
    }

    /**
     * Task scheduler task scheduler.
     *
     * @return the task scheduler
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 定时任务执行线程池核心线程数
        taskScheduler.setPoolSize(4);
        taskScheduler.setRemoveOnCancelPolicy(true);
        taskScheduler.setThreadNamePrefix("TaskSchedulerThreadPool-");
        return taskScheduler;
    }
}
