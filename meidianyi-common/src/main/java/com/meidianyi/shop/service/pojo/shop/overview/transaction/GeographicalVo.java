package com.meidianyi.shop.service.pojo.shop.overview.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author liufei
 * @date 2019/7/31
 * @description
 */
@Data
public class GeographicalVo {
    /** 省份 */
    private String province;
    /** 省份编码 */
    @JsonIgnore
    private String provinceCode;
    /** 付款总金额 */
    private double totalDealMoney;
    /** 付款人数 */
    private int orderUserNum;
    /** 访客数 */
    private int uv;
    /** 订单数 */
    private int orderNum;
    /** 访问付款转化率 */
    private double uv2paid;
}
