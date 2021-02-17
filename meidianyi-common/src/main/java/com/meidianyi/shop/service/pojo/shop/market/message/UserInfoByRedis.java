package com.meidianyi.shop.service.pojo.shop.market.message;


import lombok.Getter;
import lombok.Setter;

/**
 * redis 存储用户信息相关序列化
 * @author 卢光耀
 * @date 2019-09-11 11:58
 *
*/
@Getter
@Setter
public class UserInfoByRedis {
    private Integer userId;

    private Integer numbers = 0;

    private Boolean canSend = Boolean.FALSE;

    private Boolean isChecked = Boolean.FALSE;

    private Boolean isVisitMp = Boolean.FALSE;


}
