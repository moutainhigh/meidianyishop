package com.meidianyi.shop.service.pojo.shop.distribution;

import com.meidianyi.shop.service.pojo.wxapp.goods.search.SortDirectionEnum;
import com.meidianyi.shop.service.pojo.wxapp.goods.search.SortItemEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author changle
 * @date 2020/6/29 4:06 下午
 */
@Data
public class PromoteGoodsParam {
    /**
     * 分销员ID
     */
    private Integer userId;
    /**
     * 搜索内容
     */
    private String search;
    /**
     * 当前页数
     */
    private Integer currentPage;
    /**
     * 每页展示条数
     */
    private Integer pageRows;
    /**
     * 商品最低价
     */
    private BigDecimal minPrice;
    /**
     * 商品最高价格
     */
    private BigDecimal maxPrice;
    /**
     * 商家分类id集合
     */
    private List<Integer> sortIds;
    /**
     * 品牌id集合
     */
    private List<Integer> brandIds;
    /**
     * 活动类型集合
     */
    private List<Integer> activityTypes;
    /**
     * 用户指定的排序字段
     */
    private SortItemEnum sortItem;
    /**
     * 用户指定的排序方向
     */
    private SortDirectionEnum sortDirection;
    /**
     * 店铺默认的排序字段-es使用字段
     */
    private SortItemEnum shopSortItem;
    /**
     * 店铺默认的排序方向-es使用字段
     */
    private SortDirectionEnum shopSortDirection;
    /**
     * 是否展示售罄商品 true展示 false否
     */
    private Boolean soldOutGoodsShow;
    /**
     * 是否是个人推广中心
     */
    private Byte isPersonal;
}
