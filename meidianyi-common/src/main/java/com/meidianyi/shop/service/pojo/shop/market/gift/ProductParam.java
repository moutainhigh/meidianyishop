package com.meidianyi.shop.service.pojo.shop.market.gift;

import lombok.Data;

/**
 * 赠品规格
 *
 * @author 郑保乐
 */
@Data
public class ProductParam {

    /** 活动id **/
    private Integer giftId;
    /** 规格id **/
    private Integer productId;
    /** 赠品库存 **/
    private Integer productNumber;
}
