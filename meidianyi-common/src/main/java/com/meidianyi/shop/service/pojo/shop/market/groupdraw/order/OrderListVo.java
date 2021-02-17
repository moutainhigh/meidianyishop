package com.meidianyi.shop.service.pojo.shop.market.groupdraw.order;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 郑保乐
 */
@Data
public class OrderListVo {

    private Integer orderId;
    private String orderSn;
    private String goodsName;
    private String goodsImg;
    private Integer userId;
    private String mobile;
    private String consigneeRealName;
    private Boolean isWinDraw;
    private Timestamp createTime;
    private Byte orderStatus;

    private Boolean grouped;
    private Short codeCount;
    private String completeAddress;
}
