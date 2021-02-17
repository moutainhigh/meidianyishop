package com.meidianyi.shop.service.pojo.shop.medical.goods.vo;

import lombok.Data;

/**
 * @author chenjie
 * @date 2020年09月18日
 */
@Data
public class GoodsStatusVo {
    private Integer goodsId;
    private Byte isOnSale;
    private Byte delFlag;
}
