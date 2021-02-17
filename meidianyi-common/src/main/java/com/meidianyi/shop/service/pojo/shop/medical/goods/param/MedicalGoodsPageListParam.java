package com.meidianyi.shop.service.pojo.shop.medical.goods.param;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品分页查询过滤参数信息
 * @author 李晓冰
 * @date 2020年07月07日
 */
@Data
public class MedicalGoodsPageListParam extends BasePageParam {
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

    private String goodsName;
    private String goodsSn;
    private Integer brandId;
    private Integer sortId;
    private Integer labelId;

    /**药品信息*/
    private Byte isMedical;
    private Byte isRx;
    /**
     * 在查询规格时该字段表示对规格价格进行过滤
     */
    private BigDecimal lowShopPrice;
    private BigDecimal highShopPrice;

    private Byte isOnSale;
    private Byte isSaleOut;
    private Byte source;

    /**
     * 排序字段
     */
    private String orderField;
    /**
     * 排序方式
     */
    private String orderDirection;
}
