package com.meidianyi.shop.service.pojo.wxapp.share;

import lombok.Data;

/**
 * 商品分享操作记录类
 * @author 李晓冰
 * @date 2019年12月26日
 */
@Data
public class ShareRecordParam {
    /** 活动id */
    private Integer activityId;
    /** 活动类型 */
    private Integer activityType;
    private Integer userId;
}
