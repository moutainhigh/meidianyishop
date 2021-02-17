package com.meidianyi.shop.service.pojo.wxapp.order.record;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * 商品购买记录 简单参数
 * @author 孔德成
 * @date 2020/3/31
 */
@Getter
@Setter
public class GoodsOrderRecordSmallVo {

    /**用户名*/
    private String username;
    /**用户头像*/
    private String userAvatar;
    /**下单时间*/
    private Timestamp createTime;
}
