package com.meidianyi.shop.service.pojo.wxapp.goods.goods.activity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsDetailMpVo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 小程序详情页面信息存储对象
 * @author 李晓冰
 * @date 2019年11月07日
 */
@Getter
@Setter
public class GoodsDetailMpBo extends GoodsDetailMpVo{
    //************ElasticSearch中的数据**************start
    /**商品初始销量*/
    @JsonIgnore
    private Integer baseSale;

    /**是否分销改价*/
    @JsonIgnore
    private Byte canRebate;

    @JsonIgnore
    private Integer goodsVideoId;
    @JsonIgnore
    private List<GradePrd> gradeCardPrice = new ArrayList<>();
    /**直播间id*/
    @JsonIgnore
    private Integer roomId;
    //************ElasticSearch中的数据**************end

    @Data
    public static class GradePrd{
        private Integer prdId;
        private BigDecimal gradePrice;
        private Boolean isGradePrice;
    }

    /** 商品已被哪些processor处理过（商品列表里面将处理的营销码值存入） */
    @JsonIgnore
    private Set<Byte> processedTypes = new HashSet<>();

    /**商品主图*/
    @JsonIgnore
    private String goodsImg;

    @Override
    public String toString() {
        return "GoodsDetailMpBo{" +
            ", baseSale=" + baseSale +
            ", canRebate=" + canRebate +
            ", goodsVideoId=" + goodsVideoId +
            ", gradeCardPrice=" + gradeCardPrice +
            ", processedTypes=" + processedTypes +
            ", goodsImg='" + goodsImg + '\'' +
            '}'+super.toString();
    }
    
    /**是否显示好物圈的按钮 */
    private Boolean showMall;
}
