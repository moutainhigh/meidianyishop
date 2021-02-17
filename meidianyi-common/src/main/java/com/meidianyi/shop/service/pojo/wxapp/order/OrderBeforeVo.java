package com.meidianyi.shop.service.pojo.wxapp.order;

import com.google.common.collect.Lists;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.service.pojo.shop.market.insteadpay.InsteadPay;
import com.meidianyi.shop.service.pojo.shop.member.address.UserAddressVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.patient.UserPatientDetailVo;
import com.meidianyi.shop.service.pojo.shop.payment.PaymentVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StorePojo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.coupon.OrderCouponVo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.member.OrderMemberVo;
import com.meidianyi.shop.service.pojo.wxapp.order.must.OrderMustVo;
import com.meidianyi.shop.service.pojo.wxapp.order.term.OrderTerm;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 王帅
 *
 */
@Data
@Builder(toBuilder = false)
public class OrderBeforeVo {
    /**商品*/
    private List<OrderGoodsBo> orderGoods;


    /** 指定本次结算所参加的唯一营销活动类型 {@link BaseConstant} 下的ACTIVITY_TYPE**/
    private Byte activityType;
    /** 指定本次结算所参加的唯一营销活动类型 ID */
    private Integer activityId;
    /**订单类型*/
    @Builder.Default
    private List<Byte> orderType = Lists.newArrayList();
	private UserAddressVo address;
	private Byte[] expressList;
    /**
     * 1.自提:根据前端传的经纬度排序（当前门店、距离
     * 2.同城配送：根据vo.getAddress()地址排序（当前门店、距离）
     */
	private Byte deliverType;
	private String memberCardNo;
    private String couponSn;
    private OrderCouponVo defaultCoupon;
    private List<OrderCouponVo> coupons;
    /**TODO goodstype*/
    private Byte goodsType;
    private List<StorePojo> storeList;
    private OrderMemberVo defaultMemberCard;
    private List<OrderMemberVo> memberCards;
    private Map<String, PaymentVo> paymentList;
    /**必填信息*/
    private OrderMustVo must;
    /**服务条款*/
    private OrderTerm term;
    /**积分支付规则_是否限制*/
    private Byte scorePayLimit;
    /**积分支付规则_每单支付的积分数量少于 score_pay_num 积分，不可使用积分支付*/
    private Integer scorePayNum;
    /**运费*/
    private BigDecimal shippingFee;
    /**订单金额*/
    private BigDecimal orderAmount;
    /**商品总量*/
    private Integer totalGoodsNumber;
    /**积分抵扣*/
    private BigDecimal scoreDiscount;
    /**余额抵扣金额*/
    private BigDecimal accountDiscount;
    /**会员卡抵扣金额*/
    private BigDecimal memberCardDiscount;
    /**会员卡折扣金额*/
    private BigDecimal memberCardReduce;
    /**折扣（优惠卷折扣）*/
    private BigDecimal discount;
    /**支付金额（微信）*/
    private BigDecimal moneyPaid;
    /**微信支付金额-运费*/
    private BigDecimal discountedMoney;
    /**用户积分*/
    private Integer userScore;
    /**用户余额*/
    private BigDecimal userAccount;
    /**会员卡余额*/
    private BigDecimal memberCardMoney;
    /**折后订单金额*/
    private BigDecimal moneyAfterDiscount;
    /**支付方式*/
    private Byte orderPayWay;
    /**是否兑换*/
    private Byte exchang;
    /**是否可以配送*/
    private Byte canShipping;
    /**积分最大抵用金额*/
    private BigDecimal scoreMaxDiscount;
    /**积分兑换比*/
    private Integer scoreProportion;
    /**发票开关*/
    private Byte invoiceSwitch;
    /**
     * 拍下未付款订单12小时10分钟内未付款，自动取消订单
     * cancel_time保存形式为分钟，例如：730
     */
    private Integer cancelTime;
    /**TODO 自提时间*/
    private Timestamp[][] pickupDate;
    /**满折满减优惠金额*/
    @Builder.Default
    private BigDecimal promotionReduce = BigDecimalUtil.BIGDECIMAL_ZERO;
    /**打包一口价折扣金额*/
    @Builder.Default
    private BigDecimal packageDiscount = BigDecimalUtil.BIGDECIMAL_ZERO;
    /**团长优惠*/
    @Builder.Default
    private BigDecimal grouperCheapReduce = BigDecimalUtil.BIGDECIMAL_ZERO;
    /**预售折扣*/
    private BigDecimal preSaleDiscount;
    /**补款金额*/
    private BigDecimal bkOrderMoney;
    /**补款订单发货开始时间*/
    private Timestamp bkShippingTime;
    /**补款是否自动退定金*/
    private Byte bkReturnType;
    /** 代付金额*/
    @Builder.Default
    private BigDecimal insteadPayMoney = BigDecimalUtil.BIGDECIMAL_ZERO;
    /**默认支付配置->会员卡余额*/
    private Byte isCardPay;
    /**默认支付配置->余额*/
    private Byte isBalancePay;
    /**默认支付配置->积分*/
    private Byte isScorePay;
    /**订单商品折后总金额*/
    private BigDecimal tolalDiscountAfterPrice;
    private InsteadPay insteadPayCfg;
    private Byte insteadPayNum;
    private Byte isFreeshipCard;
    private BigDecimal freeshipCardMoney;

