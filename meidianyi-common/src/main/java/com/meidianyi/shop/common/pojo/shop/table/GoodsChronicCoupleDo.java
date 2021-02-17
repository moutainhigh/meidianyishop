package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 商品标签
 * @author chenjie
 * @date 2020年09月25日
 */
@Data
public class GoodsChronicCoupleDo {
    private Integer id;
    private String  goodsCommonName;
    private String  goodsForm;
    private String  labelName;
}
