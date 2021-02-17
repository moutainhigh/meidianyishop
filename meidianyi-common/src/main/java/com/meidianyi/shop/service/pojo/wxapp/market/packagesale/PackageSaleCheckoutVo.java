package com.meidianyi.shop.service.pojo.wxapp.market.packagesale;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: 王兵兵
 * @create: 2020-04-14 16:31
 **/
@Getter
@Setter
public class PackageSaleCheckoutVo {

    /**
     *状态标志：
     * 0成功；1活动已删除；2活动已停用；3活动已过期；
     * 4该分组商品选择数量不足；5该分组商品选择数量过多
     */
    private Byte state;

    /**
     * state == 4或5时的分组名称
     */
    private String groupName;

    private List<CheckoutGoods> goods;

    @Setter
    @Getter
    public static class CheckoutGoods{
        private Integer goodsId;
        private Integer productId;
        private Integer goodsNumber;
    }

}
