package com.meidianyi.shop.service.pojo.shop.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 商品、活动海报分享配置出参类
 * @author 李晓冰
 * @date 2019年12月27日
 */
@Getter
@Setter
public class PictorialShareConfigVo extends PictorialShareConfig {
    /** 分享显示的图片绝对地址*/
    private String shareImgFullUrl;
}
