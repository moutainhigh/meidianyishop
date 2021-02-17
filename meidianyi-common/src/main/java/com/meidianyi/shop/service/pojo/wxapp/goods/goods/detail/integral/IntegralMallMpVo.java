package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.integral;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsActivityDetailMp;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 积分兑换商品详情
 * 详情页面待展示的积分，所有规格中的最低积分，真实加就是该规格的价格，划线价则是该规格的原价
 * @author 李晓冰
 * @date 2020年03月03日
 */
@Setter
@Getter
public class IntegralMallMpVo extends GoodsActivityDetailMp {
    /**当前用户拥有的积分*/
    private Integer userScore;
    /**已兑换的数量*/
    private Integer redeemNum;
    /**当前用户最大可兑换数量*/
    private Integer maxExchangeNum;
    /**活动总库存*/
    private Integer stock;
    /**
     * 积分兑换活动商品规格信息
     */
    private List<IntegralMallPrdMpVo> integralMallPrdMpVos;
}
