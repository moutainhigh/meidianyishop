package com.meidianyi.shop.service.pojo.shop.market.message;

import lombok.Getter;
import lombok.Setter;

/**
 * @author luguangyao
 */
@Getter
@Setter
public class UserInfoVo extends UserInfoByRedis {

    private String username;

    private String mobile;

    private Byte subscribe;
}
