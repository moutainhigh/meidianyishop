package com.meidianyi.shop.service.pojo.wxapp.store;

import lombok.Data;

/**
 * The type Location.
 *
 * @author liufei
 * @date 10 /16/19
 */
@Data
public class Location {
    /**
     * The Latitude.维度
     */
    public double latitude;
    /**
     * The Longitude.经度
     */
    public double longitude;

    /**   todo 预留字段,暂时未使用 */
    public String speed;
    public String accuracy;
    public String verticalAccuracy;
    public String horizontalAccuracy;
    public String errMsg;
}
