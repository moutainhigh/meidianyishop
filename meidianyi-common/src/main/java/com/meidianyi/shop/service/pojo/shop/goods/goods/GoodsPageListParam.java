package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 李晓冰
 * @date 2019年07月09日
 */
@Data
public class GoodsPageListParam {

    /**
     * 前台传入的控制排序方向
     */
    public static final String ASC="asc";
    public static final String DESC="desc";
    /**
     * 	待排序字段
     */
    public static final String SHOP_PRICE="shopPrice";
    public static final String GOODS_NUMBER="goodsNumber";
    public static final String GOODS_SALE_NUM="goodsSaleNum";

    /**
     * 分别为查询：1商品分页列表信息，2规格分页列表信息，3商品分页信息（每一条商品数据附带了对应的所有规格数据）
     */
    public static final Byte GOODS_LIST=1;
    public static final Byte GOODS_PRD_LIST=2;
    public static final Byte GOODS_LIST_WITH_PRD=3;

    private Integer goodsId;
    private List<Integer> goodsIds;

    /**
     * 	不能包含的goodsId
     */
    private List<Integer> notIncludeGoodsIds;
    private String goodsName;
    private String goodsSn;
    private Integer brandId;
    /**
     * 商品来源,0：店铺自带；1、2..等：不同类型店铺第三方抓取自带商品来源
     */
    private Byte source;
    /**
     * 商品类型，0普通商品，1拼团商品，2分销，3砍价商品 4积分商品 5秒杀商品.
     * 在活动中动态设置商品所处于的类型状态，目前仅有以上几种
     */
    private Byte goodsType;
    private Integer catId;
    private Integer sortId;
    private Integer labelId;
    private Timestamp saleTimeStart;
    private Timestamp saleTimeEnd;
    /**
     * 在查询规格时该字段表示对规格价格进行过滤
     */
    private BigDecimal lowShopPrice;
    private BigDecimal highShopPrice;
    /**
     * 查询类型分类：1商品分页列表信息，2规格分页列表信息，3商品分页信息（每一条商品数据附带了对应的所有规格数据）,默认为1
     */
    private Byte selectType;
    /**
     * 是否在售 1 在售（上架），0下架（仓库中）
     * 商品数量为0时属于售罄状态，但是依然可以为（上架状态）
     */
    private Byte isOnSale;
    /**
     * 0表示查询已售罄
     * 1表示未售罄
     */
    private Byte isSaleOut;

    /**
     * 排序字段
     */
    private String orderField;
    /**
     * 排序方式
     */
    private String orderDirection;

    /**
     * 是不是聚合查询
     */
    private Boolean isFactQuery = Boolean.FALSE;
    /**
     * 需要聚合查询的字段
     */
    private List<String> factNameList;
    /**
     * 是否开启分词配置
     */
    private Boolean openedAnalyzer = Boolean.FALSE;


    /**
     * 	分页信息
     */
    private Integer currentPage;
    private Integer pageRows;
}
