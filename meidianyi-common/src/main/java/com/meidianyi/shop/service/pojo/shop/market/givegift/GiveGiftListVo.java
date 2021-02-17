package com.meidianyi.shop.service.pojo.shop.market.givegift;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2019/8/16 17:49
 */
@Data
public class GiveGiftListVo {


    /**
     * id
     */
    private Integer   id;
    /**
     * 活动名称
     */
    private String    actName;
    /**
     * 是否永久有效 0 否 1 永久有效
     */
    private Byte      dueTimeType;
    /**
     * 活动开始时间
     */
    private Timestamp startTime;
    /**
     * 结束时间
     */
    private Timestamp endTime;
    /**
     * 优先级
     */
    private Short     level;
    /**
     * 活动玩法 :
     * 先到先得 1开启
     */
    private Byte      actTypeFirstServed;
    /**
     * 定时开奖  1开启
     */
    private Byte      actTypeTimingOpen;
    /**
     * 直接送礼 1开启
     */
    private Byte      actTypeDirectGiving;
    /**
     * 指定商品可用
     */
    private String    recommendGoodsId;
    /**
     * 状态 1启用 0 停用
     */
    private Byte      status;
    /**
     * 发送礼物
     */
    private Integer sendOrderNumber;
    /**
     * 接受礼物
     */
    private Integer getOrderMunber;

    /**
     * 创建
     */
    private Timestamp createTime;
    /**
     *
     */
    private Timestamp updateTime;

    /**
     * 当前活动状态：1进行中，2未开始，3已结束，4已停用
     */
    private Byte currentState;



}
