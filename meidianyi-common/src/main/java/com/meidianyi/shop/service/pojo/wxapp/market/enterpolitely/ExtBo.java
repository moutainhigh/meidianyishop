package com.meidianyi.shop.service.pojo.wxapp.market.enterpolitely;

import lombok.Builder;
import lombok.Data;

/**
 * @author liufei
 * @date 12/25/19
 */
@Data
@Builder
public class ExtBo {
    private String title;
    private String bgImg;
    private String customizeImgPath;
    private String orderSn;
    private String activityName;
    private Integer userId;
    /**
     * 积分来源
     */
    private Integer changeWay;
    private Integer goodsId;
}
