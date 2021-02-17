package com.meidianyi.shop.service.pojo.wxapp.store;

import com.meidianyi.shop.service.pojo.shop.store.service.StoreServiceCategoryListQueryVo;
import com.meidianyi.shop.service.pojo.shop.store.service.StoreServiceListQueryVo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author liufei
 * @date 10/18/19
 */
@Data
public class StoreInfoVo {
    private Integer storeId;
    private String storeName;
    private String manager;
    private String mobile;
    private String storeImgs;
    private Byte businessState;
    private Byte businessType;
    private String openingTime;
    private String closeTime;
    private String provinceCode;
    private String cityCode;
    private String districtCode;
    private String latitude;
    private String longitude;
    private String address;
    private Integer group;
    private String service;
    private String content;
    private Integer posShopId;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Short autoPick;
    private Byte delFlag;
    /**
     * The Scan buy.是否支持扫码购.1是,0否
     */
    public Byte scanBuy;

    /**
     * The Distance.门店距离 单位KM
     */
    public String distance;

    /**
     * The Store buy.门店买单开关配置
     */
    public Byte storeBuy;

    /**
     * The All service.门店所有服列表
     */
    public List<StoreServiceListQueryVo> allService;

    /**
     * The Service cat.门店分类服务列表
     */
    public List<StoreServiceCategoryListQueryVo> serviceCat;

}
