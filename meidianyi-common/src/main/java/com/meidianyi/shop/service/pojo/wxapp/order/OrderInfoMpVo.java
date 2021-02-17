package com.meidianyi.shop.service.pojo.wxapp.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.shop.order.invoice.InvoiceVo;
import com.meidianyi.shop.service.pojo.shop.order.shipping.ShippingInfoVo;
import com.meidianyi.shop.service.pojo.shop.prescription.PrescriptionVo;
import com.meidianyi.shop.service.pojo.shop.store.store.StorePojo;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawInfoByOrderVo;
import com.meidianyi.shop.service.pojo.wxapp.market.groupbuy.GroupBuyUserInfo;
import com.meidianyi.shop.service.pojo.wxapp.order.refund.ReturnOrderListMp;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author 王帅
 *
 */
@Getter
@Setter
public class OrderInfoMpVo extends OrderListMpVo{

	/**配送信息*/
	private List<ShippingInfoVo> shippingInfo;
	/**子单信息*/
	private List<OrderInfoMpVo> subOrder;
	/**
	 * 处方列表
	 */
	private List<PrescriptionVo> prescriptionList;
	/**发票信息*/
	private InvoiceVo invoiceInfo;
	private String verifierName;
	private String verifierMobile;
	/**昵称*/
	private String username;
	/**
	 * 拼团用户信息
	 */
	private List<GroupBuyUserInfo> groupBuyUserInfos;
	/**
	 * 团id
	 */
	private Integer groupId;
	/** 拼团抽奖对应的信息*/
	private GroupDrawInfoByOrderVo groupDraw;
	/***/
	@JsonIgnore
	private String mainOrderSn;
	private Timestamp createTime;
	private Integer storeId;
	private StorePojo storeInfo;
	private Integer invoiceId;
	private Integer verifierId;
    private String verifyCode;
	private Byte orderPayWay;
	private String consignee;
	private String mobile;
	private String completeAddress;
	/** 主支付方式 */
	private String payCode;
	/** 支付金额 */
	private BigDecimal moneyPaid;
	private BigDecimal scoreDiscount;
	/**用户消费余额*/
	private BigDecimal useAccount;
	/**会员卡消费金额*/
	private BigDecimal memberCardBalance;
	/**会员卡优惠金额*/
    private BigDecimal memberCardReduce;
    /**优惠券优惠金额*/
    private BigDecimal discount;
    /**运费*/
    private BigDecimal shippingFee;
	@JsonIgnore
	private BigDecimal subGoodsPrice;
	@JsonIgnore
	private Timestamp orderRemindTime;
	@JsonIgnore
	private Byte orderRemind;
	@JsonIgnore
	private Byte extendReceiveAction;
	@JsonIgnore
	private Timestamp extendReceiveTime;
	public void setVerifierInfo(String verifierName, String verifierMobile){
		this.verifierName = verifierName;
		this.verifierMobile = verifierMobile;
	}

    /**
     * 客服按钮展示开关
     */
    private Byte orderDetailService;

    /**是否显示好物圈的按钮 */
    private Boolean showMall;
    /**商品数量（不包含赠品） */
    private Integer goodsAmount;
    private BigDecimal packageDiscount;
    private BigDecimal grouperCheapReduce;

    private List<ReturnOrderListMp> returnOrderList;

}
