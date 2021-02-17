package com.meidianyi.shop.service.pojo.shop.summary.portrait;

import lombok.Data;

/**
 * 用户画像入参
 *
 * @author 郑保乐
 */
@Data
public class PortraitParam {

    public static final int LAST_DAY = 1;
    public static final int LAST_WEEK = 2;
    public static final int LAST_MONTH = 3;

    /**
     * 范围（0：昨天，1：最近7天，2：最近30天）
     */
    private Integer type;
    private String startDate;
    private String endDate;
}
