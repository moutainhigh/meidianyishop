package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 商品标签
 * @author 李晓冰
 * @date 2020年07月02日
 */
@Data
public class GoodsLabelDo {
    private Integer   id;
    private String    name;
    private Byte      goodsDetail;
    private Byte      goodsList;
    private Byte      isAll;
    private Short     level;
    private Timestamp delTime;
    private Byte      delFlag;
    private Short     listPattern;
    private Byte      goodsSelect;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Byte      isNone;
    private Byte      isChronic;
}
