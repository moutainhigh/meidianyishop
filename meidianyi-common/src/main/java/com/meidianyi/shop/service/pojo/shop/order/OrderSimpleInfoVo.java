package com.meidianyi.shop.service.pojo.shop.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 订单简略信息
 * @author 王帅
 */
@Data
public class OrderSimpleInfoVo {
    private String orderSn;
    private Byte orderStatus;
    /** 收件人姓名 */
    private String consignee;
    /** 收件人手机 */
    private String mobile;
    /** 主支付方式 */
    @JsonIgnore
    private String payCode;
    /** 支付方式列表 */
    private List<Byte> payCodeList;
    /** 配送方式:0 快递 1 自提 */
    private Byte deliverType;
    /** 下单时间 */
    private Timestamp createTime;
    /**完整收货地址*/
    private String completeAddress;
    private Integer userId;
    private String username;
    private String goodsType;
    /**下单人手机号*/
    private String userMobile;
}
