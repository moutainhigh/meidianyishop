package com.meidianyi.shop.service.pojo.shop.image;

import lombok.Data;

/**
 * @author 郑保乐
 */
@Data
public class ShareQrCodeVo {

    /** 图片地址 **/
    private String imageUrl;
    /** 显示在页面上的链接地址 **/
    private String pagePath;
}
