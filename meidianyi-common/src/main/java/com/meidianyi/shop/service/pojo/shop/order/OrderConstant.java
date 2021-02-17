package com.meidianyi.shop.service.pojo.shop.order;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.Util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 订单状态order_status;订单类型ACTIVITY_TYPE常量类
 *
 * @author wangshuai
 *
 */
public class OrderConstant {
    /** 语言包放在excel相关properties里 */
    public final static String LANGUAGE_TYPE_EXCEL = "excel";

    /**order sn 前缀*/
    public final static String ORDER_SN_PREFIX = "P";

    /**pay sn 前缀*/
    public final static String PAY_SN_PREFIX = "PS";

    /**return sn 前缀*/
    public final static String RETURN_SN_PREFIX = "R";

    /**定金 sn 后缀*/
    public final static String BK_SN_SUFFIX = "_BK";

    /**代付的支付订单 T 前缀*/
    public final static String INSTEAD_PAY_SN_PREFIX = "T";

	/** 订单状态order_status */
	/** 待付款 可进行操作：关闭订单 */
	public final static byte ORDER_WAIT_PAY = 0;
	/** 客户已取消 可进行操作：无 */
	public final static byte ORDER_CANCELLED = 1;
	/** 卖家关闭 */
	public final static byte ORDER_CLOSED = 2;
	/** 待发货 可进行操作：发货，卖家关闭（关闭原因） */
	public final static byte ORDER_WAIT_DELIVERY = 3;
	/** 已发货 可进行操作：已收货（需检查已超时未收货时才可操作） */
	public final static byte ORDER_SHIPPED = 4;
	/** 已收货 可进行操作：完成 */
	public final static byte ORDER_RECEIVED = 5;
	/** 已完成 可进行操作：无 */
	public final static byte ORDER_FINISHED = 6;
	/** 目前没用-退货中 可进行操作：完成退货 已完成 */
	public final static byte ORDER_RETURNING = 7;
	/** 完成退货 可进行操作：无 */
	public final static byte ORDER_RETURN_FINISHED = 8;
	/** 目前没用-退款中 可进行操作：无 */
	public final static byte ORDER_REFUNDING = 9;
	/** 退款成功 可进行操作：无 */
	public final static byte ORDER_REFUND_FINISHED = 10;
	/** 拼团已支付拼团中状态,这时需等待拼团是否成功.拼团成功后,如果为定金订单则变为待补款,否则为待发货;拼团失败则变为退款 */
	public final static byte ORDER_PIN_PAYED_GROUPING = 11;
	/** 已成团 */
	public final static byte ORDER_PIN_SUCCESSS = 12;
	/** 礼单(主订单)环节已完成 */
	public final static byte ORDER_GIVE_GIFT_FINISHED = 13;
	/* *******医药******** */
	/**
	 * 审核中
	 * 支付完成即待审核
	 * 待审核->待发货
	 */
	public final static byte ORDER_TO_AUDIT =14;
	/** 待开方 */
 	public final static byte ORDER_TO_AUDIT_OPEN =15;
 	/** 代配送 与代发货一致,属于门店发货的中间状态 可进行操作：发货 */
 	public final static byte ORDER_WAIT_STORE_DELIVERY =16;


