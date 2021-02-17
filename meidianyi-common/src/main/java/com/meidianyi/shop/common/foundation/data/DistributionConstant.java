package com.meidianyi.shop.common.foundation.data;

import java.sql.Date;

/**
 * 分销返利常量
 * @author 王帅
 */
public class DistributionConstant {
    /**
     * 分销等级1-5
     */
    public static Byte LEVEL_1 = 1;
    public static Byte LEVEL_2 = 2;
    public static Byte LEVEL_3 = 3;
    public static Byte LEVEL_4 = 4;
    public static Byte LEVEL_5 = 5;

    /**
     * 返利类型:1返利订单
     */
    public static Byte REBATE_ORDER = 1;

    /**
     * 返利等级
     * 0自购返利自己；1返利直接上级；2返利间件上级
     */
    public static Byte REBATE_LEVEL_0 = 0;
    public static Byte REBATE_LEVEL_1 = 1;
    public static Byte REBATE_LEVEL_2 = 2;

    /**
     * 返利商品类型 0：全部商品；1：部分商品
     */
    public static Byte ALL_GOODS = 0;
    public static Byte PART_GOODS = 1;

    /**
     * 返利类型 0：自购返利；1：直接返利；2：间接返利
     */
    public static Byte REBATE_TYPE_SELF = 0;
    public static Byte REBATE_TYPE_DIRECT = 1;
    public static Byte REBATE_TYPE_INDIRECT = 2;

    /**
     * 是否赠送优惠券 0：不赠送；1：赠送
     */
     public static Byte NOT_SEND_COUPON = 0;
     public static Byte SEND_COUPON = 1;

    /**
     * '佣金计算方式;0:商品实际支付金额*佣金比例；1：商品实际利润（实际支付金额-成本价）* 佣金比例'
     */
    public static Byte STRATEGY_TYPE_PAYMENT = 0;
    public static Byte STRATEGY_TYPE_PROFIT = 1;

    /**
     * 分销员列表表头排序 1；下级用户数；2：间接邀请用户数；3：累计返利商品总额；4：累计获得佣金总额；5：待返利佣金总额：
     */
    public static Byte SORT_BY_NEXT_NUM = 1;
    public static Byte SORT_BY_SUBLAYER_NUM = 2;
    public static Byte SORT_BY_TOTAL_CAN_FANLI = 3;
    public static Byte SORT_BY_TOTAL_FANLI = 4;
    public static Byte SORT_BY_WAIT_FANLI = 5;

    /**
     * 永久返利有效期永久有效：1970-01-13
     */
    public static Date NO_INVITE_EXPIRY = Date.valueOf("1970-01-13");

    /**
     * 订单返利状态 0：待返利；1：已返利；2：不返利
     */
    public static final Byte WAIT_SETTLEMENT_FLAG = 0;
    public static final Byte HAS_SETTLEMENT_FLAG = 1;
    public static final Byte NO_SETTLEMENT_FLAG = 2;

    /**
     * 分销配置-审核开关 0：关闭；1：开启
     */
    public final static Byte JUDGE_STATUS_CLOSE = 0;
    public final static Byte JUDGE_STATUS_OPEN = 1;

    /**
     * 返利方式：小程序wx_mini、公众号wx_open、子商户sub_mch
     */
    public static Byte RT_WX_OPEN = 1;
    public static Byte RT_WX_MINI = 2;
    public static Byte RT_SUB_MCH = 3;

    public static String[] RT_DES = {"wx_open", "wx_mini", "sub_mch"};

    public static byte getWithdrawType(String type) {
        for (int i = 0, size = RT_DES.length; i < size; i++) {
            if(RT_DES[i].equals(type)) {
                return (byte)++i;
            }
        }
        return (byte)0;
    }

    /**
     * 提现订单前缀
     */
    public static String ORDER_SN_PREFIX = "T";
    /**
     * 返利金额，最多有两级
     */
    public static int LIMIT_REBATE_LEVEL = 2;

    /**
     * 分销邀请关系 0：全部；1：直接邀请；2：间接邀请
     */
    public static final Integer INVITE_RELATION_ALL = 0;
    public static final Integer INVITE_RELATION_DIRECTLY = 1;
    public static final Integer INVITE_RELATION_INDIRECT = 2;

