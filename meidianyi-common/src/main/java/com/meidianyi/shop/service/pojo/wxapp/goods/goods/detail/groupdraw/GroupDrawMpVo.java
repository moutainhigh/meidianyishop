package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.groupdraw;

import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.GoodsActivityDetailMp;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 拼团抽奖活动商品详情页信息
 * @author 李晓冰
 * @date 2020年02月27日
 */
@Getter
@Setter
public class GroupDrawMpVo extends GoodsActivityDetailMp {
    /**抽奖支付金额*/
    private BigDecimal payMoney;
    /**开团人数*/
    private Short limitAmount;
    /**是否显示已参加人数*/
    private Boolean showJoinNumber;
    /**已参加拼团人数*/
    private Integer joinNumber;
}
