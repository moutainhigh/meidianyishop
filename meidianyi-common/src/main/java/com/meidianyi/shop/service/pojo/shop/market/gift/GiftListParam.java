package com.meidianyi.shop.service.pojo.shop.market.gift;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

import lombok.Getter;
import lombok.Setter;

/**
 * 定金膨胀列表查询入参
 *
 * @author 郑保乐
 */
@Getter
@Setter
public class GiftListParam extends BasePageParam {

    /** 活动名称 **/
    private String name;
    /** 状态 **/
    private Byte status;
}
