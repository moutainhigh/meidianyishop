package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.gift;

import lombok.Data;

import java.util.List;

/**
 * 商品详情-赠品信息
 * @author 李晓冰
 * @date 2020年01月14日
 */
@Data
public class GoodsGiftMpVo {
    /**赠品活动id*/
    private Integer id;
    /**是否需要凑单获取*/
    private Boolean isFullPrice;
    /**赠品信息说明*/
    private String explain;
    /**赠品活动对应的赠品项信息*/
    private List<GoodsGiftPrdMpVo> goodsGiftPrdMpVos;
}
