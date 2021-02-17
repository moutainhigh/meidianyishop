package com.meidianyi.shop.service.pojo.wxapp.order.base;

import com.meidianyi.shop.service.pojo.wxapp.login.WxAppSessionUser;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 基础param
 * @author 王帅
 *
 */
@Getter
@Setter
@ToString
public class BaseParam {
	private WxAppSessionUser wxUserInfo;
}
