package com.meidianyi.shop.service.pojo.wxapp.share.integral;

import com.meidianyi.shop.service.pojo.wxapp.share.GoodsShareBaseParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 积分兑换活动入参
 * @author 李晓冰
 * @date 2020年05月15日
 */
@Getter
@Setter
public class IntegralMallShareInfoParam extends GoodsShareBaseParam {
    /**使用积分*/
    private Integer score;
}
