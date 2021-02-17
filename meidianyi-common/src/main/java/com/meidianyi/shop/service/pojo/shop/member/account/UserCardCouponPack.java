package com.meidianyi.shop.service.pojo.shop.member.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 	会员卡优惠券礼包显示信息
 * @author 黄壮壮
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCardCouponPack {
    private Integer id;
    /**
     * 	活动名称
     */
    private String  actName;
    
    /**
     * 	礼包名称
     */
    private String packName;
}
