package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 活动预告类
 * @author 李晓冰
 * @date 2020年04月25日
 */
@Data
public class GoodsActivityAnnounceMpVo {
    /**预告活动类型*/
    private Byte activityType;
    /**预告活动时间*/
    private Timestamp startTime;
    /**预告活动价格*/
    private BigDecimal realPrice;
}