	/*退货状态*/
    /** 退货的默认状态 */
    public final static byte REFUND_DEFAULT_STATUS = 0;
	/**审核中,退款且退货申请*/
	public final static byte  REFUND_STATUS_AUDITING = 1;
	/**审核通过,更新卖家的收货地址信息*/
	public final static byte  REFUND_STATUS_AUDIT_PASS = 2;
	/**未通过审核,继续走正常订单流程*/
	public final static byte  REFUND_STATUS_AUDIT_NOT_PASS = 3;
	/**买家提交物流 或 仅退款时,买家申请退款,(仅退运费/手动退款)*/
	public final static byte  REFUND_STATUS_APPLY_REFUND_OR_SHIPPING = 4;
	/**退款/退货完成*/
	public final static byte  REFUND_STATUS_FINISH = 5;
	/**拒绝退款/退货*/
	public final static byte  REFUND_STATUS_REFUSE = 6;
	/**退款/退货 撤销*/
	public final static byte  REFUND_STATUS_CLOSE = 7;
	/**
	 * 	支付code
	 */
	/**微信*/
	public final static String PAY_CODE_WX_PAY = "wxpay";
	/**积分*/
	public final static String PAY_CODE_SCORE_PAY = "score";
	/**余额*/
	public final static String PAY_CODE_BALANCE_PAY = "balance";
	/**货到付款*/
	public final static String PAY_CODE_COD = "cod";
	/**会员卡*/
	@Deprecated
	public final static String PAY_CODE_MEMBER_CARD = "member_card";
	/**ali_mini*/
	@Deprecated
	public final static String PAY_CODE_ALI_MINI_PAY = "ali_mini_pay";

    /**
     * 小程序支付方式编码
     */
    /**微信*/
    public final static Byte MP_PAY_CODE_WX_PAY = 0;
    /**货到付款*/
    public final static Byte MP_PAY_CODE_COD = 1;

    /**小程序支付方式编码->string*/
    public final static String[] MP_PAY_CODE_TO_STRING ={PAY_CODE_WX_PAY , PAY_CODE_COD, PAY_CODE_SCORE_PAY};

	/**
	 *	 配送方式
	 *
	 */
	/**快递*/
	public final static byte DELIVER_TYPE_COURIER = 0;
	/**自提*/
	public final static byte DELIVER_TYPE_SELF = 1;
    /**同城配送*/
    public final static byte CITY_EXPRESS_SERVICE =2;
    /**门店配送**/
    public final static byte STORE_EXPRESS =3;
    /**
     * 配送方式名称
     */
    public final static String[] DELIVER_LIST={"快递","自提","同城配送","门店配送"};

	/**
	 *	 是否部分发货
	 */
	/**否*/
	public final static byte NO_PART_SHIP = 0;
	/**是*/
	public final static byte PART_SHIP = 1;

	/**
	 * admin
	 */
	public final static byte PLATFORM_ADMIN =0;
	/**
	 * 门店 store
	 */
	public final static byte PLATFORM_STORE =1;
	/**
	 * wxapp 微信
	 */
	public final static byte PLATFORM_WXAPP =2;
	/**
	 * wxapp_store 微信门店
	 */
	public final static byte PLATFORM_WXAPP_STORE =3;


	/**
	 *  return_order_goods表success字段
	 */
	/**0拒绝*/
	public final static byte SUCCESS_REFUSE = 0;
	/**退货、款中1*/
	public final static byte SUCCESS_RETURNING = 1;
	/**2完成*/
	public final static byte SUCCESS_COMPLETE = 2;
	/**3撤销*/
	public final static byte SUCCESS_REVOKE = 3;

	/**
	 * mp端是否支持退款、退货,orderInfo表字段
	 */
	public final static byte CFG_RETURN_TYPE_Y = 1;
	public final static byte CFG_RETURN_TYPE_N = 2;
	/**
	 * mp端是否支持退款、退货,orderGoods表字段
	 */
	public final static byte IS_CAN_RETURN_Y = 1;
	public final static byte IS_CAN_RETURN_N = 0;
	/**
	 * 0当前订单可以退款
	 * 1当前订单可以退款、退货
	 */
	/**退款支持状态:待审核,待开方,待发货 已发货 已收货 已完成*/
	public final static List<Byte> IS_RETURNMONEY = new ArrayList<Byte>(Arrays.asList(ORDER_TO_AUDIT_OPEN,ORDER_TO_AUDIT,ORDER_WAIT_DELIVERY, ORDER_SHIPPED, ORDER_RECEIVED, ORDER_FINISHED));
	/**审核订单 医师可以退   待审核,待开方 */
	public final static List<Byte> IS_AUDIT_ORDER = new ArrayList<Byte>(Arrays.asList(ORDER_TO_AUDIT_OPEN,ORDER_TO_AUDIT));
	/** 门店退款 代发货 **/
	public final static List<Byte> IS_STOTE_RETURN_ORDER = new ArrayList<Byte>(Arrays.asList(ORDER_WAIT_DELIVERY));
	/**退货支持状态:已发货 已收货 已完成*/
	public final static List<Byte> IS_RETURNGOODS = new ArrayList<Byte>(Arrays.asList(ORDER_SHIPPED, ORDER_RECEIVED, ORDER_FINISHED));

