package com.meidianyi.shop.service.pojo.wxapp.share;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * 小程序-商品和各个活动分享操作基类
 * @author 李晓冰
 * @date 2019年12月27日
 */
@Data
public class GoodsShareInfo {
    /** 分享操作返回码：0正常，1活动已删除无,2活动商品已删除,3指定的宣传图片读取异常，4二维码读取异常*/
    @JsonIgnore
    private Byte shareCode = 0;
    /**文案内容*/
    private String shareDoc;
    /** 分享显示的图片绝对地址 */
    private String imgUrl;
}
