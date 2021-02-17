package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

/**
 * @Author 常乐
 * @Date 2020-04-23
 */
@Data
public class UserBindParam {
    /**
     * 分享人ID
     */
    private Integer inviteId;
    /**
     * 用户ID
     */
    private Integer userId;
}
