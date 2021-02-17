package com.meidianyi.shop.service.pojo.shop.anchor;

import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 孔德成
 * @date 2020/9/4 17:48
 */
@Data
public class AnchorPointsListParam  extends BasePageParam {

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

    /**
     * 锚点类型
     */
    private  String key;
    /**
     * 值
     */
    private String value;
    /**
     * 开始时间
     */
    private Timestamp startTime;
    /**
     * 结束时间
     */
    private Timestamp endTime;

}
