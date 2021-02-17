package com.meidianyi.shop.common.foundation.data;

/**
 * 会话常量
 * @author 李晓冰
 * @date 2020年07月22日
 */
public class ImSessionConstant {
    /**
     * 待支付
     */
    public static final Byte SESSION_READY_TO_PAY = 0;
    /**
     * 医师待接诊
     */
    public static final Byte SESSION_READY_TO_START = 1;
    /**待接诊权重*/
    public static final Byte SESSION_READY_TO_START_WEIGHT = 4;

    /**
     * 会话中
     */
    public static final Byte SESSION_ON = 2;
    /**会话中权重*/
    public static final Byte SESSION_ON_WEIGHT = 6;
    /**
     * 会话取消
     */
    public static final Byte SESSION_CANCEL = 3;
    /**会话取消权重*/
    public static final Byte SESSION_CANCEL_WEIGHT = 1;
    /**
     * 会话正常结束
     */
    public static final Byte SESSION_END = 4;
    /**会话结束权重*/
    public static final Byte SESSION_END_WEIGHT = 2;

    /**
     * 继续问诊
     */
    public static final Byte SESSION_CONTINUE_ON = 5;
    /**继续问诊权重*/
    public static final Byte SESSION_CONTINUE_ON_WEIGHT =5;

    /**
     * 会话终止
     */
    public static final Byte SESSION_DEAD = 6;
    /**会话终止权重*/
    public static final Byte SESSION_DEAD_WEIGHT = 1;

    /**会话接诊后退款*/
    public static final Byte SESSION_REFUND = 7;
    public static final Byte SESSION_REFUND_WEIGHT = 2;

    /**
     * 会话取消等待时间 24小时
     */
    public static final Integer CANCEL_LIMIT_TIME = 24;

    /**
     * 可以继续问诊次数
     */
    public static final Integer CONTINUE_SESSION_TIME = 1;

    /**不可评价*/
    public static final Byte SESSION_EVALUATE_CAN_NOT_STATUS = 0;
    /**可评价*/
    public static final Byte SESSION_EVALUATE_CAN_STATUS = 1;
    /**已评价*/
    public static final Byte SESSION_EVALUATE_ALREADY_STATUS = 2;

    /**
     * 会话关闭默认等待时间24小时
     */
    public static final Integer CLOSE_LIMIT_TIME = 24;

    /**会话可用 可发消息*/
    public static final Byte SESSION_CAN_USE = 1;
    /**会话不可用 不可发消息*/
    public static final Byte SESSION_CAN_NOT_USE = 0;

    /**
     * 文本
     */
    public static final Byte SESSION_ITEM_TYPE_TEXT = 0;
    /**
     * 图片
     */
    public static final Byte SESSION_ITEM_TYPE_PICTURE = 1;
    /**
     * 处方
     */
    public static final Byte SESSION_ITEM_TYPE_PRESCRIPTION = 2;
}
