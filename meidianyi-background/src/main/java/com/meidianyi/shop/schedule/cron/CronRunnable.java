package com.meidianyi.shop.schedule.cron;

import com.meidianyi.shop.db.main.tables.records.CronDefineRecord;
import com.meidianyi.shop.support.SpringUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.meidianyi.shop.db.main.tables.CronRecord.CRON_RECORD;
import static com.meidianyi.shop.service.shop.store.store.StoreWxService.BYTE_TWO;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;

/**
 * The interface Cron runnable.
 *
 * @author liufei
 * @date 12 /18/19
 */
@Service
public interface CronRunnable extends Runnable {
    /**
     * Execute.
     */
    void execute();

    /**
     * run
     */
    @Override
    @Retryable(value = Throwable.class, maxAttempts = 5, backoff = @Backoff(value = 100))
    default void run() {
        CronTaskRegistrar registrar = SpringUtil.getBean(CronTaskRegistrar.class);
        CronDefineRecord record = registrar.findByCronKey(this.getClass().getName());
        Byte executeNum = registrar.singleFieldFromRecord(record.getId(), CRON_RECORD.EXECUTE_NUM);
        if (Objects.isNull(executeNum) || executeNum < record.getRetriesNum()) {
            if (registrar.lock(record.getClassName())) {
                registrar.updateStatus(BYTE_ONE);
                execute();
                registrar.updateStatus(BYTE_TWO);
                registrar.release(record.getClassName());
            }
        }
    }

    /**
     * Recover job.当全部尝试都失败时执行
     *
     * @param throwable the throwable
     */
    @Recover
    default void recoverJob(Throwable throwable) {
        CronTaskRegistrar repository = SpringUtil.getBean(CronTaskRegistrar.class);
        CronDefineRecord record = repository.findByCronKey(this.getClass().getName());
        // 停用定时任务
        repository.removeCronTask(repository.getCronTask(this.getClass().getName()));
        // 记录异常原因
        repository.failedRecord(record.getId(), (byte) (record.getRetriesNum() + BYTE_ONE), ExceptionUtils.getStackTrace(throwable));
    }
}
