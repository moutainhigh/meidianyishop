package com.meidianyi.shop.service.pojo.shop.anchor;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 孔德成
 * @date 2020/9/10 10:59
 */
@Data
public class AnchorPointsReportVo {

    private String date;
    private String event;
    private String key;
    private String value;
    private String name;
    private BigDecimal money;
    private String device;
    private String platform;
    private Integer count;
}
