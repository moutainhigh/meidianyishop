package com.meidianyi.shop.service.pojo.shop.market.seckill;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-08-07 14:18
 **/
@Data
public class SeckillDetailPageListQueryVo {
    private String goodsName;

    private Integer userId;

    private String username;

    private String mobile;

    private String orderSn;

    private Timestamp createTime;

    private Integer goodsAmount;
}
