package com.meidianyi.shop.service.pojo.shop.decoration.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 
 * @author lixinguo
 *
 */
@Getter
@Setter
public class ModuleGoodsGroup extends ModuleBase {

    /**
     * 分组内容
     */
    @JsonProperty("sort_group_arr")
    private List<SortGroup> sortGroupArr;

    /**
     *菜单样式radio，0顶部展示商品分组，1左侧展示商品分组
     */
    @JsonProperty("menu_style")
    private Byte menuStyle=0;

    /**
     *菜单位置radio，0 顶部固定，1左侧固定
     */
    @JsonProperty("position_style")
    private Byte positionStyle=0;

    /**
     *商品列表样式radio，0大图展示，1一行两个，2一行三个，3商品列表，4一行横滑
     */
    @JsonProperty("shop_style")
    private Byte shopStyle=1;

    /**
     *模块角度radio，0直角，1圆角
     */
    @JsonProperty("if_radius")
    private Byte ifRadius=0;

    /**
     *
     */
    @JsonProperty("sort_length")
    private Integer sortLength=1;

    /**
     *模块样式radio，1白底无边框，2边框投影，3白底有边框
     */
    @JsonProperty("module_style")
    private Byte moduleStyle=1;

    /**
     * 是否展示"全部商品"栏位 1展示 0不展示
     */
    @JsonProperty("group_display")
    private Byte groupDisplay=1;

    /**
     *其它信息按钮下隐藏模块radio，1市场价，2销量，3评价数
     */
    @JsonProperty("show_market")
    private Byte showMarket=1;

    /**
     *
     */
    @JsonProperty("goods_module_bg")
    private Byte goodsModuleBg=0;

    /**
     *
     */
    @JsonProperty("goods_bg_color")
    private String goodsBgColor="#f5f5f5";

    /**
     * 显示商品名称
     */
    @JsonProperty("show_name")
    private Byte showName=1;

    /**
     * 显示商品价格
     */
    @JsonProperty("show_price")
    private Byte showPrice=1;

    /**
     *显示购买按钮
     */
    @JsonProperty("cart_btn")
    private Byte cartBtn=1;

    /**
     * 按钮形状
     */
    @JsonProperty("cart_btn_choose")
    private Byte cartBtnChoose=0;

    /**
     * 是否展示其它信息0展示 1不展示
     */
    @JsonProperty("other_message")
    private Byte otherMessage=0;

    private List<?> goodsListData;

    @Getter
    @Setter
    public static class SortGroup{
        /**选择分组筛查条件名*/
        @JsonProperty("sort_name")
        private String sortName;
        /**设置的分组名称*/
        @JsonProperty("group_name")
        private String groupName;
        /**选择的分组筛选条件id*/
        @JsonProperty("sort_id")
        private Integer sortId;
        /**选择的分组筛选条件类型,为了兼容php数据类型，空字符串:商家分类，"1":商品标签，"2":商品品牌*/
        @JsonProperty("sort_type")
        private String sortType;
        /**分组指定的商品id字符串集合，逗号分隔*/
        @JsonProperty("group_goods_id")
        private String groupGoodsId;
        /**分组指定的商品的数量*/
        @JsonProperty("group_goods_num")
        private Integer groupGoodsNum;
        /**是展示全部商品：1，还是指定商品：2*/
        @JsonProperty("is_all")
        private Byte isAll;
        /**展示全部商品时的商品数量*/
        @JsonProperty("sort_goods_num")
        private Integer sortGoodsNum;
    }
}
