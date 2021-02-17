package com.meidianyi.shop.service.pojo.wxapp.market.groupbuy;

import lombok.Getter;
import lombok.Setter;

/**
 * 参团信息
 * @author 孔德成
 * @date 2019/12/27 16:55
 */
@Getter
@Setter
public class JoinGroupListInfo {

    private Integer userId;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 订单
     */
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
