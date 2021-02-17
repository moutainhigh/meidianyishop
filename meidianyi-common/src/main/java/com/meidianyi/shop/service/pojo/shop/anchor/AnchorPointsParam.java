package com.meidianyi.shop.service.pojo.shop.anchor;

import lombok.Data;

import java.lang.annotation.Native;
import java.util.List;

/**
 * @author 孔德成
 * @date 2020/8/31 11:09
 */
@Data
public class AnchorPointsParam {

    @Native
    private Integer shopId;
    /**
     * 埋点事件
     */
    private  String event;

    /**
     * 页面地址
     */
    private String page;
    /**
     * 平台
     */
    private String platform;
    /**
     * 终端
     */
    private String device;
    /**
     * 门店
     */
    private Integer storeId;
    /**
     * 用户id
     */
    private Integer userId;

    private List<AnchorPotionKeyParam> list;

}
