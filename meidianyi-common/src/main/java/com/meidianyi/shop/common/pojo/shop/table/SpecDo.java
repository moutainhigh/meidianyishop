package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 规格名
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Data
public class SpecDo {
    private Integer   specId;
    private String    specName;
    private Byte      delFlag;
    private Integer   goodsId;
    private Timestamp createTime;
    private Timestamp updateTime;
}