    /**
     * 用户类型（邀请有效期） 0：有效用户；1：即将过期用户；2：已失效用户
     */
    public static final Integer USER_EFFECTIVE = 0;
    public static final Integer USER_WILL_EXPIRE = 1;
    public static final Integer USER_HAS_EXPIRE = 2;

    /**
     * 有效期集合 0:都不选；1:都选； 2:保护有效期剩余不超过10天；3：返利有效期不超过10天
     */
    public static final Integer ALL_NOT_EXPIRE_PROTECT = 0;
    public static final Integer ALL_EXPIRE_PROTECT = 1;
    public static final Integer PROTECT_LEFT_TEN = 2;
    public static final Integer EXPIRE_LEFT_TEN = 3;

    /**
     * 分销员邀请用户排序字端
     */
    public static final String INVITE_TIME = "inviteTime";
    public static final String ORDER_NUMBER = "orderNumber";
    public static final String TOTAL_FANLI_MONEY = "totalFanliMoney";
    public static final String SORT_TYPE = "desc";


    /**
     * 性别 f:女；m:男
     */
    public static final String SEX_F = "f";
    public static final String SEX_M = "m";

    /**
     * 分销开关 0：关闭;1：开启
     */
    public static final Byte DISTRIBUTION_SWITCH_CLOSE = 0;
    public static final Byte DISTRIBUTION_SWITCH_OPEN = 1;


    /**
     * 是否分销员 1：是，0：否
     */
    public final static Byte IS_DISTRIBUTOR = 1;
    public final static Byte NOT_DISTRIBUTOR = 0;


    /**
     * 独立商品推广页开关 0：关闭；1：开启
     */
    public final static Byte DISTRIBUTOR_PROMOTE_CLOSE = 0;
    public final static Byte DISTRIBUTOR_PROMOTE_OPEN = 1;

    /**
     * 该分销员是否有独立商品推广页权限 1：有；0：没有
     */
    public final static Byte PERSONAL_PROMOTE = 1;
    public final static Byte NO_PERSONAL_PROMOTE = 0;

    /**
     * 独立推广页权限 0：全部分销员；1：部分分销员
     */
    public final static Byte ALL_DISTRIBUTOR = 0;
    public final static Byte PART_DISTRIBUTOR = 1;

    /**
     * 分销员是否可直接绑定 0：否；1：是
     */
    public final static Byte DIRECTLY_BING_NO = 0;
    public final static Byte DIRECTLY_BING_YES = 1;

    /**
     * 是否是个人推广中心（区分与推广中心）
     */
    public final static Byte PERSONAL = 1;

    /**
     * 收藏类型 0：收藏；1：取消收藏
     */
    public final static Byte ADD_COLLECTION = 0;
    public final static Byte CANCEL_COLLECTION = 1;

    /**
     * 是否收藏 0：否；1：是
     */
    public final static Byte NO_COLLECTION = 0;
    public final static Byte COLLECTION = 1;

    /**
     * 是否已上传二维码 0：未上传 1：已上传
     */
    public final static Byte NO_QR_CODE = 0;
    public final static Byte QR_CODE = 1;

    /**
     * 分销员微信图片前缀
     */
    public final static String DISTRIBUTOR_IMAGE_PREFIX = "qr_code_";

    /**
     * 佣金计算方式 0:实际支付金额*返利比例；1：实际利润*返利比例
     */
    public final static Byte STRATEGY_TYPE_GOODS_RPICE = 0;
    public final static Byte STRATEGY_TYPE_PROFITS_RPICE = 1;

    /**
     * 用户绑定分享链接来源 0：其他来源；1：独立商品推广页
     */
    public final static Byte INVITE_SOURCE_OTHERS = 0;
    public final static Byte INVITE_SOURCE_PROMOTE = 1;

    /**
     * 分销等级升级类型 0自动，1手动
     */
    public final static Byte LEVEL_UP_ROUTE_AUTO = 0;
    public final static Byte LEVEL_UP_ROUTE_MANUAL = 1;

    /**
     * 分销员等级启用/停用状态  0停用，1启用
     */
    public final static Byte LEVEL_STATUS_OFF = 0;
    public final static Byte LEVEL_STATUS_ON = 1;
}