	/**
	 * return_order return_type
	 * 退-退款类型:0仅退款 1:退货退款 2:仅退运费 3:手动退款 4:换货
	 */
	public final static byte RT_ONLY_MONEY = 0;
	public final static byte RT_GOODS = 1;
	public final static byte RT_ONLY_SHIPPING_FEE = 2;
	public final static byte RT_MANUAL = 3;
	public final static byte RT_CHANGE = 4;

	public final static String[] RETURN_TYPE_CN = {
			"仅退款",
			"退货退款",
			"仅退运费",
			"手动退款"
	};

	/**
	 * 返利类型，0：普通订单，1：分销返利订单，2：返利会员返利订单
	 */
	public final static byte FANLI_TYPE_DEFAULT = 0;
	public final static byte FANLI_TYPE_DISTRIBUTION_ORDER = 1;
	public final static byte FANLI_TYPE_MEMBER_ORDER = 2;

	/**
	 * 	settlement_flag 返利结算标志：0：未结算，1：已结算，2：不返利
	 */

	public final static byte SETTLEMENT_WAIT = 0;
	public final static byte SETTLEMENT_FINISH = 1;
	public final static byte SETTLEMENT_NOT = 2;

	/**元_分的比例*/
	public final static byte TUAN_FEN_RATIO = 100;

	/**一分*/
	public final static BigDecimal CENT = new BigDecimal("0.01");

	/**操作人：0商家 、1买家、2定时任务、3mq、4pos、5erp/ekb 6医师 7门店  **/
	public final static byte IS_MP_Y = 1;
	public final static byte IS_MP_ADMIN = 0;
	public final static byte IS_MP_AUTO = 2;
    public final static byte IS_MP_MQ = 3;
    public final static byte IS_MP_POS = 4;
    public final static byte IS_MP_ERP_OR_EKB = 5;
    public final static byte IS_MP_DOCTOR = 6;
    public final static byte IS_MP_STORE_CLERK = 7;

    /**
     *   ReturnSource
     *  售后方式7个
     *         0商家主动售后
     *         1下单人申请售后
     *         2系统自动售后（1）改价失败退款（2）微信支付失败（3）活动自动售后
     *         3旺店通ERP主动售后（仅对接后显示）
     *         4旺店通POS主动售后（仅对接后显示）
     *         其中活动自动售后设计的活动有：社区团购、我要送礼、多人拼团
	 *         5医师审核失败
	 *         6门店退款
     */
    public final static byte RS_ADMIN = 0;
    public final static byte RS_MP = 1;
    public final static byte RS_AUTO = 2;
    public final static byte RS_ERP = 3;
    public final static byte RS_POS = 4;
    public final static byte RS_DOCTOR_AUDIT = 5;
    public final static byte RS_STORE = 6;

    /**
     * 其中活动自动售后设计的活动有：0社区团购、1我要送礼、2多人拼团
     */
    public final static byte RS_AUTO_COMMUNITY_GROUP = 0;
    public final static byte RS_AUTO_GIVE_GIFT = 1;
    public final static byte RS_AUTO_GROUP_BUY = 2;
    public final static byte RS_AUTO_AUDIT = 3;

