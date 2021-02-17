package com.meidianyi.shop.service.pojo.wxapp.share;

import lombok.Data;

import java.awt.image.BufferedImage;

/**
 * 商品海报返回信息
 * @author 李晓冰
 * @date 2020年03月02日
 */
@Data
public class GoodsPictorialInfo {
    public static final Byte OK = 0;
    /** 分享操作返回码：0正常，1活动已删除无,2活动商品已删除,3指定的宣传图片读取异常，4二维码读取异常*/
    private Byte pictorialCode = OK;
    /**图片base64*/
    private String base64;

    /**
     * 此变量仅仅是为了测试方面
     */
    private BufferedImage bgImg;
}
