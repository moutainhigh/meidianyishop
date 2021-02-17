package com.meidianyi.shop.service.pojo.saas.shop.version;

import lombok.Data;

/**
 * 版本权限对应类
 * @author zhaojianqiang
 *
 *         2019年11月21日 上午10:21:27
 */
@Data
public class VersionName {

	/** 概况统计 */
	public static final String SUB_0_BASIC_YESTERDAY = "basic_yesterday";
	/** 用户画像 */
	public static final String SUB_0_PORTRAIT_USER = "portrait_user";
	/** 访问分析 */
	public static final String SUB_0_SECOND_VIEW = "second_view";
	/** 来源分析 */
	public static final String SUB_0_VISIT_SOURCE = "visit_source";
	/** 交易统计 */
	public static final String SUB_0_ANALYSIS_VISIT_SOURCE = "analysis_visit_source";

	/** 商家分类 */
	public static final String SUB_1_SORT = "sort";

	/** 会员卡模块 */
	public static final String SUB_2_M_MEMBER_CARD = "m_member_card";
	/** 优惠券模块 */
	public static final String SUB_2_M_VOUCHER = "m_voucher";
	/** 砍价模块 */
	public static final String SUB_2_M_BARGAIN = "m_bargain";
	/** 视频模块 */
	public static final String SUB_2_M_VIDEO = "m_video";
	/** 积分商品模块 */
	public static final String SUB_2_M_INTEGRAL_GOODS = "m_integral_goods";
	/** 秒杀模块 */
	public static final String SUB_2_M_SECKILL_GOODS = "m_seckill_goods";
	/** 微信公众号授权 */
	public static final String SUB_2_AUTHORIZATION = "authorization";
	/** 拼团抽奖 */
	public static final String SUB_2_M_GROUP_DRAW = "m_group_draw";
	/** 瓜分积分 */
	public static final String SUB_2_M_PIN_INTEGRATION = "m_pin_integration";
	/** 顶部导航模板 */
	public static final String SUB_2_M_NAV = "m_nav";

	/** 充值会员卡 */
	public static final String SUB_3_CHARGE_CARD = "charge_card";
	/** 限次会员卡 */
	public static final String SUB_3_COUNT_CARD = "count_card";
	/** 等级会员卡 */
	public static final String SUB_3_GRADE_CARD = "grade_card";
	/** 会员标签 */
	public static final String SUB_3_TAG = "tag";
	/** 签到送积分 */
	public static final String SUB_3_SIGN_SCORE = "sign_score";
	/** 买单送积分 */
	public static final String SUB_3_PAY_SCORE = "pay_score";

	/** 满折满减 */
	public static final String SUB_4_FULL_CUT = "full_cut";
	/** 拼团 */
	public static final String SUB_4_PIN_GROUP = "pin_group";
	/** 砍价 */
	public static final String SUB_4_BARGAIN = "bargain";
	/** 表单统计 */
	public static final String SUB_4_FORM_DECORATION = "form_decoration";
	/** 分销 */
	public static final String SUB_4_DISTRIBUTION = "distribution";
	/** 消息推送 */
	public static final String SUB_4_MESSAGE_TEMPLATE = "message_template";
	/** 支付有礼 */
	public static final String SUB_4_PAY_REWARD = "pay_reward";
	/** 定向发券 */
	public static final String SUB_4_COUPON_GRANT = "coupon_grant";
	/** 开屏有礼 */
	public static final String SUB_4_ACTIVITY_REWARD = "activity_reward";
	/** 积分商品 */
	public static final String SUB_4_INTEGRAL_GOODS = "integral_goods";
	/** 限时降价 */
	public static final String SUB_4_REDUCE_PRICE = "reduce_price";
	/** 秒杀 */
	public static final String SUB_4_SECKILL_GOODS = "seckill_goods";
	/** 抽奖 */
	public static final String SUB_4_LOTTERY = "lottery";
	/** 加价购 */
	public static final String SUB_4_PURCHASE_PRICE = "purchase_price";
	/** 拼团抽奖 */
	public static final String SUB_4_GROUP_DRAW = "group_draw";
	/** 组队瓜分积分 */
	public static final String SUB_4_PIN_INTEGRATION = "pin_integration";
	/** 打包一口价 */
	public static final String SUB_4_PACKAGE_SALE = "package_sale";
	/** 定金膨胀 */
	public static final String SUB_4_PRE_SALE = "pre_sale";
	/** 好友代付 */
	public static final String SUB_4_INSTEADPAY = "insteadpay";
	/** 赠品策略 */
	public static final String SUB_4_GIFT = "gift";
	/** 好友助力 */
	public static final String SUB_4_PROMOTE = "promote";
	/** 测评 */
	public static final String SUB_4_ASSESS = "assess";
	/** 满包邮 */
	public static final String SUB_4_FREE_SHIP = "free_ship";
	/** 优惠券包 */
	public static final String SUB_4_COUPON_PACKAGE = "coupon_package";
	/** 评价有礼 */
	public static final String SUB_4_COMMENT_GIFT = "comment_gift";
	/** 首单特惠 */
	public static final String SUB_4_FIRST_SPECIAL = "first_special";
	/** 我要送礼 */
	public static final String SUB_4_GIVE_GIFT = "give_gift";
	/** 分享有礼 */
	public static final String SUB_4_SHARE_AWARD = "share_award";

	/** 门店买单开关配置 */
	public static final String SUB_5_STORE_PAY = "store_pay";
	/** 职称配置 */
	public static final String SUB_5_TECHNICIAN = "technician";
	/** 门店列表-门店管理-技师管理tab */
	public static final String SUB_5_SERVICE = "service";
	/** 同城配送 */
	public static final String SUB_5_SERVICE_CITY = "service_city";
	/** 所有小程序组件 */
	public static final String[] SUB_2 = {SUB_2_M_MEMBER_CARD,SUB_2_M_VOUCHER,SUB_2_M_BARGAIN,SUB_2_M_VIDEO,SUB_2_M_INTEGRAL_GOODS,SUB_2_M_SECKILL_GOODS,SUB_2_AUTHORIZATION,SUB_2_M_GROUP_DRAW,SUB_2_M_PIN_INTEGRATION,SUB_2_M_NAV};
}
