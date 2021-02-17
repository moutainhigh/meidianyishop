package com.meidianyi.shop.service.pojo.shop.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.common.pojo.shop.table.PrescriptionDo;
import com.meidianyi.shop.service.pojo.shop.order.rebate.OrderRebateVo;
import com.meidianyi.shop.service.pojo.shop.order.refund.OrderConciseRefundInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.shipping.ShippingInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.pay.instead.InsteadPayDetailsVo;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientDetailVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionItemVo;
import com.meidianyi.shop.service.pojo.shop.prescription.bo.PrescriptionItemBo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author wangshuai
 */
@Getter
@Setter
public class OrderInfoVo extends OrderListInfoVo {
	/**支付时间 */
	private Timestamp payTime;
	/**发货时间*/
	private Timestamp shippingTime;
	/**自提时间*/
	private Timestamp confirmTime;
	/**确认收货时间 (同上)*/
	private Timestamp affirmTime;
	/**完成时间*/
	private Timestamp finishedTime;
	/**订单关闭时间*/
	private Timestamp closedTime;
	/**订单取消时间*/
	private Timestamp cancelledTime;
    /**拼团中时间*/
    private Timestamp pinStartTime;
    /**拼团抽奖已成团时间*/
    private Timestamp pinEndTime;
	/**买家留言*/
	private String addMessage;
	/**快递单号:后台判断是否查询配送信息依据之一*/
	@JsonIgnore
	private String shippingNo;
	/**快递信息*/
	private List<ShippingInfoVo> shippingList;
	/**退款、货信息*/
	private List<OrderConciseRefundInfoVo> refundList;
	/**其他信息...*/
	/**返利类型，0：普通订单，1：分销返利订单，2：返利会员返利订单*/
	private Integer fanliType;
	@JsonIgnore
	private Integer cardId;
	@JsonIgnore
	private Byte orderPayWay;
	/**（券）折扣金额*/
	private BigDecimal discount;
	private Integer storeId;
	private String storeName;
	private Integer verifierId;
	private String verifierName;
	private BigDecimal orderAmount;
	private BigDecimal packageDiscount;
	private BigDecimal memberCardReduce;
	private Byte exchang;
	private Short goodsAmount;
	/**满折满减优惠*/
	private BigDecimal promotionReduce;
	/**团长优惠金额*/
	private BigDecimal grouperCheapReduce;
	/**会员等级优惠*/
	private BigDecimal discountPrice;
	/**搭配立减优惠*/
	private BigDecimal dapeiReduceAmount;
	private String sellerRemark;
	private Byte isCod;
	@JsonIgnore
	private String verifyCode;
	/**退定金模式1:自动退定金0:不退定金*/
	private Byte bkReturnType;
	/**在退款流程中判断是否补款退款*/
    @JsonIgnore
    private Boolean isReturnBk;
    /**返利信息*/
    private List<OrderRebateVo> rebateList;
    /**是否展示手动退款退货按钮*/
    private Boolean showManualReturn;
    private Byte settlementFlag;
    /**会员卡名称*/
    private String cardName;
    /**订单退货完成时间 */
    private Timestamp returnFinishTime;
    /**订单退款完成时间*/
    private Timestamp refundFinishTime;
    /**代付明细*/
    private List<InsteadPayDetailsVo> insteadPayInfo;


	private Byte       orderMedicalType;
	private Byte       orderAuditType;
	private Byte       orderAuditStatus;
	private String     prescriptionCodeList;
	private Timestamp  auditTime;
	private Integer    patientId;

    private List<PrescriptionDo> prescriptionDoList;
    private List<PrescriptionDo> prescriptionOldDoList;
    private UserPatientDetailVo patientInfo;
    private List<PrescriptionItemBo> prescriptionItemList;

}
