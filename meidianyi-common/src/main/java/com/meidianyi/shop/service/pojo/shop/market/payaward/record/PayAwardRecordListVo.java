package com.meidianyi.shop.service.pojo.shop.market.payaward.record;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2019/10/31 16:55
 */
@Getter
@Setter
public class PayAwardRecordListVo {
    private Integer userId;
    private String username;
    private String mobile;
    private String orderSn;
    private Byte status;
    /**
     * 礼物类型 0 无奖品 1普通优惠卷  2分裂优惠卷 3幸运大抽奖 4 余额 5 商品 6积分 7 自定义
     */
    private Integer giftType;
    private Timestamp createTime;
    private Timestamp updateTime;

}

