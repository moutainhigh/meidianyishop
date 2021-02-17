package com.meidianyi.shop.service.pojo.wxapp.share;

import lombok.Data;

/**
 * @author liufei
 * @date 3/5/2020
 * 表单分享-海报背景图片内容相关像素
 */
@Data
public class PictorialFormImgPx extends PictorialImgPx {
    /**
     * 图片总宽
     */
    private Integer bgWidth = 750;
    /**
     * 背景图宽/高
     */
    private Integer bgImgWidth = 670;
    /**
     * 背景图内边距
     */
    private Integer bgPadding = 40;
    /**
     * 图片头部
     */
    private Integer headerHeight = 230;
    /**
     * 底部高度
     */
    private Integer bottomHeight = 260;
    /**
     * 图片总高度
     */
    private Integer bgHeight = headerHeight + bottomHeight + bgImgWidth;
    /**
     * 用户头像直径
     */
    private Integer userHeaderDiameter = 90;
    /**
     * 二维码直径
     */
    private Integer qrCodeWidth = 180;
    /**
     * 二维码X
     */
    private Integer qrCodeStartX = bgWidth / 2 - (qrCodeWidth + bgPadding);
    /**
     * 二维码y
     */
    private Integer qrCodeStartY = headerHeight + bgImgWidth + (bottomHeight - qrCodeWidth) / 2;
    /**
     * 指纹X
     */
    private Integer fingerStartX = bgWidth / 2 + bgPadding;
    /**
     * 指纹Y
     */
    private Integer fingerStartY = headerHeight + bgImgWidth + (bottomHeight - qrCodeWidth) / 2;


    // 用户名字体大小 24/96*72
    /**
     * 用户名开始x,y坐标
     */
    private Integer userNameX = bgPadding + userHeaderDiameter + 10;
    private Integer userNameY = bgPadding + userHeaderDiameter / 5;

}
