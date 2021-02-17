package com.meidianyi.shop.service.pojo.shop.goods.brand;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 李晓冰
 * @date 2019年11月13日
 */
@Data
public class GoodsBrandClassifyPageListVo {
    private Integer classifyId;
    private String classifyName;
    private Short first;
    private Integer brandNum;
    private Timestamp createTime;
}
