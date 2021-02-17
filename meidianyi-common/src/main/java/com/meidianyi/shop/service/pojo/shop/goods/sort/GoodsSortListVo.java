package com.meidianyi.shop.service.pojo.shop.goods.sort;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author 李晓冰
 * @date 2019年11月20日
 */
@Getter
@Setter
public class GoodsSortListVo extends GoodsSort{
    private Byte hasChild;
    private Timestamp createTime;
}