    /*******处方列表*************/
    private List<PrescriptionVo> prescriptionList;
    /**
     * 订单药品类型 0非处方药 1处方药
     */
    private Byte orderMedicalType;
    /**
     * 处方药关联处方校验 0不校验 1 通过 2不通过
     */
    private Byte checkPrescriptionStatus;
    /**
     * 订单审核类型 0不审核 1审核 2开方 3有效处方
     */
    private Byte orderAuditType;
    /**
     * 患者信息
     */
    private UserPatientDetailVo patientInfo;


    /**
     * 默认配送方式 0 ，1 ，2, 3
     * @return
     */
    public Byte getDefaultDeliverType(){
        //门店配送
        if(expressList[OrderConstant.STORE_EXPRESS]==BaseConstant.YES.byteValue()){
            return OrderConstant.STORE_EXPRESS;
        }
        //自提
        if(expressList[OrderConstant.DELIVER_TYPE_SELF]==BaseConstant.YES.byteValue()){
            return OrderConstant.DELIVER_TYPE_SELF;
        }
        //快递
        if(expressList[OrderConstant.DELIVER_TYPE_COURIER]==BaseConstant.YES.byteValue()){
            return OrderConstant.DELIVER_TYPE_COURIER;
        }
        return null;
    }

    public void intoRecord(OrderInfoRecord orderRecord){
        //支付方式
        orderRecord.setGoodsAmount(getTotalGoodsNumber().shortValue());
        orderRecord.setShippingFee(getShippingFee());
        orderRecord.setMoneyPaid(getMoneyPaid());
        orderRecord.setOrderAmount(getOrderAmount());
        orderRecord.setGrouperCheapReduce(getGrouperCheapReduce());
        orderRecord.setMemberCardReduce(getMemberCardReduce());
        orderRecord.setPromotionReduce(getPromotionReduce());
        orderRecord.setDiscount(getDiscount());
        orderRecord.setScoreDiscount(getScoreDiscount());
        orderRecord.setUseAccount(getAccountDiscount());
        orderRecord.setMemberCardBalance(getMemberCardDiscount());
        orderRecord.setPackageDiscount(getPackageDiscount());
        orderRecord.setPreSaleDiscount(getPreSaleDiscount());
        //订单付款方式，0全款 1定金 2好友代付(此处只是设置默认值，后续可能修改)
        orderRecord.setOrderPayWay(getOrderPayWay() == null ? OrderConstant.PAY_WAY_FULL : getOrderPayWay());
        if(getBkOrderMoney() != null){
            orderRecord.setBkOrderMoney(getBkOrderMoney());
        }
        if(getBkShippingTime() != null){
            orderRecord.setBkShippingTime(getBkShippingTime());
        }
        if(getBkReturnType() != null){
            orderRecord.setBkReturnType(getBkReturnType());
        }
        if(insteadPayNum != null) {
            orderRecord.setInsteadPayNum(insteadPayNum.shortValue());
        }
        orderRecord.setInsteadPayMoney(getInsteadPayMoney());
        orderRecord.setExchang(getExchang());


        if(getActivityId() != null && getActivityType() != null){
            //营销活动订单
            orderRecord.setActivityId(getActivityId());
        }
        if(getScoreProportion() != null) {
            orderRecord.setScoreProportion(getScoreProportion());
        }
        if(getIsFreeshipCard() != null) {
            orderRecord.setIsFreeshipCard(getIsFreeshipCard());
        }
    }
}
