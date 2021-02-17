package com.meidianyi.shop.service.pojo.shop.decoration.module;

/**
 * 
 * @author lixinguo
 *
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ModuleGoods extends ModuleBase {

    @JsonProperty("cart_btn")
    private Byte cartBtn=1;
    @JsonProperty("cart_btn_choose")
    private Byte cartBtnChoose=0;
    @JsonProperty("col_type")
    private Byte colType=4;

    /**
     * 商品范围：all,sort,cat,brand,label
     */
    @JsonProperty("goods_area")
    private String goodsArea="sort";
    /**
     * 范围集合
     */
    @JsonProperty("goods_area_data")
    private List<Integer> goodsAreaData;

    @JsonProperty("goods_bg_color")
    private String goodsBgColor;

    @JsonProperty("goods_display")
    private Byte goodsDisplay=0;

    @JsonProperty("goods_items")
    private List<PhpPointGoodsConverter> goodsItems;

    /**
     * php 数据迁移转化类，为了兼容php数据中的指定商品问题
     */
    public static class PhpPointGoodsConverter{
        private Integer goodsId;

        public Integer getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(Integer goodsId) {
            this.goodsId = goodsId;
        }
    }

    @JsonProperty("goods_module_bg")
    private Byte goodsModuleBg=0;

    @JsonProperty("goods_module_title")
    private Byte goodsModuleTitle=0;

    @JsonProperty("goods_num")
    private Integer goodsNum=4;

    @JsonProperty("goods_module_style")
    private Integer goodsModuleStyle=0;

    /**
     *  商品活动类型默认0，1拼团，2 会员专享，3砍价，5秒杀，6限时降价
     */
    @JsonProperty("goods_type")
    private Byte goodsType=0;

    @JsonProperty("hide_label")
    private Byte hideLabel=0;

    @JsonProperty("hide_name")
    private Byte hideName=1;

    @JsonProperty("hide_price")
    private Byte hidePrice=0;

    @JsonProperty("if_radius")
    private Byte ifRadius=0;

    @JsonProperty("img_title_url")
    private String imgTitleUrl = "";

    @JsonProperty("img_url")
    private String imgUrl="";

    @JsonProperty("is_more")
    private Byte isMore;

    /**
     * 关键词,匹配商品名称
     */
    private String keywords;

    /**
     * 商品最高价格
     */
    @JsonProperty("max_price")
    private BigDecimal maxPrice;
    /**
     * 商品最低价格
     */
    @JsonProperty("min_price")
    private BigDecimal minPrice;

    /**
     * 1展示其他信息
     */
    @JsonProperty("other_message")
    private Byte otherMessage=0;
    /**
     * 0 自动推荐 1手动推荐
     */
    @JsonProperty("recommend_type")
    private Byte recommendType=0;

    /**
     * 0 普通自动推荐（店铺药品） 1处方药
     */
    @JsonProperty("auto_recommend_type")
    private Byte autoRecommendType=0;

    @JsonProperty("show_market")
    private Byte showMarket=1;

    @JsonProperty("sort_type")
    private Byte sortType=1;

    private String title="";

    @JsonProperty("title_link")
    private String titleLink="";

    @JsonProperty("tit_center")
    private Byte titCenter=0;

    private List<?> goodsListData;

    private Boolean hasMore = false;
}
