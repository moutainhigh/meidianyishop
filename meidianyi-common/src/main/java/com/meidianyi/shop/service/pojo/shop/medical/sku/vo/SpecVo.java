package com.meidianyi.shop.service.pojo.shop.medical.sku.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 李晓冰
 * @date 2020年07月07日
 */
@Data
public class SpecVo {
    private Integer  specId;
    private Integer goodsId;
    private String specName;

    /**
     * 规格值
     */
    List<SpecValVo> goodsSpecVals;
}
