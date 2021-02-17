package com.meidianyi.shop.common.pojo.shop.table.goods;

import lombok.Data;

/**
 * 排序字段
 * @author 李晓冰
 * @date 2020年07月08日
 */
@Data
public class GoodsSortItem {
    private String columnName;
    private boolean isAsc;
}
