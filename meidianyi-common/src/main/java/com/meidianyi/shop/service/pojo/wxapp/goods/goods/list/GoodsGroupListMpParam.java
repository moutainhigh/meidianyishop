package com.meidianyi.shop.service.pojo.wxapp.goods.goods.list;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品分组-查询参数
 *
 * @author 李晓冰
 * @date 2020年01月15日
 */
@Data
public class GoodsGroupListMpParam {
    /** 展示部分商品 */
    public static final Byte SECTION_POINT_GOODS = 2;
    /**分组是商家分类*/
    public static final String SORT_TYPE = "";
    /**分组是标签*/
    public static final String LABEL_TYPE = "1";
    /**分组是品牌*/
    public static final String BRAND_TYPE = "2";
    /**商品可以显示的最大数量*/
    public static final Integer NUM_TO_SHOW = 7;


    /**菜单位置radio，0 顶部固定，1左侧固定*/
    @JsonProperty("position_style")
    private Byte positionStyle;

    /**是否展示"全部商品"栏位 1展示 0不展示*/
    @JsonProperty("group_display")
    private Byte groupDisplay;

    /**筛选条件*/
    @JsonProperty("sort_group_arr")
    private List<SortGroup> sortGroupArr;

    /**用户id controller层设置*/
    private Integer userId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SortGroup{
        @JsonProperty("sort_id")
        private Integer sortId;
        /**选择的分组筛选条件类型,为了兼容php数据类型，空字符串:商家分类，"1":商品标签，"2":商品品牌*/
        @JsonProperty("sort_type")
        private String sortType;
        /**分组指定的商品id字符串集合，逗号分隔*/
        @JsonProperty("group_goods_id")
        private String groupGoodsId;
        /**是展示全部商品：1，还是指定商品：2*/
        @JsonProperty("is_all")
        private Byte isAll;
    }
}
