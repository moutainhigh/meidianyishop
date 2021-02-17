package com.meidianyi.shop.service.pojo.shop.overview.commodity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;

/**
 * @author liufei
 * date 2019/7/22
 */
@Data
public class ProductOverviewVo {
    /**
     * 在架商品数
     */
    private int onShelfGoodsNum;
    /**
     * 动销商品数(统计时间内，销量不为0的商品数量)
     */
    private int soldGoodsNum;
    /**
     * 被访问商品数
     */
    private int visitedGoodsNum;
    /**
     * uv(商品访客数)
     */
    private int goodsUserVisit;
    /**
     * pv(商品浏览量)
     */
    private int goodsPageviews;
    /**
     * 加购人数
     */
    private int purchaseNum;
    /**
     * 加购件数
     */
    private int purchaseQuantity;
    /**
     * 付款商品数
     */
    private int paidGoodsNum;
    /**
     * 下单商品数
     */
    private int orderGoodsNum;
    /**
     * 商品访问付款转化率
     */
    private BigDecimal visit2paid = BigDecimal.ZERO;

    /**
     * The Change rate.较前N日的变化率
     * K: field-name
     * V: 变化率
     */
    Map<String, BigDecimal> changeRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endTime;

    public ProductOverviewVo() {
    }

    private ProductOverviewVo(Builder builder) {
        this.onShelfGoodsNum = builder.onShelfGoodsNum;
        this.soldGoodsNum = builder.soldGoodsNum;
        this.visitedGoodsNum = builder.visitedGoodsNum;
        this.goodsUserVisit = builder.goodsUserVisit;
        this.goodsPageviews = builder.goodsPageviews;
        this.purchaseNum = builder.purchaseNum;
        this.purchaseQuantity = builder.purchaseQuantity;
        this.paidGoodsNum = builder.paidGoodsNum;
        this.orderGoodsNum = builder.orderGoodsNum;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder implements com.meidianyi.shop.service.pojo.shop.overview.Builder<ProductOverviewVo> {
        private int onShelfGoodsNum;
        private int soldGoodsNum;
        private int visitedGoodsNum;
        private int goodsUserVisit;
        private int goodsPageviews;
        private int purchaseNum;
        private int purchaseQuantity;
        private int paidGoodsNum;
        private int orderGoodsNum;

        public Builder() {
        }

        @Override
        public void reset() {
            this.onShelfGoodsNum = 0;
            this.soldGoodsNum = 0;
            this.visitedGoodsNum = 0;
            this.goodsUserVisit = 0;
            this.goodsPageviews = 0;
            this.purchaseNum = 0;
            this.purchaseQuantity = 0;
            this.paidGoodsNum = 0;
            this.orderGoodsNum = 0;
        }

        @Override
        public ProductOverviewVo build() {
            return new ProductOverviewVo(this);
        }

        public ProductOverviewVo.Builder onShelfGoodsNum(int onShelfGoodsNum) {
            this.onShelfGoodsNum = onShelfGoodsNum;
            return this;
        }

        public ProductOverviewVo.Builder soldGoodsNum(int soldGoodsNum) {
            this.soldGoodsNum = soldGoodsNum;
            return this;
        }

        public ProductOverviewVo.Builder visitedGoodsNum(int visitedGoodsNum) {
            this.visitedGoodsNum = visitedGoodsNum;
            return this;
        }

        public ProductOverviewVo.Builder goodsUserVisit(int goodsUserVisit) {
            this.goodsUserVisit = goodsUserVisit;
            return this;
        }

        public ProductOverviewVo.Builder goodsPageviews(int goodsPageviews) {
            this.goodsPageviews = goodsPageviews;
            return this;
        }

        public ProductOverviewVo.Builder purchaseNum(int purchaseNum) {
            this.purchaseNum = purchaseNum;
            return this;
        }

        public ProductOverviewVo.Builder purchaseQuantity(int purchaseQuantity) {
            this.purchaseQuantity = purchaseQuantity;
            return this;
        }

        public ProductOverviewVo.Builder paidGoodsNum(int paidGoodsNum) {
            this.paidGoodsNum = paidGoodsNum;
            return this;
        }

        public ProductOverviewVo.Builder orderGoodsNum(int orderGoodsNum) {
            this.orderGoodsNum = orderGoodsNum;
            return this;
        }
    }
}