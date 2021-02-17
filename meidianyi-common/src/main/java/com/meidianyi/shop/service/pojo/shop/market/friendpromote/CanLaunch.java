package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

/**
 * 是否可以发起好友助力活动
 * @author liangchen
 * @date 2020.02.27
 */
@Data
public class CanLaunch {
    /** 是否可发起 0否 1是 */
    private Byte code;
    /** 0：发起成功 1：活动已停用或删除 2：活动库存不足 3：活动商品库存不足
     * 4：活动未开始 5：活动已结束 6：您已发起快邀请好友助力把 7：数据入库失败 */
    private Integer msg;
}
