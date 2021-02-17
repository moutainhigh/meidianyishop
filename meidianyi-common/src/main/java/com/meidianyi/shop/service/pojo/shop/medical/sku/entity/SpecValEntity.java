package com.meidianyi.shop.service.pojo.shop.medical.sku.entity;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Data
public class SpecValEntity {
    private Integer specValId;
    private Integer specId;
    private Integer goodsId;
    private String specValName;
}
