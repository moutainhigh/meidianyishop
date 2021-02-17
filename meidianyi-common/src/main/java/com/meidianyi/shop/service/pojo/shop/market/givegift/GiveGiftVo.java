package com.meidianyi.shop.service.pojo.shop.market.givegift;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 我要送礼
 * @author 孔德成
 * @date 2019/8/19 17:06
 */
@Data
public class GiveGiftVo {

    private Integer   id;
    private String    actName;
    private Timestamp startTime;
    private Timestamp endTime;
    private Short     level;
    private Byte      dueTimeType;
    private Byte      actTypeFirstServed;
    private Byte      actTypeTimingOpen;
    private Byte      actTypeDirectGiving;
    private String    recommendGoodsId;
    private Byte      status;
    private Byte      delFlag;
    private Timestamp createTime;
    private Timestamp updateTime;
}
