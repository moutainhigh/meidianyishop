package com.meidianyi.shop.service.pojo.shop.doctor;

/**
 * @author chenjie
 * @date 2020年08月11日
 */
public class DoctorConstant {
    /**
     * 咨询列表查询关注参数
     */
    public static final Byte ATTENTION_TYPE = 1;
    /**
     * 上班状态
     */
    public static final Byte ON_DUTY = 1;
    /**
     * 休息状态
     */
    public static final Byte NOT_ON_DUTY = 0;
    /**
     * 自动上下班类型
     */
    public static final Byte DOCTOC_DUTY_AUTOMATIC = 0;

    /**
     * 综合排序
     */
    public static final Byte COMMON_SORT_TYPE = 0;
    /**
     * 职称级别排序
     */
    public static final Byte TITLE_SORT_TYPE = 1;
    /**
     * 医师评价排序
     */
    public static final Byte COMMENT_SORT_TYPE = 2;
    /**
     * 响应时间排序
     */
    public static final Byte ANSWER_SORT_TYPE = 3;
    /**
     * 接诊数排序
     */
    public static final Byte CONSULTATION_SORT_TYPE = 4;
    /**
     * 关注数排序
     */
    public static final Byte ATTENTION_SORT_TYPE = 5;
    /**
     * 启用
     */
    public static final Byte ABLE = 1;
    /**
     * 禁用
     */
    public static final Byte DISABLE = 0;
    /**
     * 关注
     */
    public static final Byte ATTENTION = 1;

    /**
     * 10分钟内
     */
    public static final Byte TEN_MUNITE_IN = 1;
    /**
     * 半小时内
     */
    public static final Byte HALF_HOUR_IN = 2;
    /**
     * 1小时内
     */
    public static final Byte ONE_HOUR_IN = 3;
    /**
     * 超过1小时
     */
    public static final Byte ONE_HOUR_OUT = 4;
    /**
     * 是否拉取过 1是0否
     */
    public static final Byte IS_FETCH=1;
    public static final Byte IS_NOT_FETCH=0;
    /**
     * 是否接诊
     */
    public static final Byte CAN_CONSULTATION=1;
    public static final Byte CAN_NOT_CONSULTATION=0;
}
