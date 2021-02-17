package com.meidianyi.shop.service.pojo.saas.schedule.cron;

import lombok.Builder;
import lombok.Data;

/**
 * The type Cron define.
 *
 * @author liufei
 * @date 12 /21/19
 */
@Data
@Builder
public class CronDefineParam {
    /**
     * The Id.主键 cron id
     */
    Integer id;
    /**
     * The Class name.定时任务完整类名
     */
    String className;
    /**
     * The Expression.cron表达式
     */
    String expression;
    /**
     * The Description.任务描述
     */
    String description;
    /**
     * The Result.执行结果0:待执行;1:执行中；2已完成；3:执行失败
     */
    Byte result;
    /**
     * The Retries num.失败重试次数默认0不重试
     */
    Byte retriesNum;
    /**
     * The Status.状态1:启用;0:停用
     */
    Byte status;
}
