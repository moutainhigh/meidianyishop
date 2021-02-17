package com.meidianyi.shop.common.pojo.shop.table.goods;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 李晓冰
 * @date 2020年07月08日
 */
@Data
public class GoodsPageListCondition {

    /**
     * null表示不限制，否则表示限制，长度为0的集合表示没有可匹配的id
     */
    private List<Integer> goodsIdsLimit;

    private String goodsName;
    private String goodsSn;
    private List<Integer> brandIds;
    private List<Integer> sortIds;
    /**
     * 在查询规格时该字段表示对规格价格进行过滤
     */
    private BigDecimal lowShopPrice;
    private BigDecimal highShopPrice;
    private Byte isOnSale;
    private Byte isSaleOut;

    /**药品信息*/
    private Byte isMedical;

    private List<GoodsSortItem> pageSortItems;

    private Byte source;
}
