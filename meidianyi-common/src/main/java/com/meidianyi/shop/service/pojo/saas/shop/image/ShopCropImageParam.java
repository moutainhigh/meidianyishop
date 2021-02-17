package com.meidianyi.shop.service.pojo.saas.shop.image;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 孔德成
 * @date 2019/7/17 10:23
 */
@Data
public class ShopCropImageParam {
    /**
     * 图片存放地址
     */
    public String remoteImgPath;
    /**
     * 剪切后宽
     */
    @NotNull
    public Integer cropWidth;
    /**
     * 剪切后高
     */
    @NotNull
    public Integer cropHeight;
    /**
     * 剪切左上位置
     */
    @NotNull
    public Integer x;
    /**
     * 剪切左上位置
     */
    @NotNull
    public Integer y;
    /**
     * 剪切大小
     */
    @NotNull
    public Integer w;
    @NotNull
    public Integer h;
    /**
     * 缩放比例
     */
    @NotNull
    public Double imgScaleW;
    public Integer imgCatId;
    public Integer remoteImgId;
    private Integer size;
}
