package com.meidianyi.shop.service.pojo.saas.schedule;

import lombok.Getter;

import java.sql.Timestamp;

/**
 * 基类
 * @author 卢光耀
 * @date 2019-08-14 14:52
 *
*/
@Getter
public class BaseTaskJob  {
    /** 店铺ID */
    protected Integer shopId;

    /** MQ消息内容 */
    protected String content ;

    /** 任务状态：0待执行,1执行中,2已完成 */
    protected Byte status = TaskJobsConstant.STATUS_NEW;

    /** 任务进度：0-100 */
    protected Byte progress = 0;

    /** 执行类型：任务类型标识 */
    protected Integer executionType ;

    /** task任务类型(立刻执行、定时执行、循环执行) */
    protected Byte type;

    /** 周期开始日期 */
    protected Timestamp startTime ;

    /** 周期结束日期 */
    protected Timestamp endTime ;

    /** 轮循间隔(单位:秒) */
    protected Integer cycle = 60*60*3;

    /** 下次执行时间 */
    protected Timestamp nextExecuteTime;

    /** 反序列化对应的类全称 */
    protected String className;

}
