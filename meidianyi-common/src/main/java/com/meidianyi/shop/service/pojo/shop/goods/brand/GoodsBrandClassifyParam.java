package com.meidianyi.shop.service.pojo.shop.goods.brand;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 李晓冰
 * @date 2019年07月26日
 */
@Data
public class GoodsBrandClassifyParam {
    private Integer classifyId;
    private String classifyName;
    private Short first;
    private Timestamp startCreateTime;
    private Timestamp endCreateTime;

    private int currentPage;
    private int pageRows;
}
