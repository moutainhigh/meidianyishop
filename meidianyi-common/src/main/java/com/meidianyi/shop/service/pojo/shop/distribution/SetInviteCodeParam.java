package com.meidianyi.shop.service.pojo.shop.distribution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author 常乐
 * @Date 2020-02-13
 */
@Data
public class SetInviteCodeParam {
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 邀请码
     */

    private String inviteCode;
}
