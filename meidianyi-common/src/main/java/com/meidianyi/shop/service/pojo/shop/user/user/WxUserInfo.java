package com.meidianyi.shop.service.pojo.shop.user.user;

import lombok.Builder;
import lombok.Getter;


/**
 * 微信发送用户的相关信息
 * @author 卢光耀
 * @date 2019-08-27 11:56
 *
*/
@Getter
@Builder
public class WxUserInfo {

    private Integer userId;
    private String maAppId;
    private String maOpenId;

    @Builder.Default
    private Boolean isSubscribe = Boolean.FALSE;

    private String mpAppId;
    private String mpOpenId;

}
