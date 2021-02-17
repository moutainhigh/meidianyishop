package com.meidianyi.shop.service.pojo.shop.goods.goods;

import lombok.Data;

/**
 * @author 李晓冰
 * @date 2019年08月21日
 */
@Data
public class GoodsSharePostConfig {
    public static final Byte DEFAULT_ACTION=1;
    public static final Byte DEFAULT_SHARE_IMG_ACTION=1;
    /**
     * 海报样式1默认样式，2自定义样式
     */
    private Byte shareAction = DEFAULT_ACTION;

    /**
     * 文档
     */
    private String shareDoc;

    /**
     * 商品分享图方式1商品主图，2自定义
     */
    private Byte shareImgAction;

    /**
     * 自定义商品分享图地址，出参时为全地址，入参时传入的是相对地址
     */
    private String shareImgUrl;

    /**
     * 自定义商品分享图地址相对地址，出参使用
     */
    private String shareImgPath;
}