	/**
	 * 订单退款退货流程操作
	 */
	/**买家提交物流信息*/
	public final static byte RETURN_OPERATE_MP_SUBMIT_SHIPPING = 0;
	/**买家撤销退款退货*/
	public final static byte RETURN_OPERATE_MP_REVOKE = 1;
	/**商家拒绝退款退货（退货为提交物流后的拒绝）*/
	public final static byte RETURN_OPERATE_ADMIN_REFUSE = 2;
	/**商家同意退货申请*/
	public final static byte RETURN_OPERATE_ADMIN_AGREE_RETURN = 3;
	/**商家拒绝退货申请*/
	public final static byte RETURN_OPERATE_ADMIN_REFUSE_RETURN_GOODS_APPLY = 4;
	/** 商家同意退款(代发货,门店店员退款)*/
	public final static byte RETURN_OPERATE_STORE_ALL_RETURN = 5;
	/**操作对应的中文*/
	public final static String[] RETURN_OPERATE = {
			"买家提交物流信息",
			"买家撤销退款退货",
			"商家拒绝退款退货",
			"商家同意退货申请",
			"商家拒绝退货申请"
	};

	/**
	 * 订单简单操作
	 */
	/**商家关闭订单*/
	public final static byte SIMPLE_OPERATE_CLOSE = 0;
	/**买家取消订单*/
	public final static byte SIMPLE_OPERATE_CANCEL = 1;
	/**
	 * 定金尾款支付状态bk_order_paid，先定金后尾款。order_pay_way=1时有效，0未支付，1定金已支付，2尾款已支付
	 */
    public final static byte BK_PAY_NO = 0;
    public final static byte BK_PAY_FRONT = 1;
    public final static byte BK_PAY_FINISH = 2;

    /**
     * 退定金模式bk_return_type。order_pay_way=1时有效，退定金模式1:自动退定金0:不退定金
     */
    public final static byte BK_RETURN_TYPE_N = 0;
    public final static byte BK_RETURN_TYPE_Y = 1;

	/**
	 * 综合查询：支付方式
	 */
	/**余额*/
	public final static byte SEARCH_PAY_WAY_USE_ACCOUNT = 1;
	/**积分支付*/
	public final static byte SEARCH_PAY_WAY_SCORE_DISCOUNT = 2;
	/**积分兑换*/
	public final static byte SEARCH_PAY_WAY_SCORE_EXCHANGE = 3;
	/**货到付款*/
	public final static byte SEARCH_PAY_WAY_COD = 4;
	/**活动奖品*/
	public final static byte SEARCH_PAY_WAY_EVENT_PRIZE = 5;
	/**微信支付*/
	public final static byte SEARCH_PAY_WAY_WXPAY = 6;

    private static String[] orderStatus = {
        JsonResultMessage.ORDER_STATUS_WAIT_PAY,
        JsonResultMessage.ORDER_STATUS_CANCELLED,
        JsonResultMessage.ORDER_STATUS_CLOSED,
        JsonResultMessage.ORDER_STATUS_WAIT_DELIVERY,
        JsonResultMessage.ORDER_STATUS_SHIPPED,
        JsonResultMessage.ORDER_STATUS_RECEIVED,
        JsonResultMessage.ORDER_STATUS_FINISHED,
        JsonResultMessage.ORDER_STATUS_RETURNING,
        JsonResultMessage.ORDER_STATUS_RETURN_FINISHED,
        JsonResultMessage.ORDER_STATUS_REFUNDING,
        JsonResultMessage.ORDER_STATUS_REFUND_FINISHED,
        JsonResultMessage.ORDER_STATUS_PIN_PAYED_GROUPING,
        JsonResultMessage.ORDER_STATUS_PIN_SUCCESS,
        JsonResultMessage.ORDER_STATUS_GIVE_GIFT_FINISHED
    };

