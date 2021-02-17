package com.meidianyi.shop.common.foundation.data;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @author:lijianpeng
 */
public class WithdrawConstant {

    /**
     * 开关定义
     */
    public static final byte ON = 1;
    public static final byte OFF = 0;

    /**
     * 返利方式：小程序wx_mini、公众号wx_open、子商户sub_mch
     */
    public static Byte RT_WX_OPEN = 1;
    public static Byte RT_WX_MINI = 2;
    public static Byte RT_SUB_MCH = 3;

    /** 佣金提现方式 **/
    public static String[] RT_DES = {"wx_open", "wx_mini", "sub_mch"};

    /** 佣金提现方式Map **/
    public static byte getWithdrawType(String type) {
        for (int i = 0, size = RT_DES.length; i < size; i++) {
            if(RT_DES[i].equals(type)) {
                return (byte)++i;
            }
        }
        return (byte)0;
    }

    /** 提现订单前缀 **/
    public static String ORDER_SN_PREFIX = "T";

    /** 审核操作类型 **/
    public static String[] OPERATE_ACTION = {"pass", "refuse", "remark", "billing"};

    /**
     * 提现单状态
     */
    public static final Map<Integer, String> STATUS_MAP = ImmutableMap.<Integer, String>builder()
        .put(1, "待审核")
        .put(2, "已驳回申请")
        .put(3, "已审核，待出账")
        .put(4, "出账成功")
        .put(5, "出账失败")
        .put(6, "红包已发放")
        .build();

    /**
     * 提现审核状态 1待审核 2拒绝 3已审核待出账 4出账成功 5失败 6已发红包
     */
    public static Byte WITHDRAW_CHECK_WAIT_CHECK = 1;
    public static Byte WITHDRAW_CHECK_REFUSE = 2;
    public static Byte WITHDRAW_CHECK_WAIT_PAY = 3;
    public static Byte WITHDRAW_CHECK_PAY_SUCCESS = 4;
    public static Byte WITHDRAW_CHECK_PY_FAIL = 5;
    public static Byte WITHDRAW_CHECK_SEND_PACKAGE = 6;
}
