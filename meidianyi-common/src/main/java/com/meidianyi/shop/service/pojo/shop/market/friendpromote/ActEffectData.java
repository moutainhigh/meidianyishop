package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

/**
 * 助力活动效果数据
 * @author liangchen
 * @date 2020.03.20
 */
@Data
public class ActEffectData {
    /** 时间 */
    private String date;
    /** 发起用户数 */
    private Integer launch;
    /** 帮助用户数 */
    private Integer promote;
    /** 成功用户数 */
    private Integer success;
    /** 拉新用户数 */
    private Integer newUser;
}
