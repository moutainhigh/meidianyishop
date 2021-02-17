package com.meidianyi.shop.service.pojo.wxapp.market.prize;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;

/**
 * 我的奖品
 *
 * @author 孔德成
 * @date 2020/1/3 9:39
 */
@Getter
@Setter
public class PrizeRecordParam extends BasePageParam {
    private Byte status=0;
}
