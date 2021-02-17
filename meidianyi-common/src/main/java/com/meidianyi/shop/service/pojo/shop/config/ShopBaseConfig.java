package com.meidianyi.shop.service.pojo.shop.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @author 王兵兵
 *
 * 2019年7月3日
 */
@Data
@NoArgsConstructor
public class ShopBaseConfig {
    /** 过期时间 */
	private Timestamp expireTime = null;
    private String shopName = "";
    private String shopAvatar = "";
    private Timestamp created = null;
    /** 营业状态：0未营业，1正在营业 */
    private Byte businessState = 0;
    /** 是否显示小程序端店铺logo，在shop_cfg表 */
    private Byte showLogo = 0;
    /** 小程序端店铺logo图片链接，在主库shop表 */
    private String logo = "";
    /** 小程序端店铺logo点击跳转链接，在shop_cfg表 */
    private String logoLink = "";

    /**
     * 省
     */
    private Integer provinceCode;
    private String provinceName;
    /**
     * 市
     */
    private Integer cityCode;
    private String cityName;
    /**
     * 区县
     */
    private Integer districtCode;
    private String  districtName;
    /**
     * 所在地址
     */
    private Integer address;
    /**
     * 地址全称
     */
    private String completeAddress;
    /**
     * 宣传图
     */
    private String  publicityImg;
    /**
     * 店面详情
     */
    private String copywriting;
    /**
     * 公章
     */
    private String cachet;


}
