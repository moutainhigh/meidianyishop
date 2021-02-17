package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

/**
 * @Author 常乐
 * @Date 2020-04-14
 */
@Data
public class ShareUserInfoParam {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 分销员ID
     */
    private Integer inviteId;
}
