package com.meidianyi.shop.service.pojo.wxapp.market.groupbuy;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * 拼团详情
 * @author 孔德成
 * @date 2019/12/10 14:59
 */
@Getter
@Setter
public  class GroupBuyInfoParam {

    /**
     * 拼团团id
     */
    @NotNull
    private Integer groupId;
    /**
     * 用户id
     */
    private Integer userId;
}
