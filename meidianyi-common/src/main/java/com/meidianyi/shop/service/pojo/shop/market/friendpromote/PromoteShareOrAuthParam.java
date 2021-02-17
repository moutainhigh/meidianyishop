package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

/**
 * 小程序好友助力入参
 * @author liangchen
 * @date 2020.02.26
 */
@Data
public class PromoteShareOrAuthParam {
    /** 用户id */
    private Integer userId;
    /** 助力活动码 */
    private String actCode;
    /** 活动发起id */
    private Integer launchId;
    /** 0分享 1授权*/
    private Integer type;
}
