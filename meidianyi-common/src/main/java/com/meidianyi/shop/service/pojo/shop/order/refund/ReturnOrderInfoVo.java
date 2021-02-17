package com.meidianyi.shop.service.pojo.shop.order.refund;

import com.meidianyi.shop.service.pojo.shop.order.OrderInfoVo;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @author wangshuai
 */
@Data
public class ReturnOrderInfoVo {
    private String[] orderType;
	private String shippingCode;
	/** 优先级退款信息 */
	private Map<String, BigDecimal> calculateMoney;
	/** 最后一次操作人类型 */
	private Byte operatorLastType;
	private List<OperatorRecord> operatorRecord;
	private Long returnMoneyDays;
	private Long returnAddressDays;
    private Long returnShippingDays;
	private Long returnAuditPassNotShoppingDays;
	private BigDecimal canReturnShippingFee;
	/** order info */
	private OrderInfoVo orderInfo;
	/** return goods */
	private List<OrderReturnGoodsVo> returnGoods;
	/** return order info */
	private Integer retId;
	private Integer orderId;
	private String orderSn;
	private String returnOrderSn;
	private Byte refundStatus;
	private BigDecimal money;
	private BigDecimal shippingFee;
	private Byte returnType;
	private Byte reasonType;
	private String reasonTypeDesc;
	private String reasonDesc;
	private String shippingType;
    private String shippingName;
	private String shippingNo;
	private String goodsImages;
	private String voucherImages;
	private String phone;
	private Timestamp applyTime;
	private Timestamp applyPassTime;
	private Timestamp applyNotPassTime;
	private Timestamp shippingOrRefundTime;
	private Timestamp refundSuccessTime;
	private Timestamp refundRefuseTime;
	private Timestamp refundCancelTime;
	private String applyNotPassReason;
	private String refundRefuseReason;
	private String returnAddress;
	private String merchantTelephone;
	private String consignee;
	private String zipCode;
	private String currency;
    private String userId;
    private String username;
    private String mobile;
    /**
     * 客服按钮展示开关
     */
    private Byte returnService;
    private Byte returnSource;
    /**
     * 退款异常信息展示
     */
    private Byte showRefundFailInfo;
    private BigDecimal successMoney;
    private BigDecimal failMoney;
    private String failDesc;
}




























