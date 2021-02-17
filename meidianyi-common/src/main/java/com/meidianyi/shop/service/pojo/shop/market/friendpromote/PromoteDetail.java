package com.meidianyi.shop.service.pojo.shop.market.friendpromote;

import lombok.Data;

/**
 * 好友助力明细
 * @author liangchen
 * @date 2020.02.27
 */
@Data
public class PromoteDetail {
    /** 助力值 */
    private Integer promoteValue;
    /** 用户名 */
    private String username;
    /** 用户头像 */
    private String userAvatar;
    /** 用户id */
    private Integer userId;
}
