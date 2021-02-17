package com.meidianyi.shop.service.pojo.shop.market.coopen;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

import lombok.Getter;
import lombok.Setter;

/**
 *  开屏有礼列表
 * @author 孔德成
 * @date 2019/11/22 14:15
 */
@Getter
@Setter
public class CoopenListParam extends BasePageParam {
    /**
     * 导航状态
     */
    private Byte nvaType;
}
