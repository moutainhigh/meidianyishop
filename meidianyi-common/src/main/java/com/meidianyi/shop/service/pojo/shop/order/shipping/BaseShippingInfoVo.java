package com.meidianyi.shop.service.pojo.shop.order.shipping;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author: 王兵兵
 * @create: 2019-10-17 13:52
 **/
@Getter
@Setter
public class BaseShippingInfoVo {
    protected String orderSn;
    /**物流单号*/
    protected String shippingNo;
    /**快递公司id*/
    protected Byte shippingId;
    /**物流名称*/
    protected String shippingName;
    /**发货时间*/
    protected Timestamp shippingTime;
    /**确认收货时间*/
    protected Timestamp confirmTime;
    /**
     * 配送人操作平台
     */
    private Byte      shippingPlatform;
    /**
     * 配送人
     */
    private String   shippingAccountName;
    /**
     * 绑定用户
     */
    private Integer   shippingUserId;
    /**
     * 配送人电话
     */
    private String    shippingMobile;
    private Integer   shippingAccountId;
    private Timestamp createTime;
    private Integer   confirmAccountId;
    private Byte      confirmPlatform;
    private String    confirmMobile;
    private Integer   confirmUserId;
}
