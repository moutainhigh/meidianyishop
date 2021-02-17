package com.meidianyi.shop.service.pojo.wxapp.order.goods;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRebateRecord;
import com.meidianyi.shop.service.foundation.util.lock.annotation.RedisLockField;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author 王帅
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderGoodsBo {
	@JsonIgnore
	private Integer orderId;
    @JsonIgnore
	private String orderSn;
    @RedisLockField
	private Integer goodsId;
	private String goodsName;
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
	private String goodsSn;
	private Integer productId;
	private String productSn;
	private Integer goodsNumber;
	private BigDecimal marketPrice;
	private BigDecimal goodsPrice;
	private String goodsAttr;
	private String goodsAttrId;
	private String goodsImg;
	private Integer straId;
	private BigDecimal perDiscount;
	private Integer isGift;
	private String rGoods;
	private Integer goodsScore;
	private Integer goodsGrowth;
	private BigDecimal discountedGoodsPrice;
	/**
	 * 折后现价总价
	 */
	private BigDecimal discountedTotalPrice;
	/**折扣详情*/
	private String discountDetail;
	/**成本价*/
	private BigDecimal costPrice;
	/**对接CRM 商品推广*/
	private String promoteInfo;
	/***/
	private Integer sendNumber;
	private Integer returnNumber;
	private Byte isReal;
	private Byte refundStatus;
	private Byte commentFlag;
	private Byte fanliType;
	private BigDecimal canCalculateMoney;
	private BigDecimal fanliMoney;
	private BigDecimal totalFanliMoney;
	private String fanliStrategy;
	private BigDecimal fanliPercent;
	private Integer giftId;
	private Byte isCanReturn;
	private Integer reducePriceNum;
	private Byte activityType;
	private Integer activityId;
	private Integer activityRule;

	/**
	 * Table:goods字段
	 */
	private Byte goodsType;
	private Integer deliverTemplateId;
	private BigDecimal goodsWeight;
	/**平台分类*/
	private Integer catId;
	/**商家分类*/
	private Integer sortId;
	/**品牌*/
	private Integer brandId;
	private Byte isCardExclusive;
	/**
	 * 非Table
	 */
	/**TODO 优惠卷*/
	private Integer[] userCoupon;
	/**TODO 营销活动类型*/
	private Byte goodsPriceAction;
	/**加价购id*/
	private Integer purchasePriceId;
	/**加价购换购挡位id*/
	private Integer purchasePriceRuleId;
	/**限时降价活动id*/
	private Integer reducePriceId;
	/**首单特惠活动id*/
	private Integer firstSpecialId;
	/**包邮*/
    private Integer freeShip;
    /**是否可以配送*/
	private Byte isShipping;
	/**该商品对应的返利信息*/
    private ArrayList<OrderGoodsRebateRecord> rebateList;
    /**不可使用优惠券*/
    @JsonIgnore
    private Byte noUseCoupon;
    //******药品信息******//
	/**
	 * 是否是处方药
	 */
	private Byte isRx;

	/**处方详情*/
	private PrescriptionVo prescriptionInfo;
	/**getDoctorByHospitalCode
	 * 处方号
	 */
	private String prescriptionOldCode;
	/**
	 * 处方号
	 */
	private String prescriptionCode;
    /**
     *处方明细号
     */
    private String prescriptionDetailCode;
	/**
	 * 订单审核类型 0不审核 1审核 2开方 3有效处方
	 */
	private Byte medicalAuditType;
	/**
	 * 药品审核状态 0未审核 1审核通过 2审核不通过
	 */
	private Byte medicalAuditStatus;
	/**
	 * 处方药关联处方校验 0不校验 1 通过 2不通过
	 */
	private Byte checkPrescriptionStatus;
}
