package com.meidianyi.shop.service.pojo.shop.rebate;

/**
 * @author yangpengcheng
 * @date 2020/8/27
 **/
public class DoctorWithdrawConstant {
    /**
     * 小程序提现类型,小程序公众号wx_open、wx_mini、子商户sub_mch
     */
    public static final Byte RT_WX_OPEN=1;
    public static final Byte RT_WX_MINI=2;
    public static final Byte RT_SUB_MCH=3;

    public static final String ORDER_SN_PREFIX="T";

    /**元_分的比例*/
    public final static byte YUAN_FEN_RATIO = 100;
    /**
     * 提现审核状态 1待审核 2拒绝 3已审核待出账 4出账成功 5失败 6已发红包
     */
    public static final Byte WITHDRAW_CHECK_WAIT_CHECK = 1;
    public static final Byte WITHDRAW_CHECK_REFUSE = 2;
    public static final Byte WITHDRAW_CHECK_WAIT_PAY = 3;
    public static final Byte WITHDRAW_CHECK_PAY_SUCCESS = 4;
    public static final Byte WITHDRAW_CHECK_PY_FAIL = 5;
    public static final Byte WITHDRAW_CHECK_SEND_PACKAGE = 6;

}
