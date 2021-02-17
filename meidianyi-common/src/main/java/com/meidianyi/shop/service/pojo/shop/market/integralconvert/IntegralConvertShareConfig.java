package com.meidianyi.shop.service.pojo.shop.market.integralconvert;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * 添加积分活动
 * @author liangchen
 * @date 2019年8月15日
 */
@Data
public class IntegralConvertShareConfig {
	
	/** 样式  1：默认样式   2：自定义样式 */
	private Byte shareAction = 1;
	
	/** 自定义样式文案 */
	private String shareDoc;
	
	/** 是否默认活动商品信息图  1:默认图片  2：自定义图片 */
	private Byte shareImgAction = 1;
	
	/** 自定义图片地址 */
	private String shareImg;

}
