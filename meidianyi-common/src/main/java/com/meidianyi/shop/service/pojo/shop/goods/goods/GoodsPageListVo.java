package com.meidianyi.shop.service.pojo.shop.goods.goods;

import com.meidianyi.shop.service.pojo.shop.goods.label.GoodsLabelSelectListVo;
import com.meidianyi.shop.service.pojo.shop.goods.spec.GoodsSpecProduct;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

/**
 * 商品（规格）分页信息返回类，该类同时使用于
 * 1商品分页列表信息，2规格分页列表信息，3商品分页信息（每一条商品数据附带了对应的所有规格数据）
 * @author 李晓冰
 * @date 2019年07月09日
 */
@Data
public class  GoodsPageListVo {
    private Integer goodsId;
    private String goodsName;
    private String goodsTitleName;
    private String goodsImg;
    private String goodsSn;
    private BigDecimal  marketPrice;
    private BigDecimal shopPrice;
    private Byte source;
    private Byte goodsType;
    private Integer catId;
    private String catName;
    private String sortName;
    private Integer sortId;
    private Integer brandId;
    private String brandName;
    private Integer goodsNumber;
    private Integer goodsSaleNum;
    private Byte isCardExclusive;
    /** 指定标签集合（在商品列表只展示可修改的标签）*/
    private List<GoodsLabelSelectListVo> goodsPointLabels = new ArrayList<>(3);
    /** 普通标签集合（在商品列表只展示不可修改的标签）使用set避免商家分类上的标签和全部商品类型标签存在重复*/
    private Set<GoodsLabelSelectListVo> goodsNormalLabels = new HashSet<>(3);
    /**
     * 商品对应的规格数据,未使用
     */
    private List<GoodsSpecProduct> goodsSpecProducts;
    private Boolean isDefaultPrd;
    /**
     * 查询商品对应的规格时该值为规格id,或者表示默认规格的id，否则为空
     */
    private Integer prdId;
    private String prdDesc;
    private String prdImg;
    private String prdSn;
    private BigDecimal prdPrice;
    private Integer prdNumber;
    /**
     * 商品规格类型数量（默认规格为类型数量为0）
     */
    private Integer prdTypeNum;
    /**
     * 商品规格中价格最高的价格，只有一个规格数据或默认规格时其最高价格和最低价格一样
     */
    private BigDecimal prdMaxShopPrice;
    /**
     * 商品规格中价格最低的价格
     */
    private BigDecimal prdMinShopPrice;
}
