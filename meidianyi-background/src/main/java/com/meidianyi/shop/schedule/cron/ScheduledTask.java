package com.meidianyi.shop.schedule.cron;

import java.util.concurrent.ScheduledFuture;

/**
 * The type Scheduled task.
 *
 * @author liufei
 * @date 12 /20/19
 */
public final class ScheduledTask {

    /**
     * The Future.
     */
    volatile ScheduledFuture<?> future;

    /**
     * 取消定时任务
     */
    void cancel() {
        ScheduledFuture<?> future = this.future;
        if (future != null) {
            future.cancel(true);
        }
    }
}
