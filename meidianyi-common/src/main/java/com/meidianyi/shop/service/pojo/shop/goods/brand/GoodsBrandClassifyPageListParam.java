package com.meidianyi.shop.service.pojo.shop.goods.brand;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 李晓冰
 * @date 2019年11月13日
 */
@Data
public class GoodsBrandClassifyPageListParam {
    private String classifyName;
    private Timestamp startAddTime;
    private Timestamp endAddTime;

    private Integer currentPage;
    private Integer pageRows;
}
