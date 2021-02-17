package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 李晓冰
 * @date 2020年07月07日
 */
@Data
public class SortDo {
    private Integer   sortId;
    private String    sortName;
    private Integer   parentId;
    private Short     level;
    private Byte      hasChild;
    private String    sortImg;
    private String    imgLink;
    private Short     first;
    private Byte      type;
    private String    sortDesc;
    private Timestamp createTime;
    private Timestamp updateTime;
}