	private String[] goodsType = {
			// ACTIVITY_TYPE_GENERAL
			"普通订单",
			// ACTIVITY_TYPE_PIN_GROUP
			"拼团订单",
			// ACTIVITY_TYPE_REBATE
			"返利订单",
			// ACTIVITY_TYPE_BARGAIN
			"砍价订单",
			// ACTIVITY_TYPE_INTEGRAL
			"积分兑换订单",
			// ACTIVITY_TYPE_SECKILL
			"秒杀订单",
			// ACTIVITY_TYPE_REDUCE_PRICE
			"限时降价订单",
			// ACTIVITY_TYPE_FIRST_SPECIAL
			"首单特惠订单",
			// ACTIVITY_TYPE_PURCHASE_PRICE
			"加价购订单",
			// ACTIVITY_TYPE_GROUP_DRAW
			"拼团抽奖订单",
			// ACTIVITY_TYPE_PACKAGE_SALE
			"一口价订单",
			// ACTIVITY_TYPE_PRE_SALE
			"定金膨胀订单",
			// ACTIVITY_TYPE_GIFT
			"赠品订单",
			// ACTIVITY_TYPE_LOTTERY_PRESENT
			"幸运抽奖订单",
			// ACTIVITY_TYPE_EXCHANG_ORDER
			"限次卡兑换订单",
			// ACTIVITY_TYPE_PROMOTE_ORDER
			"好友助力订单",
			// ACTIVITY_TYPE_FREESHIP_ORDER
			"满包邮",
			// ACTIVITY_TYPE_ASSESS_ORDER
			"测评订单",
			// ACTIVITY_TYPE_GIVE_GIFT
			"送礼订单"
	};

	/** 全款 **/
    public static final byte PAY_WAY_FULL = 0;
    /** 定金 **/
    public static final byte PAY_WAY_DEPOSIT = 1;
    /** 好友代付 **/
    public static final byte PAY_WAY_FRIEND_PAYMENT = 2;

	public static String getOrderStatusName(byte orderStatusCode,String lang) {
        return Util.translateMessage(lang, orderStatus[orderStatusCode] ,JsonResultMessage.ORDER_STATUS_UNKNOWN,LANGUAGE_TYPE_EXCEL);
	}

	public String getGoodsType(byte goodsTypeCode) {
		return goodsType[goodsTypeCode];
	}

	/** 是否删除 0： 否； 1： 是 */
	public static final Byte DELETE_NO = 0;
	public static final Byte DELETE_YES = 1;

	/**
	 * 是否是礼物
	 */
	public static final Integer IS_GIFT_Y = 1;
	public static final Integer IS_GIFT_N = 0;

	/**
	 * 	前端搜索退款订单的returnStatus与sql对应的关系
	 */
	/**退款、退货申请等待商家确认*/
	public static final byte SEARCH_RETURNSTATUS_14 = 14;
	/**买家已退货，等待商家确认收货*/
	public static final byte SEARCH_RETURNSTATUS_41 = 41;
	/**商家未收货，拒绝退款*/
	public static final byte SEARCH_RETURNSTATUS_61 = 61;
	/**商家拒绝退款申请*/
	public static final byte SEARCH_RETURNSTATUS_60 = 60;
    /**
     * 	前端搜索退款订单的stateCollection与sql对应的关系(/**状态集合:1商家待处理；2买家待处理；3已完成)
     */
    /**1商家待处理*/
    public static final byte STATE_COLLECTION_1 = 1;
    /**2买家待处理*/
    public static final byte STATE_COLLECTION_2 = 2;
    /**3已完成*/
    public static final byte STATE_COLLECTION_3 = 3;

	/**
	 * 买单订单状态
	 */
	/**未支付*/
	public static final byte STORE_STATUS_DEFAULT = 0;
	/**已支付*/
	public static final byte STORE_STATUS_PAY = 1;
	/**退款*/
	public static final byte STORE_STATUS_RETURN = 2;

