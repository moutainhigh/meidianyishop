package com.meidianyi.shop.service.pojo.shop.rebate;

/**
 * @author yangpengcheng
 * @date 2020/8/24
 **/
public class InquiryOrderRebateConstant {
    /**
     * 待返利
     */
    public static final Byte TO_REBATE=0;
    /**
     * 已返利
     */
    public static final Byte REBATED=1;
    /**
     * 未返利
     */
    public static final Byte REBATE_FAIL=2;

    /**
     *未返利原因
     */
    public final static String REASON_OVERTIME="超时自动退款";
    public final static String REASON_DOCTOR_REFUND="医师退款";
    public final static String REASON_OPERATE_REFUND="手动退款";
}
