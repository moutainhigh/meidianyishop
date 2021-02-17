package com.meidianyi.shop.service.pojo.shop.goods.brand;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 映射分页查询条件实体类
 * @author 李晓冰
 * @date 2019年07月02日
 */
@Data
public class GoodsBrandPageListParam {
    private String brandName;
    private Timestamp startAddTime;
    private Timestamp endAddTime;
    private Integer classifyId ;
    private Byte isRecommend ;

    /**
     * 	分页信息
     */
    private int currentPage;
    private int pageRows;
}
