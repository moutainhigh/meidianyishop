package com.meidianyi.shop.service.pojo.wxapp.goods.goodssort;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2019年10月18日
 * 小程序商品分类页面左侧目录类
 */
@Data
public class GoodsSortMenuVo {

    /**目录条目类型：1全部品牌，2推荐品牌，
     * 5推荐分类，6普通分类*/
    private Byte menuType;
    private Integer menuId;
    private String menuName;
    /**该目录下的内容项*/
    private MenuContentBaseVo menuContent;
}
