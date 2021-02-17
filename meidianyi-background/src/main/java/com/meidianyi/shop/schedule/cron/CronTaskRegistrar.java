package com.meidianyi.shop.schedule.cron;

import com.meidianyi.shop.db.main.tables.records.CronDefineRecord;
import com.meidianyi.shop.service.foundation.jedis.JedisManager;
import com.meidianyi.shop.service.pojo.saas.schedule.cron.CronDefineParam;
import com.meidianyi.shop.service.saas.schedule.cron.MpCronRegistration;
import com.meidianyi.shop.support.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jooq.Field;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Cron task registrar.
 *
 * @author liufei
 * @date 12 /20/19 定时任务管理器，负责创建，删除定时任务
 */
@Slf4j
@Component
public class CronTaskRegistrar implements DisposableBean {
    /**
     * 定时任务启动器
     */
    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private MpCronRegistration mpCronRegistration;

    @Autowired
    private JedisManager jedisManager;

    private final Map<Runnable, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(32);

    /**
     * Gets scheduler.
     *
     * @return the scheduler
     */
    public TaskScheduler getScheduler() {
        return this.taskScheduler;
    }

    /**
     * Enable schedule.
     *
     * @param param the param
     */
    public void enableSchedule(CronDefineParam param) {
        try {
            Class<?> clazz = Class.forName(param.getClassName());
            Runnable task = (Runnable) SpringUtil.getBean(clazz);
            CronDefineRecord record = findByCronKey(param.getClassName());
            addCronTask(task, record.getExpression());
            mpCronRegistration.updateCronDefine(param);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disable schedule.
     *
     * @param param the param
     */
    public void disableSchedule(CronDefineParam param) {
        try {
            Class<?> clazz = Class.forName(param.getClassName());
            Runnable task = (Runnable) SpringUtil.getBean(clazz);
            removeCronTask(task);
            mpCronRegistration.updateCronDefine(param);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute schedule.
     *
     * @param param the param
     */
    public void executeSchedule(CronDefineParam param) {
        try {
            Class<?> clazz = Class.forName(param.getClassName());
            Runnable task = (Runnable) SpringUtil.getBean(clazz);
            task.run();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets cron task.
     *
     * @param className the class name
     * @return the cron task
     */
    public Runnable getCronTask(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return (Runnable) SpringUtil.getBean(clazz);
        } catch (ClassNotFoundException e) {
            log.error("【{}】ClassNotFound：{}", className, ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Add cron task.
     *
     * @param task           the task
     * @param cronExpression the cron expression
     */
    public void addCronTask(Runnable task, String cronExpression) {
        addCronTask(new CronTask(task, cronExpression));
    }

    /**
     * Add cron task.
     *
     * @param cronTask the cron task
     */
    public void addCronTask(CronTask cronTask) {
        if (Objects.nonNull(cronTask)) {
            Runnable task = cronTask.getRunnable();
            // 添加前校验是否已存在，删除之前已存在的旧任务
            if (this.scheduledTasks.containsKey(task)) {
                removeCronTask(task);
            }
            this.scheduledTasks.put(task, scheduleCronTask(cronTask));
        }
    }

    /**
     * Remove cron task.
     *
     * @param task the task
     */
    public void removeCronTask(Runnable task) {
        ScheduledTask scheduledTask = this.scheduledTasks.remove(task);
        if (Objects.nonNull(scheduledTask)) {
            scheduledTask.cancel();
        }
    }

    /**
     * Schedule cron task scheduled task.
     *
     * @param cronTask the cron task
     * @return the scheduled task
     */
    public ScheduledTask scheduleCronTask(CronTask cronTask) {
        ScheduledTask scheduledTask = new ScheduledTask();
        // 启动定时任务，并加入到缓存列表中
        scheduledTask.future = this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
        return scheduledTask;
    }

    @Override
    public void destroy() {
        // 清除定时任务列表，并终止所有定时任务
        this.scheduledTasks.values().forEach(ScheduledTask::cancel);
        this.scheduledTasks.clear();
    }

    /**
     * Find all list.
     *
     * @return the list
     */
    public List<CronDefineRecord> findAll() {
        return mpCronRegistration.findAll();
    }

    /**
     * Find by cron key cron define record.
     *
     * @param key the key
     * @return the cron define record
     */
    public CronDefineRecord findByCronKey(String key) {
        return mpCronRegistration.findByCronKey(key);
    }

    /**
     * Update status.
     *
     * @param result the result
     */
    public void updateStatus(byte result) {
        mpCronRegistration.updateCronDefine(CronDefineParam.builder().result(result).build());
    }

    /**
     * Failed record.
     *
     * @param cronId       the cron id
     * @param executeNum   the execute num
     * @param failedReason the failed reason
     */
    public void failedRecord(int cronId, byte executeNum, String failedReason) {
        mpCronRegistration.failedRecord(cronId, executeNum, failedReason);
    }

    /**
     * Single field from record t.
     *
     * @param <T>    the type parameter
     * @param cronId the cron id
     * @param field  the field
     * @return the t
     */
    public <T> T singleFieldFromRecord(int cronId, Field<T> field) {
        return mpCronRegistration.singleFieldFromRecord(cronId, field);
    }

    /**
     * Lock boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public boolean lock(String key) {
        key = String.valueOf(key.hashCode());
        return jedisManager.addLock(key, key, 60);
    }

    /**
     * Release.
     *
     * @param key the key
     */
    public void release(String key) {
        key = String.valueOf(key.hashCode());
        jedisManager.releaseLock(key, key);
    }
}
