package com.meidianyi.shop.service.pojo.wxapp.share;

import lombok.Data;

import java.awt.image.BufferedImage;

/**
 * 海报中的用户信息
 * @author 李晓冰
 * @date 2019年12月31日
 */
@Data
public class PictorialUserInfo {
    /**名称*/
    private String userName;
    /**用户头像*/
    private BufferedImage userAvatarImage;
}
