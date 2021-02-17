package com.meidianyi.shop.service.pojo.shop.market.channel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月15日
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeShareVo {
    /** 图片地址 **/
    private String imageUrl;
    /** 显示在页面上的链接地址 **/
    private String pagePath;
}

