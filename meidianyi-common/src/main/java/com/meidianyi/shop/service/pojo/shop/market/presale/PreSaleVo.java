package com.meidianyi.shop.service.pojo.shop.market.presale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 定金膨胀活动入参
 *
 * @author 郑保乐
 */
@Data
public class PreSaleVo  {

    /** 活动id **/
    private Integer id;
    /** 活动类型 **/
    private Byte presaleType;
    /** 活动名称 **/
    private String presaleName;
    /** 定金期数 **/
    private Byte prePayStep;
    /** 定金支付开始时间 **/
    private Timestamp preStartTime;
    /** 定金支付结束时间 **/
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
    private String  goodsId;
    /** 商品名称 **/
    private String goodsName;
    /** 发货时间模式 **/
    private Byte deliverType;
    /** 指定发货时间 **/
    private Timestamp deliverTime;
    /** 尾款支付几天后发货 **/
    private Integer deliverDays;
    /** 优惠叠加策略 **/
    private Byte discountType;
    /** 定金退款策略 **/
    private Byte returnType;
    /** 预售数量展示 **/
    private Byte showSaleNumber;
    /** 商品购买方式 **/
    private Byte buyType;
    /** 最大购买数量 **/
    private Integer buyNumber;
    /** 活动商品 **/
    private List<ProductVo> products;
    /** 分享配置 **/
    private Byte shareAction;
    private String shareDoc;
    private Byte shareImgAction;
    private String shareImg;
    /** 活动状态 **/
    private Byte status;
    /** 预告时间 预告时间：-1：立刻预告；0：不预告；大于0：开始前预告小时数*/
    private Integer preTime;
    /**优先级*/
    private Integer first;
    /**
     * 商品规格
     */
    private List<PreSaleGoods> goodsList;

    @JsonIgnore
    private Integer saleNumber;
    @JsonIgnore
    private String shareConfig;
    @JsonIgnore
    private Byte delFlag;
    @JsonIgnore
    private Timestamp createTime;
    @JsonIgnore
    private Timestamp updateTime;


    @Setter
    @Getter
    public static class PreSaleGoods{
        private Integer goodsId;
        private String goodsName;
        /** 商品主图 */
        private String goodsImg;
        /** 商品库存 */
        private Integer goodsNumber;

        /** 商品价格 */
        private BigDecimal shopPrice;
        /** 单位 */
        private String unit;
        /**
         * 产品规格配置
         */
        private List<ProductVo> productList;
    }



}
