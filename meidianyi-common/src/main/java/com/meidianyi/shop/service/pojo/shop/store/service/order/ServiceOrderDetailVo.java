package com.meidianyi.shop.service.pojo.shop.store.service.order;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 王兵兵
 *
 * 2019年7月17日
 */
@Data
public class ServiceOrderDetailVo {


    /**
     *预约订单信息
	 */
	private Integer    orderId;
    private String     orderSn;
    /**
     * 订单状态 0：待服务，1：已取消，2：已完成，3待付款
     */
    private Byte       orderStatus;
    private String     orderStatusName;
    private String     subscriber;
    private String     mobile;
    private String     technicianName;
    private String     serviceDate;
    private String     servicePeriod;
    private String     addMessage;
    private String     adminMessage;
    /**   订单总金额 */
    private BigDecimal orderAmount;
    /**   订单应付金额 */
    private BigDecimal moneyPaid;
    /**   券抵扣金额 */
    private BigDecimal discount;
    /**   用户余额抵扣金额 */
    private BigDecimal useAccount;
    /**   会员卡余额抵扣金额 */
    private BigDecimal memberCardBalance;

    private Timestamp  finishedTime;
    private Byte       verifyType;
    private String     verifyCode;
    private String     verifyAdmin;
    /**
     * 创建类型 0用户创建 1后台
     */
    private Byte       type;
    /**
     * 核销支付方式 0门店买单 1会员卡 2余额
     */
    private Byte       verifyPay;
    private Timestamp  createTime;


    /**
     *服务信息
     */
    private String     serviceName;
    private String     servicePrice;
    private String     serviceSubsist;
    private String     serviceImg;

    private Integer serviceId;
    private Integer storeId;
    private String storeName;
}
