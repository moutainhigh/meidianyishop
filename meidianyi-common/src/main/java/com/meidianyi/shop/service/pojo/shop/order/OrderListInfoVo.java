package com.meidianyi.shop.service.pojo.shop.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.common.pojo.shop.table.StoreDo;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StoreOrderVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StorePojo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 订单列表展示订单信息
 * @author wangshuai
 */

@Data
@NoArgsConstructor
public class OrderListInfoVo {

	private Integer orderId;
	private String orderSn;
	/**
	 * 逻辑订单号
	 * 普通订单LogicMainOrderSn=orderSn
	 * 拆单主订单LogicMainOrderSn=orderSn=mainOrderSn
	 * 拆单子订单LogicMainOrderSn=mainOrderSn
	 */
	@JsonIgnore
	private String logicMainOrderSn;
	private String mainOrderSn;
	private String goodsType;
	private List<? extends OrderListInfoVo> childOrders;
	private List<? extends OrderGoodsVo> goods;
	private Byte orderStatus;
	/** 收件人姓名 */
	private String consignee;
	/** 收件人手机 */
	private String mobile;
	/**店铺名称**/
	private String shopName;
	/** 主支付方式 */
	private String payCode;
	/** 支付方式列表 */
	private List<Byte> payCodeList;
	/** 配送方式:0 快递 1 自提 */
	private Byte deliverType;
	/** 下单时间 */
	private Timestamp createTime;
	/** 快递费金额(退款时为退运费金额) */
	private BigDecimal shippingFee;
	/** 支付金额 */
	private BigDecimal moneyPaid;
	/**是否部分发货:0否，1是*/
	private Byte partShipFlag;
	/**是否支持mp端退款*/
	@JsonIgnore
	private Byte returnTypeCfg;
	/**积分抵扣金额*/
	private BigDecimal scoreDiscount;
	/**用户消费余额*/
	/**918*/
	private BigDecimal useAccount;
	/**会员卡消费金额*/
	/**918*/
	private BigDecimal memberCardBalance;
	private BigDecimal subGoodsPrice;
	/**退款/退货状态*/
	private Byte refundStatus;
	/**补款金额*/
	/**918*/
	private BigDecimal bkOrderMoney;
	private Integer userId;
    /**下单人昵称*/
    private String username;
    /**下单人手机号*/
    private String userMobile;
    /**会员卡号*/
	private String cardNo;
	@JsonIgnore
	private Integer memberCardId;
	@JsonIgnore
	private String bkOrderSn;
	@JsonIgnore
	private Integer activityId;
	/**918*/
	private Byte bkOrderPaid;
	/**币种*/
	private String currency;
	private Byte starFlag;
	/**发货时间*/
	private Timestamp shippingTime;
	/**确认收货时间*/
	private Timestamp confirmTime;
	/**定金膨胀订单自提时间*/
	private Timestamp pickupTime;
	/**定金订单发货时间*/
	private Timestamp bkShippingTime;
	@JsonIgnore
	/**确认收货后自动订单完成时间,单位天*/
	private Short orderTimeoutDays;
	private Boolean canClose;
	private Boolean canFinish;
	private Boolean canDeliver;
	private Boolean canVerify;
    private Boolean showReturnInfo;
    private Integer scoreProportion;
    @JsonIgnore
    private Byte isLock;
    private Timestamp orderRemindTime;
    /**完整收货地址*/
    private String completeAddress;
    /**订单申请退货时间*/
    private Timestamp returnTime;
    /**订单申请退款时间*/
    private Timestamp refundTime;
	/**订单药品类型  0普通 1处方药'*/
    private Byte orderMedicalType;
	/**订单审核类型, 0不审核,1审核,2开方,3根据处方下单*/
	private Byte orderAuditType;
	/**订单审核状态 0未审核 1审核通过 2审核不通过*/
	private Byte orderAuditStatus;
	/**药品审核时间*/
    private Timestamp auditTime;
    /**患者id*/
	private Integer patientId;
    /**门店id**/
	private Integer storeId;
	/**门店名称**/
	private String storeName;
	/**门店信息**/
	private StoreOrderVo storeOrderVo;
	/**自提时间**/
	private String pickupdateTime;


}
