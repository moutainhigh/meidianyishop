package com.meidianyi.shop.service.pojo.shop.prescription.config;

/**
 * @author 孔德成
 * @date 2020/7/8 15:07
 */
public interface PrescriptionConstant {
    //********处方来源******//
    /**
     * 0系统内
     */
    public static final Byte SOURCE_MP_SYSTEM =0;
    /**
     * 1his系统
     */
    public static final Byte SOURCE_HIS_SYSTEM =1;
    //******审核状态********//
    /**
     * 0待审核
     */
    public static final Byte STATUS_NO_NEED=0;
    /**
     * 1审核通过
     */
    public static final Byte STATUS_PASS=1;
    /**
     * 2审核未通过
     */
    public static final Byte STATUS_NO_PASS=2;
    //********有效期*******//
    /**
     * 失效
     */
    public static final Byte EXPIRE_TYPE_INVALID=0;
    /**
     * 永久有效
     */
    public static final Byte EXPIRE_TYPE_EVER =1;
    /**
     * 时间段内有效
     */
    public static final Byte EXPIRE_TYPE_TIME=2;

    /**
     * 处方号前缀
     */
    public static final String PRESCRIPTION_CODE_PREFIX="C";
    /**
     * 处方明细号前缀
     */
    public static final String PRESCRIPTION_DETAIL_CODE_PREFIX="M";
    /**
     * 默认过期时间
     */
    public static final int PRESCRIPTION_EXPIRE_DAY=3;

    /**
     * 处方审核类型 药品审核类型, 0不审核,1审核,2开方,3根据处方下单
     */
    public static final Byte PRESCRIPTION_AUDIT_TYPE_NOT_AUDIT = 0;
    public static final Byte PRESCRIPTION_AUDIT_TYPE_AUDIT = 1;
    public static final Byte PRESCRIPTION_AUDIT_TYPE_PRESCRIBE = 2;
    public static final Byte PRESCRIPTION_AUDIT_TYPE_ORDER = 3;
    /**
     * 结算状态，处方返利使用 0未结算 1已结算 2不返利
     */
    public final static Byte SETTLEMENT_WAIT=0;
    public final static Byte SETTLEMENT_FINISH=1;
    public final static Byte SETTLEMENT_NOT=2;

    /**
     * 已使用
     */
    public final static Byte HAS_USED=1;
}
