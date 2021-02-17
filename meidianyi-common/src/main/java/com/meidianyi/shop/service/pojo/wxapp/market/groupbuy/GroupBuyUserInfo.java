package com.meidianyi.shop.service.pojo.wxapp.market.groupbuy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户
 * @author 孔德成
 * @date 2019/12/11 11:35
 */
@Getter
@Setter
public class GroupBuyUserInfo {

    private Integer userId;
    /**
     * 用户名称
     */
    @JsonIgnore
    private String username;
    /**
     * 订单
     */
    @JsonIgnore
    private String orderSn;
    /**
     * 头像
     */
    private String userAvatar;
    /**
     * 拼团状态
     */
    private Byte status;
}
