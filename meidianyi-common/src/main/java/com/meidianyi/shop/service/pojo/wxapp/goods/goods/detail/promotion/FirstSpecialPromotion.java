package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.promotion;

import lombok.Getter;
import lombok.Setter;

/**
 * 首单特惠营销信息类
 * @author 李晓冰
 * @date 2020年01月06日
 */
@Getter
@Setter
public class FirstSpecialPromotion extends PromotionBase {
    /**是否限购 */
    private Boolean isLimit;
    /**限购数量*/
    private Integer limitAmount;
    /**是否进行超购限制*/
    private Boolean limitFlag;
}
