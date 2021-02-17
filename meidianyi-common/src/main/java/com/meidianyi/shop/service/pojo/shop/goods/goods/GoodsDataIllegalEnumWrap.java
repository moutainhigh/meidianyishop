package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Data;

/**
 * 商品插入修改操作结果信息类
 *
 * @author 李晓冰
 * @date 2020年03月24日
 */
@Data
public class GoodsDataIllegalEnumWrap {
    /**
     * 被操作的商品id
     */
    private Integer goodsId;
    /**
     * 操作结果状态
     */
    private GoodsDataIIllegalEnum illegalEnum = GoodsDataIIllegalEnum.GOODS_OK;
}
