package com.meidianyi.shop.schedule.cron;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;

/**
 * The type Cron initialize.
 *
 * @author liufei
 * @date 12 /20/19
 */
@Slf4j
@Service
public class CronInitialize implements CommandLineRunner {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private CronTaskRegistrar cronRepository;

    @Autowired
    private CronTaskRegistrar cronTaskRegistrar;

    @Override
    public void run(String... args) {
        // 启动初始化定时任务
        cronRepository.findAll().forEach((r) -> {
            try {
                Class<?> clazz = Class.forName(r.getClassName());
                Runnable task = (Runnable) context.getBean(clazz);
                Assert.isAssignable(CronRunnable.class, task.getClass(), "定时任务类必须实现CronRunnable接口");
//                cronRepository.failedRecord(r.getId(), (byte) (r.getRetriesNum() + BYTE_ONE), "定时任务类必须实现CronRunnable接口");
//                log.error("定时任务初始化异常：定时任务类【{}】必须实现CronRunnable接口", r.getClassName());
                cronTaskRegistrar.addCronTask(task, r.getExpression());
            } catch (ClassNotFoundException e) {
                cronRepository.failedRecord(r.getId(), (byte) (r.getRetriesNum() + BYTE_ONE), "未找到定时任务执行类 " + r.getClassName());
                log.error("定时任务初始化异常：未找到定时任务执行类【{}】", r.getClassName());
            } catch (BeansException e) {
                cronRepository.failedRecord(r.getId(), (byte) (r.getRetriesNum() + BYTE_ONE), r.getClassName() + " 未纳入到spring管理");
                log.error("定时任务初始化异常：【{}】未纳入到spring管理", r.getClassName());
            }
        });
    }
}
