package com.meidianyi.shop.service.pojo.shop.goods.goodsanalysis;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author chenjie
 * @date 2020年09月17日
 */
@Data
public class GoodsAnalysisListParam {
    private Integer currentPage;
    private Integer pageRows;
    /**
     * 排序字段
     */
    private String orderField;
    /**
     * 排序方式
     */
    private String orderDirection;
    private Timestamp startTime;
    private Timestamp endTime;

    /**
     * 前台传入的控制排序方向
     */
    public static final String ASC = "asc";
    public static final String DESC = "desc";
    /**
     * 待排序字段
     */
    public static final String PV = "pv";
    public static final String UV = "uv";
    public static final String ADD_CART_USER_NUM = "add_cart_user_num";
    public static final String COLLECTION_INCREMENT_NUM = "collection_increment_num";
    public static final String SALE_NUM = "sale_num";
    public static final String SALE_MONEY = "sale_money";
}