	/**order status code */
	public static final byte ALL = 0;
	public static final byte WAIT_PAY = 1;
	public static final byte WAIT_DELIVERY = 2;
	public static final byte SHIPPED = 3;
	public static final byte FINISHED = 4;
	public static final byte REFUND = 5;
	/** 审核中 */
	public static final byte AUDIT = 6;
	/** 已取消 */
	public static final byte RETURNING = 7;
	/**
	 *门店列表待发货
	 */
	public static final byte STORE_ACCOUNT_WAIT_DELIVERY = 8;
	/**
	 * 门店列表已完成
	 */
	public static final byte STORE_ACCOUNT_FINISHED = 9;
	/**
	 * 等待售后
	 */
	public static final byte STORE_WAIT_REFUND = 10;

	/**
	 * 是否货到付款订单
	 */
	/** 否 */
	public static final Byte IS_COD_NO = 0;
	/** 是 */
	public static final Byte IS_COD_YES = 1;

	/** yes */
	public static final byte YES = 1;
	/** no */
	public static final byte NO = 0;
	/**奖品订单类型*/
	public static final ImmutableSet<Byte> AWARD_ORDER;

    static{
        AWARD_ORDER = ImmutableSet.<Byte>builder()
            .add(BaseConstant.ACTIVITY_TYPE_LOTTERY_PRESENT)
            .add(BaseConstant.ACTIVITY_TYPE_PROMOTE_ORDER)
            .add(BaseConstant.ACTIVITY_TYPE_PAY_AWARD)
            .add(BaseConstant.ACTIVITY_TYPE_ASSESS_ORDER)
            .build();
    }
    /**
     * 订单折扣类型 Discount Type
     */
    /**会员卡*/
    public static final Byte D_T_MEMBER_CARD = 0;
    /**优惠卷*/
    public static final Byte D_T_COUPON = 1;
    /**满折满减*/
    public static final Byte D_T_FULL_REDUCE = 2;
    /**预售*/
    public static final Byte D_T_FULL_PRE_SALE = 3;
    /**分销*/
    public static final Byte D_T_REBATE = 4;
    /**
     * table : b2c_customer_avail_coupons type 1为减价，2为打折
     */
    public static final byte T_CAC_TYPE_REDUCTION = 0;
    public static final byte T_CAC_TYPE_DISCOUNT = 1;

    /**
     *  默认运费模板 0：统一运费，1：满额包邮，
     */
    public static final Integer DEFAULT_SHIPPING_FEE_TEMPLATE_UNITY = 0;
    public static final Integer DEFAULT_SHIPPING_FEE_TEMPLATE_FREE_LIMIT = 1;

    /**
     *  运费模板分类：0：普通运费模板；1：重量运费模板
     */
    public static final Byte SHIPPING_FEE_TEMPLATE_NUMBER = 0;
    public static final Byte SHIPPING_FEE_TEMPLATE_WEIGHT = 1;

    /**
     *  会员卡 type 0线上 1线下
     */
    public static final Byte MEMBER_CARD_ONLINE = 0;
    public static final Byte MEMBER_CARD_OFFLINE = 1;

    /** 指定条件包邮（可选） 0:关闭，1:开启 */
    public static final Integer S_HAS_FEE_CONDITION_OPEN = 1;
    public static final Integer S_HAS_FEE_CONDITION_CLOSE = 0;

    /**
     * 除可配送区域外，其他不可配送开关
     * 0:不限制;1:限制
     */
    public static final Integer S_LIMIT_DELIVER_AREA_OPEN = 1;
    public static final Integer S_LIMIT_DELIVER_AREA_CLOSE = 0;

    /**
     * 计算运费->包邮地区规则
     * CALCULATE_FREE_SHIPPING_FEE_RULE_TYPE -> CFSFRT
     * 包邮类型（1:件数/公斤,2:金额,3:件数+金额/公斤+金额）
     */
    public static final Integer CFSFRT_NUMBER_OR_WEIGHT = 1;
    public static final Integer CFSFRT_AMOUNT = 2;
    public static final Integer CFSFRT_MIXING = 3;

