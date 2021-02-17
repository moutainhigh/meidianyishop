package com.meidianyi.shop.service.pojo.shop.image.share;

import lombok.Data;

/**
 * 活动分享配置
 *
 * @author 郑保乐
 */
@Data
public class ShareConfig {

    private Byte shareAction;
    private String shareDoc;
    private Byte shareImgAction;
    private String shareImg;
}
