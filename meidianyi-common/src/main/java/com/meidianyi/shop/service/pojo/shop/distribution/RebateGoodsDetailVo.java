package com.meidianyi.shop.service.pojo.shop.distribution;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.Data;

/**
 * 商品返利明细列表出参
 * @author 常乐
 * 2019年8月12日
 */
@Data
public class RebateGoodsDetailVo {
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品数量
     */
    private Integer goodsNumber;
    /**
     * 退款数量
     */
    private Integer returnNumber;
    /**
     * 返利订单号
     */
    private String orderSn;
    /**
     * 下单用户手机号
     */
    private String mobile;
    /**
     * 下单用户id
     */
    private Integer userId;
    /**
     * 下单用户昵称
     */
    private String username;
    /**
     * 分销员id
     */
    private Integer distributorId;
    /**
     * 分销员手机号
     */
    private String distributorMobile;
    /**
     * 分销员昵称
     */
    private String distributorName;
    /**
     * 返利比例
     */
    private BigDecimal rebatePercent;
    /**
     * 返利状态
     */
    private Byte settlementFlag;
    /**
     * 返利日期
     */
    private Timestamp finishedTime;
    /**
     * 商品图片
     */
    private String goodsImg;
    /**
     * 返利关系：0 自购返利 1 直接返利 2 二级返利
     */
    private Byte rebateLevel;
    /**
     * 分销员真实姓名
     */
    private String distributorRealName;
    /**
     * 商品返利佣金金额
     */
    private BigDecimal realRebateMoney;
    /**
     * 返利商品金额
     */
    private BigDecimal canCalculateMoney;
}