    /**
     * 前端默认会员卡、优惠卷号
     */
    public static final String DEFAULT_COUPON_OR_ORDER_SN = "0";

    /**
     * 订单待支付自动取消默认时间(默认30minutes)
     */
    public static final int DEFAULT_AUTO_CANCEL_TIME = 30;

    /**
     * 是否是购物车
     */
    public static final Byte CART_Y = 1;
	/**
	 * 是处方订单
	 */
	public static final Byte PRESCRIPTION_ORDER_Y = 1;

    /**
     * admin订单搜索导航
     */
    public static final byte SEARCH_WAIT_DELIVERY = 1;
    public static final byte SEARCH_WAIT_TAKEDELIVER = 2;
    public static final byte SEARCH_RETURNING = 3;
    public static final byte SEARCH_RETURN_COMPLETED = 4;

    /**
     * 淘客订单类型：0：普通订单，1：京东订单，2：淘宝订单
     */
    public static final Byte TK_NORMAL = 0;
    public static final Byte TK_JD = 1;
    public static final Byte TK_TB = 2;

    /**赠品判断新老用户*/
    public static final Byte USER_ACTION_NEW = 1;
    public static final Byte USER_ACTION_OLD = 2;

    /**
     * 交易售后配置
     */

    /**
     * 店铺助手查询订单列表操作
     */
    /**发货逾期*/
    public static final Byte SHOP_HELPER_OVERDUE_DELIVERY = 0;
    /**提醒发货*/
    public static final Byte SHOP_HELPER_REMIND_DELIVERY = 1;
    /**退款申请逾期*/
    public static final Byte SHOP_HELPER_OVERDUE_RETURN_APPLY = 2;

    /**
     * 代付订单子单
     */
    public static class SubOrderConstant{
        /** 待支付*/
        public static final Byte SUB_ORDER_WAIT_PAY = 0;
        /** 已支付*/
        public static final Byte SUB_ORDER_PAY_OK = 1;
        /** 已关闭*/
        public static final Byte SUB_ORDER_CANCELLED = 2;
        /** *已退款*/
        public static final Byte SUB_ORDER_REFUND_SUCESS = 3;
    }

    /**
     * 退款原因描述
     */
    private static final String[] RETRURN_REASON_TYPE_DESC= new String[]{
        "协商一致退款",
        "未按约定时间发货",
        "缺货",
        "拍错/多拍/不想要",
        "其他",
        "医师审核未通过",
    };
    public static String getReturnReasonDesc(Integer index) {
        if(index == null || index < 0 || index > RETRURN_REASON_TYPE_DESC.length - 1) {
            index = RETRURN_REASON_TYPE_DESC.length - 1;
        }
        return RETRURN_REASON_TYPE_DESC[index];
    }

	/**
	 * 退款原因类型 医师审核未通过
	 */
	public static final Byte RETRURN_REASON_TYPE_DOCTOR_AUDIT = 6;
	public static final Byte RETRURN_REASON_TYPE_STORE = 4;

