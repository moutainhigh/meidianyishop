package com.meidianyi.shop.service.pojo.wxapp.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.pojo.shop.table.GoodsMedicalInfoDo;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import com.meidianyi.shop.db.shop.tables.records.StoreGoodsRecord;
import com.meidianyi.shop.service.pojo.shop.market.insteadpay.InsteadPay;
import com.meidianyi.shop.service.pojo.shop.order.write.operate.AbstractOrderOperateQueryParam;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientDetailVo;
import com.meidianyi.shop.service.pojo.shop.payment.PaymentVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionVo;
import com.meidianyi.shop.service.pojo.wxapp.cart.activity.OrderCartProductBo;
import com.meidianyi.shop.service.pojo.wxapp.market.bargain.BargainRecordInfo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.order.medical.OrderMedicalHistoryBo;
import com.meidianyi.shop.service.pojo.wxapp.order.validated.CreateOrderValidatedGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author 王帅
 *
 */
@Getter
@Setter
@ToString
public class OrderBeforeParam extends AbstractOrderOperateQueryParam{

    /** 指定本次结算所参加的唯一营销活动类型 {@link BaseConstant} 下的ACTIVITY_TYPE**/
    private Byte activityType;
    /** 指定本次结算所参加的唯一营销活动类型 ID */
    private Integer activityId;

    @NotNull(groups = {CreateOrderValidatedGroup.class}, message = JsonResultMessage.MSG_ORDER_ADDRESS_NO_NULL)
	private Integer addressId;
	private List<Goods> goods;
    @NotNull(groups = {CreateOrderValidatedGroup.class}, message = JsonResultMessage.MSG_ORDER_DELIVER_TYPE_NO_NULL)
    private Byte deliverType;
    /**购物车标记*/
    private Byte isCart;
    /**
     * 门店Id
     */
	private Integer storeId;
    /**
     * 0:默认选第一张；null：小程序不选；""不可使用;其他：卡号
     */
	private String memberCardNo;
    /**
     * 0:默认选第一张；null：不选；""不可使用；其他：卡号
     */
    private String couponSn;
    /**积分抵扣*/
    private Integer scoreDiscount;
    /**余额抵扣金额*/
    private BigDecimal balance;
    /**会员卡抵扣金额*/
    private BigDecimal cardBalance;
    /**支付方式*/
    @NotNull(groups = {CreateOrderValidatedGroup.class}, message = JsonResultMessage.MSG_ORDER_AMOUNT_NO_NULL)
    private Byte orderPayWay;
	@JsonIgnore
	/**方便查找*/
	private Map<Integer, Goods> goodsMap;
    @JsonIgnore
    /**方便查找*/
    private List<OrderGoodsBo> bos;
	/**方便查找*/
	/** 订单业务处理方法*/
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private OrderCartProductBo orderCartProductBo;
	/**下单时间*/
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Timestamp date = DateUtils.getSqlTimestamp();
    /**活动免运费*/
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Byte isFreeShippingAct;
    /**好友代付：0多人；1单人*/
    private Byte insteadPayNum;

    /**
     * 指定可用的支付方式
     */
    private Map<String, PaymentVo> paymentList;

    /**
     * 拼团标记
     */
    /** 没有id就是团长*/
    private Integer groupId;
    /** 是否是团长*/
    private Byte isGrouper;
    /** 邀请人 id*/
	private Integer inviteId = 0;
    /**
     * 砍价下单标记
     */
    /** 砍价发起记录的ID */
    private Integer recordId;
    private BargainRecordInfo bargainRecordInfo;

    private InsteadPay insteadPayCfg;

    /** 直播订单里的房间号*/
    private Integer roomId;
    //*******处方-药品****************//
    /**
     * 处方列表
     */
    private List<PrescriptionVo> prescriptionList;
    /**
     * 处方药关联处方校验 0不校验 1 通过 2不通过
     */
    private Byte checkPrescriptionStatus;
    /**
     * 订单药品类型 0非处方药 1处方药
     */
    private Byte orderMedicalType;
    /**
     * 订单审核类型 0不审核 1审核 2开方 3有效处方
     */
    private Byte orderAuditType;
    /**
     * 患者id
     */
    private Integer  patientId;
    /**
     * 患者信息
     */
    private UserPatientDetailVo patientInfo;
    /**
     * 患者历史诊断
     */
    private OrderMedicalHistoryBo patientDiagnose;
    /**
     * 是否是处方下单
     */
    private Byte isPrescription;
    /**
     * 下单的处方号
     */
    private String prescriptionCode;

    LinkedHashMap<String, List<Integer>> productIdMap;

