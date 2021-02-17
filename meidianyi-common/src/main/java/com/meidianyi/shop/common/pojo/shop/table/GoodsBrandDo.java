package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 品牌do
 * @author 李晓冰
 * @date 2020年07月07日
 */
@Data
public class GoodsBrandDo {
    private Integer   id;
    private String    brandName;
    private String    eName;
    private String    logo;
    private Byte      first;
    private Byte      delFlag;
    private String    desc;
    private Byte      isRecommend;
    private Integer   classifyId;
    private Timestamp createTime;
    private Timestamp updateTime;
}
