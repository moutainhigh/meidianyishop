package com.meidianyi.shop.service.pojo.shop.member.card;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meidianyi.shop.service.pojo.shop.member.card.base.CardMarketActivity;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardCustomAction;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardCustomRights;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardExchangGoods;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardFreeship;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardGive;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardRenew;
import com.meidianyi.shop.service.pojo.shop.member.card.create.CardTag;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author 黄壮壮
 * @Date: 2019年7月30日
 * @Description: 会员卡公共属性
 */

@Data
@NoArgsConstructor
public class CardParam {
	/** 会员卡id */
	private Integer id;
	/** 会员卡类型，名称 */
	private Byte cardType;
	private String cardName;

	/** 背景类型 0： 背景色；1：背景图 */
	private Byte bgType;
	/** 背景色 */
	private String bgColor;
	/** 背景图 */
	private String bgImg;

	/** 会员折扣 值为 0-10之间 */
	private BigDecimal disCount;
	/** 会员折扣: 全部商品；1代表全部商品，0代表指定商品 */
	private Byte discountIsAll;

	/** 折扣商品Id */
	private List<Integer> goodsId;
	/** 折扣商家Id */
	private List<Integer> shopCategoryIds;
	/** 折扣平台Id */
	private List<Integer> platformCategoryIds;
	/** 折扣品牌id */
	private List<Integer> discountBrandId;

	
	/** 专享商品 */
	/** 商品id */
	private List<Integer> ownGoodsId;
	/** 商家分类id */
	private List<Integer> ownStoreCategoryIds;
	/** 平台分类id  */
	private List<Integer> ownPlatFormCategoryIds;
	/** 商品分类id */
	private List<Integer> ownBrandId;
	
	/** 
	/**
	 * 积分获取开关，会员折扣开关， 0表示关闭，1表示开启
	 */
	private Byte powerScore;
	private Byte powerCount;

	/** 会员专享商品 on表示打开 */
	private String powerPayOwnGood;

	/** 开卡送多少积分 */
	private Integer score;
	/** 购物送积分策略json序列化对象 */
	private ScoreJson scoreJson;
	/**
	 * 卡充值开关 0关闭；1开启
	 */
	private Byte powerCard;
	/** 开卡送多少元 */
	private Integer sendMoney;
	/** 卡充值送积分策略json数据*/
	private PowerCardJson powerCardJson;

	/**
	 * 会员有效期类型 0：固定日期；1：自领取多少内有效；2：永久有效
	 */
	private Byte expiredType;
	/** 开始时间 */
	private Timestamp startTime;
	/** 结束时间 */
	private Timestamp endTime;
	/** 自领取之日内多少时间 */
	private Integer receiveDay;
	/** 时间类型 */
	private Byte dateType;

	/**
	 * 使用门店类型 0：全部门店；1：部分门店；-1：不可在门店使用
	 */
	private Byte storeListType;
	/** 门店Id */
	private List<String> storeList;

	/**
	 * 领取类型 0：直接领取；1：需要购买；2：需要领取码
	 */
	private Byte isPay;
	/**
	 * 购买类型 0： 现金购买；1：积分购买
	 */
	private Byte payType;
	/** 现金购买金额 */
	private BigDecimal payMoney;
	/** 积分购买金额 */
	private BigDecimal payScore;
	/**   领取码类型  1: 领取码 ； 2： 卡号+密码 */
	private Byte receiveAction;
	private List<Integer> batchIdList;
	

	/** 使用须知 */

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String desc;
	/** 联系电话 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String mobile;

	/**
	 * 限次会员卡允许使用时间 1：工作日； 2：双休 0：不限制
	 */
	private Integer useTime;
	/** 允许使用总次数 */
	private Integer count;
	/** 发放总数 */
	private Integer stock;
	/** 领取限制 */
	private Integer limits;

	/** 等级会员卡 */
	/**
	 * 会员卡是否启用 1:使用中，2:停止使用
	 */
	private Byte flag;
	/** 会员卡升级策略json对象 */
	private GradeConditionJson gradeConditionJson;
	/** 等级 */
	private String grade;

	/**
	 * 是否需要激活 0： 否；1： 是
	 */
	private Byte activation;
	/** 激活需要填写的信息 */
	private List<String> activationCfgBox;

	/** 是否审核 0： 否；1： 是 */
	private Byte examine;
	
	/**   发送会员卡开关 */
	private String sendCoupon;
	/**   优惠券类型 */
	private Byte couponType;
	/**   赠送优惠券id */
	private List<Integer> couponIds;
	/**   赠送优惠礼包id */
	private Integer couponPackage;
	
	/**
	 * 	包邮信息
	 */
	private CardFreeship freeship;
	
	/**
	 * 	卡的自定义权益信息
	 */
	private CardCustomRights customRights;
	/**
	 * 续费功能
	 */
	private CardRenew cardRenew;
	
	/**
	 * 卡的自定义激活项
	 */
	private List<CardCustomAction> customAction;
	/**
	 * 	同步打标签
	 */
	private CardTag cardTag;
	
	/**
	 * 	会员卡转赠
	 */
	private CardGive cardGive;
	
	/**
	 * 	折扣不与营销活动公用
	 */
	private List<CardMarketActivity> marketActivities;
	/**
	 * 兑换商品
	 */
	private CardExchangGoods cardExchangGoods;
	
}