    Set<Integer> productIdSet;
    /**
	 * 商品参数计算使用
	 * @author 王帅
	 *
	 */
	@Getter
	@Setter
    @ToString
	public static class Goods{
		@NotNull
		private Integer goodsId;
        /** 商品价(商品在结算页,) */
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
		private BigDecimal goodsPrice;
		/**购买数量*/
		@NotNull(message = JsonResultMessage.MSG_ORDER_GOODS_NO_ZERO)
		private Integer goodsNumber;
		@NotNull
		private Integer productId;
		/**促销折扣均摊到每件商品的折扣*/
		private BigDecimal perDiscount;
        /**营销活动类型*/
        private Byte goodsPriceAction;
		/**以下为后台产生逻辑值initGoods*/
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
		private Integer straId;
		private Integer purchasePriceId;
		private Integer purchasePriceRuleId;
		private String promoteInfo;
		/** 拼团的 折后团长优惠价*/
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
		private BigDecimal grouperTotalReduce=BigDecimal.ZERO;
		/** 拼团的 折后团长优惠价单价，逐级计算折扣单价*/
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
		private BigDecimal grouperGoodsReduce =BigDecimal.ZERO;
		/**以下为后台产生逻辑值directPurchase*/
        /** 规格价(商品实际购买价格) */
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private BigDecimal productPrice;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private Integer firstSpecialId;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private Integer reducePriceId;
		/**方便计算*/
		@JsonIgnore
		private GoodsSpecProductRecord productInfo;
        @JsonIgnore
        private GoodsRecord goodsInfo;
        /**积分兑换需要的积分*/
        private Integer goodsScore;
        /**是否校验过限购，true校验过*/
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private Boolean isAlreadylimitNum;
        /**购物车参数：选择活动类型*/
        private Byte cartType;
        /**购物车参数：对应type的id*/
        private Integer cartExtendId;
        /**会员专享*/
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private Byte isCardExclusive;

        //**********药品信息*******//
        /**药品信息*/
        private GoodsMedicalInfoDo medicalInfo;
        /**处方详情*/
        private PrescriptionVo prescriptionInfo;
        /**
         * 老处方号
         */
        private String prescriptionOldCode;
        /**
         * 老处方明细号
         */
        private String prescriptionDetailOldCode;
        /**
         *处方明细号
         */
        private String prescriptionDetailCode;
        /**
         * 处方号
         */
        private String prescriptionCode;
        /**
         * 处方药关联处方校验 0不校验 1 通过 2不通过
         */
        private Byte medicalAuditStatus;
        /**
         * 订单审核类型 0不审核 1审核 2开方 3有效处方
         */
        private Byte medicalAuditType;
        /**
         * 规格参数
         */
        private String goodsQualityRatio;
        /**
         * 生产厂家
         */
        private String goodsProductionEnterprise;
        /**
         * 批准文号
         */
        private String goodsApprovalNumber;
        /**
         * 门店商品
         */
        @JsonIgnore
        private StoreGoodsRecord storeGoods;

        public static Goods init(Integer goodsId, Integer goodsNumber, Integer productId) {
            OrderBeforeParam.Goods goods = new OrderBeforeParam.Goods();
            goods.setGoodsId(goodsId);
            goods.setGoodsNumber(goodsNumber);
            goods.setProductId(productId);
            return goods;
        }
	}

    /**
     * 获取商品计算首单特惠活动。。。
     */
    public OrderCartProductBo createOrderCartProductBo(){
    	if (orderCartProductBo==null){
			orderCartProductBo= new OrderCartProductBo();
            orderCartProductBo.setUserId(getWxUserInfo().getUserId());
            orderCartProductBo.setStoreId(getStoreId());
            orderCartProductBo.setDate(date);
			goods.forEach(x->{
				orderCartProductBo.getAll().add(new OrderCartProductBo.OrderCartProduct(x.getProductId(), x.getGoodsNumber(), BaseConstant.ACTIVITY_TYPE_PURCHASE_GOODS.equals(x.getCartType()), x.getProductInfo()));
			});
		}
        return orderCartProductBo;
    }

	/**
	 * 获取当前购买商品ids
	 * @return
	 */
	public List<Integer> getGoodsIds() {
		return goods == null ? Collections.emptyList() : goods.stream().map(Goods::getGoodsId).distinct().collect(Collectors.toList());
	}

	/**
	 * 获取当前购买商品规格ids
	 * @return
	 */
	public List<Integer> getProductIds() {
		return goods == null ? Collections.emptyList() : goods.stream().map(Goods::getProductId).collect(Collectors.toList());
	}
}
