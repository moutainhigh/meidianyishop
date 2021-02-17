package com.meidianyi.shop.service.pojo.shop.market.groupbuy.param;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

/**
 * @author 孔德成
 * @date 2019/7/22 14:16
 */

@NoArgsConstructor
@Setter
@Getter
public class GroupBuyListParam  extends BasePageParam {

    /**
     *  0全部拼团活动 1进行中 2未开始 3已过期 4已停用
     */
    @NotNull
    private Byte type;


}
