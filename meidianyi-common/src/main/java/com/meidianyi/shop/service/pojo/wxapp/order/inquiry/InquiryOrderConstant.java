package com.meidianyi.shop.service.pojo.wxapp.order.inquiry;

/**
 * @author 杨鹏程
 * @date 2020/7/22
 **/
public class InquiryOrderConstant {
    //订单状态
    /**
     * 待支付
     */
    public final static Byte ORDER_TO_PAID=0;
    /**
     * 待接诊
     */
    public final static Byte ORDER_TO_RECEIVE=1;
    /**
     * 接诊中
     */
    public final static Byte ORDER_RECEIVING=2;
    /**
     * 已完成
     */
    public final static Byte ORDER_FINISHED=3;
    /**
     * 已退款
     */
    public final static Byte ORDER_REFUND=4;
    /**
     * 已取消
     */
    public final static Byte ORDER_CANCELED=5;
    /**
     * 待退款
     */
    public final static Byte ORDER_TO_REFUND=6;
    /**
     * 部分退款
     */
    public final static Byte ORDER_PART_REFUND=7;

    /**
     *  超时时间
     */
    public final static int EXPIRY_TIME_HOUR=24;

    /**
     * 微信支付
     */
    public final static String PAY_CODE_WX_PAY = "wxpay";

    /**
     *   订单号前缀
     */
    public final static String INQUIRY_ORDER_SN_PREFIX="Z";

    /**
     * 商品名称
     */

    public final static String GOODS_NAME="问诊咨询";

    /**
     * 退款状态 1成功 2失败
     */
    public final static Byte REFUND_SUCCESS=1;
    public final static Byte REFUND_FAILED=2;
    /**
     *退款原因
     */
    public final static String REFUND_REASON_OVERTIME="超时自动退款";
    public final static String REFUND_REASON_DOCTOR="医师退款";
    /**
     * 结算状态，返利使用 0未结算 1已结算 2不返利
     */
    public final static Byte SETTLEMENT_WAIT=0;
    public final static Byte SETTLEMENT_FINISH=1;
    public final static Byte SETTLEMENT_NOT=2;
}
