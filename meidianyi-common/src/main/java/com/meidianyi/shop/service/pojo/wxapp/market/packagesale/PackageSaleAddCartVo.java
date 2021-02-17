package com.meidianyi.shop.service.pojo.wxapp.market.packagesale;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: 王兵兵
 * @create: 2020-04-14 14:57
 **/
@Setter
@Getter
public class PackageSaleAddCartVo {
    /**
     * 状态标志：
     * 0加购成功；1活动已删除；2活动已停用；3活动已过期；
     * 4活动未开始；5活动规则已发生变化，请重新选择;6该分组已选满；
     * 7商品不存在；8商品已下架；9商品库存不足
     */
    private Byte state = 0;

    /**
     * state == 6时的分组名称
     */
    private String groupName;
}
