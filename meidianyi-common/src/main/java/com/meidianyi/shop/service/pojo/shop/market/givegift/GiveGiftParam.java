package com.meidianyi.shop.service.pojo.shop.market.givegift;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 我要送礼 添加-更新
 * @author 孔德成
 * @date 2019/8/15 17:00
 */
@Data
public class GiveGiftParam {

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
}
