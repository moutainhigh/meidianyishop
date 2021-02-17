package com.meidianyi.shop.service.pojo.shop.anchor;

import lombok.Data;

import java.util.List;

/**
 * @author 孔德成
 * @date 2020/9/7 14:41
 */
@Data
public class AnchorPotionEventBo {

    /**
     * 事件
     */
    private String event;
    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 关键字
     */
    List<AnchorPotionKeyBo> keys;

}