    /**
     * 订单综合查询排序规则ORDER_QUERY_SORT_RULE
     */
    /**申请售后时间*/
    public static final byte OQSR_APPLY_RETURN = 1;
    //*********药品状态*********//
	/**
	 * 未审核
	 */
	public static final byte MEDICAL_AUDIT_DEFAULT = 0;
	/**
	 * 审核通过
	 */
	public static final byte MEDICAL_AUDIT_PASS = 1;
	/**
	 * 审核未通过
	 */
	public static final byte MEDICAL_AUDIT_NOT_PASS = 2;
	//********订单得处方校验*******//
	/**
	 * 不校验
	 */
	public static final Byte CHECK_ORDER_PRESCRIPTION_NO_NEED =0;
	/**
	 * 通过
	 */
	public static final Byte CHECK_ORDER_PRESCRIPTION_PASS=1;
	/**
	 * 不通过
	 */
	public static final Byte CHECK_ORDER_PRESCRIPTION_NO_PASS=2;
	//*******订单药品类型*******//
	/**
	 * 非处方药品订单
	 */
	public static final Byte MEDICAL_TYPE_OTC = 0;
	/**
	 * 处方药(审核)
	 */
	public static final Byte MEDICAL_TYPE_RX = 1;
	//******订单审核流程*********//
	/**
	 *  0不审核
	 */
	public static final Byte MEDICAL_ORDER_AUDIT_TYPE_NOT = 0;
	/**
	 *  1审核
	 */
	public static final Byte MEDICAL_ORDER_AUDIT_TYPE_AUDIT = 1;
	/**
	 * 2开方
	 */
	public static final Byte MEDICAL_ORDER_AUDIT_TYPE_CREATE = 2;
	/**
	 * 3根据处方下单
	 */
	public static final Byte MEDICAL_ORDER_AUDIT_TYPE_PRESCRIPTION = 3;

	//*************his 订单状态 **************//
	/**
	 * 下单
	 */
	public static final Byte EXTERNAL_HIS_ORDER_STATUS_ORDER =1;
	/**
	 * 退款
	 */
	public static final Byte EXTERNAL_HIS_ORDER_STATUS_REFUND =0;
    /**
     * 未删除
     */
    public static final Byte NORMAL_DEL=0;


    /**对外接口 start*/
    /**
     *  退款结果：同意agree 拒绝refuse
     */
    public static final String API_RETURN_AGREE = "agree";
    public static final String API_RETURN_REFUSE = "refuse";
    /**
     * erp 退款/退款订单状态ERP_RETURN_STATUS ->ERS
     */
    /**等待卖家同意*/
    private static final String ERS_WAIT_SELLER_AGREE= "WAIT_SELLER_AGREE";
    /**等待买家退货*/
    private static final String ERS_WAIT_BUYER_RETURN_GOODS = "WAIT_BUYER_RETURN_GOODS";
    /**卖家拒绝*/
    private static final String ERS_SELLER_REFUSE_BUYER = "SELLER_REFUSE_BUYER";
    /**成功*/
    private static final String ERS_SUCCESS = "SUCCESS";
    /**关闭*/
    private static final String ERS_CLOSED = "CLOSED";
    /**等待卖家收货*/
    private static final String ERS_WAIT_SELLER_CONFIRM_GOODS = "WAIT_SELLER_CONFIRM_GOODS";
    /**RER 与 小程序退款状态转化*/
    private static final ImmutableMap<Byte, String> ERP_RETURN_STATUS;

    static{
        ERP_RETURN_STATUS = ImmutableMap.<Byte, String>builder()
            .put(REFUND_STATUS_AUDITING, ERS_WAIT_SELLER_AGREE)
            .put(REFUND_STATUS_AUDIT_PASS, ERS_WAIT_BUYER_RETURN_GOODS)
            .put(REFUND_STATUS_AUDIT_NOT_PASS, ERS_SELLER_REFUSE_BUYER)
            .put(REFUND_STATUS_APPLY_REFUND_OR_SHIPPING, ERS_WAIT_SELLER_AGREE)
            .put(REFUND_STATUS_FINISH, ERS_SUCCESS)
            .put(REFUND_STATUS_REFUSE, ERS_SELLER_REFUSE_BUYER)
            .put(REFUND_STATUS_CLOSE, ERS_CLOSED)
            .build();
    }

    public static String getErpReturnStatus(Byte status, boolean isSubmitInfo) {
        if(REFUND_STATUS_APPLY_REFUND_OR_SHIPPING == status && isSubmitInfo) {
            return ERS_WAIT_SELLER_CONFIRM_GOODS;
        }
        return ERP_RETURN_STATUS.get(status);
    }
    /**对外接口 end*/
}
