package com.meidianyi.shop.common.pojo.shop.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 规格值
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecValDo {
    private Integer   specValId;
    private Integer   specId;
    private Integer   goodsId;
    private String    specValName;
    private Byte      delFlag;
    private Timestamp createTime;
    private Timestamp updateTime;

    public SpecValDo(Integer specId, Integer goodsId, String specValName) {
        this.specId = specId;
        this.goodsId = goodsId;
        this.specValName = specValName;
    }
}
