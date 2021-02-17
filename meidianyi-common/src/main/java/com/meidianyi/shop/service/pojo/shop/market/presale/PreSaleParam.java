package com.meidianyi.shop.service.pojo.shop.market.presale;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

/**
 * 定金膨胀活动入参
 *
 * @author 郑保乐
 */
@Data
public class PreSaleParam {


    /** 活动id **/
    private Integer id;
    /** 活动类型 **/
    @NotNull
    private Byte presaleType;
    /**优先级*/
    private Integer first;
    /** 活动名称 **/
    @NotNull
    private String presaleName;
    /** 定金期数 1/2**/
    @NotNull
    private Byte prePayStep;
    /** 1定金支付开始时间 **/
    @NotNull
    private Timestamp preStartTime;
    /** 1定金支付结束时间 **/
    @NotNull
    private Timestamp preEndTime;
    /** 2段定金支付开始时间 **/
    private Timestamp preStartTime2;
    /** 2段定金支付结束时间 **/
    private Timestamp preEndTime2;
    /** 尾款支付开始时间 **/
    private Timestamp startTime;
    /** 尾款支付结束时间 **/
    private Timestamp endTime;
    /** 商品id **/
    private String goodsId;
    @NotNull
    /** 预告时间 预告时间：-1：立刻预告；0：不预告；大于0：开始前预告小时数*/
    private Integer preTime;
    /** 发货时间模式 **/
    @NotNull
    private Byte deliverType;
    /** 指定发货时间 **/
    private Timestamp deliverTime;
    /** 尾款支付几天后发货 **/
    private Integer deliverDays;
    /** 优惠叠加策略 **/
    @NotNull
    private Byte discountType;
    /** 定金退款策略 **/
    @NotNull
    private Byte returnType;
    /** 预售数量展示 **/
    @NotNull
    private Integer showSaleNumber;
    /** 商品购买方式 **/
    @NotNull
    private Byte buyType;
    /** 最大购买数量 **/
    @NotNull
    private Integer buyNumber;
    /** 分享样式 **/
    @NotNull
    private Byte shareAction;
    /** 分享文案 **/
    private String shareDoc;
    /** 分享图来源 **/
    private Byte shareImgAction;
    /** 自定义图片 **/
    private String shareImg;
    /** 活动商品 **/
    @NotEmpty
    @Valid
    private List<ProductParam> products;
}
