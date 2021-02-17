package com.meidianyi.shop.service.pojo.wxapp.goods.search;

import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.service.pojo.shop.config.ShowCartConfig;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpVo;
import lombok.Data;

/**
 * 商品搜索页，结果内容
 * @author 李晓冰
 * @date 2020年01月07日
 */
@Data
public class GoodsSearchContentVo {
    /**搜索结果数据，包含分页信息*/
    PageResult<? extends GoodsListMpVo> pageResult;
    /**划线价开关 0关，1显示*/
    Byte delMarket;
    /**购物车按钮显示配置*/
    ShowCartConfig showCart;
}
