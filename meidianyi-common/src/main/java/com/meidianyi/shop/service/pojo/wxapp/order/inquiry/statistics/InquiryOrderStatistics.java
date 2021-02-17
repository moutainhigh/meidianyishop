package com.meidianyi.shop.service.pojo.wxapp.order.inquiry.statistics;

import lombok.Data;

/**
 * @author yangpengcheng
 * @date 2020/8/3
 **/
@Data
public class InquiryOrderStatistics {
    /**
     *时间
     */
    public static  final String CREAT_TIME="createTime";
    /**
     * 医师id
     */
    public static  final String DOCTOR_ID="doctorId";
    /**
     * 医师姓名
     */
    public static  final String DOCTOR_NAME="doctorName";
    /**
     * 科室id
     */
    public static  final String DEPARTMENT_ID="departmentId";
    /**
     * 科室名称
     */
    public static  final String DEPARTMENT_NAME="departmentName";
    /**
     * 咨询单数
     */
    public static  final String AMOUNT="amount";
    /**
     *咨询单次价格
     */
    public static  final String ONE_PRICE="oncePrice";
    /**
     *咨询总金额
     */
    public static  final String AMOUNT_PRICE="amountPrice";
    /**
     * 咨询单数
     */
    public static  final String AMOUNT_TOTAL="amountTotal";
    /**
     *咨询单次价格
     */
    public static  final String ONE_PRICE_TOTAL="oncePriceTotal";
    /**
     *咨询总金额
     */
    public static  final String AMOUNT_PRICE_TOTAL="amountPriceTotal";
    /**
     * 店铺id
     */
    public static final String SHOP_ID="shopId";
    /**
     * 店铺名称
     */
    public static final String SHOP_NAME="shopName";
}
