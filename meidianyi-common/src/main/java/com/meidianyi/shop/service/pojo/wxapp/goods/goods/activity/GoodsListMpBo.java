package com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 小程序-商品列表信息存储Bo类，
 * 继承自{@link com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpVo}，
 * 本类内的字段不会返回到前端，所有属性都要加@JsonIgnore注解
 * @author 李晓冰
 * @date 2019年10月29日
 */
@Getter
@Setter
public class GoodsListMpBo extends GoodsListMpVo {

    //************ElasticSearch中的数据**************start
    /** 平台、商家、品牌分类id */
    @JsonIgnore
    private Integer catId;
    @JsonIgnore
    private Integer sortId;
    @JsonIgnore
    private Integer brandId;
    /**商品初始销量*/
    @JsonIgnore
    private Integer baseSale;
    /**商品主表内存储的价格*/
    @JsonIgnore
    private BigDecimal shopPrice;
    /**商品市场价格（多规格内的最大市场价格，用于普通商品的划线价）*/
    @JsonIgnore
    private BigDecimal marketPrice;
    /**商品最大规格价格（用于活动商品的划线价）*/
    @JsonIgnore
    private BigDecimal prdMaxPrice;
    /**商品图片集合*/
    @JsonIgnore
    private List<String> goodsMoreImgs;
    /**最高规格价*/
    @JsonIgnore
    private BigDecimal highPrdPrice;
    /**最低规格价*/
    @JsonIgnore
    private BigDecimal lowPrdPrice;
    /**成本价*/
    @JsonIgnore
    private BigDecimal costPrice;
    /**商品跟活动会员等级计算后的价格*/
    @JsonIgnore
    private BigDecimal showPrice;
    /**
     * 这个商品参加了首单特惠对应的活动的价格
     */
    @JsonIgnore
    private BigDecimal speciallyGoodsPrice;
    //************ElasticSearch中的数据**************end


    /** 商品已被哪些processor处理过（商品列表里面将处理的营销码值存入） */
    @JsonIgnore
    private List<Byte> processedTypes = new ArrayList<>();

    @Override
    public String toString() {
        return "GoodsListMpBo{" +
            "catId=" + catId +
            ", sortId=" + sortId +
            ", brandId=" + brandId +
            ", baseSale=" + baseSale +
            ", shopPrice=" + shopPrice +
            ", marketPrice=" + marketPrice +
            ", prdMaxPrice=" + prdMaxPrice +
            ", processedTypes=" + processedTypes +
            '}'+super.toString();
    }
}
