package com.meidianyi.shop.service.pojo.shop.market.sharereward;

import lombok.Builder;
import lombok.Data;

/**
 * @author liufei
 * @date 1/14/20
 */
@Data
@Builder
public class GoodsShareDetail {
    private Integer dailyShareLimit;
    private ShareRewardInfoVo infoVo;
}
