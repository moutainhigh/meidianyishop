package com.meidianyi.shop.service.pojo.shop.market.groupbuy.param;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

/**
 * @author 孔德成
 * @date 2019/7/23 13:40
 */
@Setter
@Getter
public class GroupBuyDetailParam extends BasePageParam {

    @NotNull
    private Integer activityId;


    private String mobile;

    private String nickName;

    /**
     * 拼团中 0 拼团成功 1 拼团失败 2
     */
    private Byte status;


}
