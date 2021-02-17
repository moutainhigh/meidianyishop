package com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.live;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 小程序-商品详情页-直播信息
 * @author 李晓冰
 * @date 2020年04月09日
 */
@Data
public class RoomDetailMpVo {
    /**直播间id*/
    private Integer roomId;
    /**直播状态码*/
    private Integer liveStatus;
    /**主持人头像*/
    private String anchorImg;
    /**直播开始时间*/
    private Timestamp startTime;
}
