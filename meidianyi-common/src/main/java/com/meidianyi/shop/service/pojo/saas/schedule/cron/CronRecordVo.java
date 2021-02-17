package com.meidianyi.shop.service.pojo.saas.schedule.cron;

import lombok.Data;

/**
 * The type Cron record.
 *
 * @author liufei
 * @date 12 /21/19
 */
@Data
public class CronRecordVo {
    /**
     * The Id.主键id
     */
    Integer id;

    /**
     * The Cron id.定时任务id
     */
    Integer cronId;

    /**
     * The Execute num.执行次数（小于等于失败重试次数）
     */
    Byte executeNum;

    /**
     * The Failed reason.最后一次执行失败原因
     */
    String failedReason;
}
