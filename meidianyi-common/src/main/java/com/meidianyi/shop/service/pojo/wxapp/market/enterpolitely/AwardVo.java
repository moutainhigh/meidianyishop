package com.meidianyi.shop.service.pojo.wxapp.market.enterpolitely;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * The type Award vo.
 *
 * @author liufei
 * @date 12 /25/19
 */
@Data
@Builder
public class AwardVo {
    /**
     * The Award type.活动类型：优惠券 大转盘抽奖 跳转自定义链接  积分  余额  分裂优惠券
     */
    public Byte awardType;
    /**
     * The Activity id.
     */
    public Integer activityId;
    /**
     * The Award content.奖励内容：大转盘抽奖（lottery_id），跳转自定义链接（customize_img_path，customize_url），积分（score），余额（balance），优惠券（title，bg_img，coupon_list）
     */
    public String awardContent;
    /**
     * The Ext content.跳转自定义链接（customize_img_path），优惠券（title，bg_img）
     */
    public Map<String, String> extContent;
}
