package com.meidianyi.shop.service.pojo.wxapp.goods.goodssort;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2019年10月18日
 */
@Data
public class GoodsSortMenuParam {
    /** 1:ALL_BRAND_TYPE,2:RECOMMEND_BRAND_TYPE,5:RECOMMEND_SORT_TYPE,6:NORMAL_SORT_TYPE*/
    private Byte menuType;
    /** 当menuType = 6 */
    private Integer menuId;
}
