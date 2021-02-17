package com.meidianyi.shop.service.pojo.shop.store.store;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author 赵晓东
 * @description
 * @create 2020-09-14 15:25
 **/
@Data
public class StoreBestSellersParam {
    /** 日期类型 1:最近1天 7:最近7天 30:最近30天 0:自定义 */
    public Integer type = 30;
    public String startDate;
    public String endDate;
    /**
     * 前台传入的控制排序方向
     */
    public static final String ASC = "asc";
    public static final String DESC = "desc";
    /**
     * 待排序字段
     */
    private static final String TOTAL_PRICE = "totalPrice";
    private static final String GOODS_NUMBER = "goodsNumber";

    private List<Integer> storeIds;
    /**
     * 排序字段
     */
    private String orderField;
    /**
     * 排序方式
     */
    private String orderDirection;

    private Integer currentPage;
    private Integer pageRows;
}
