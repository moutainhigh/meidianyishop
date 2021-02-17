package com.meidianyi.shop.service.pojo.shop.store.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.service.pojo.shop.store.validated.StoreAddValidatedGroup;
import com.meidianyi.shop.service.pojo.shop.store.validated.StoreCodingCheckValidatedGroup;
import com.meidianyi.shop.service.pojo.shop.store.validated.StoreUpdateValidatedGroup;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author 王兵兵
 *
 * 2019年7月8日
 */
@Data
@NoArgsConstructor
public class StorePojo implements Comparable<StorePojo> {
    @NotNull(groups = {StoreUpdateValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private Integer   storeId;
    @NotNull(groups = {StoreAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private String    storeName;
    @NotNull(groups = {StoreAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private String    manager;
    @NotNull(groups = {StoreAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private String    mobile;
    @NotNull(groups = {StoreAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private String    storeImgs;
    private Byte      businessState;
    private Byte      businessType;
    @NotNull(groups = {StoreAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private String    openingTime;
    @NotNull(groups = {StoreAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private String    closeTime;
    @NotNull(groups = {StoreAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private String    provinceCode;
    @NotNull(groups = {StoreAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private String    cityCode;
    @NotNull(groups = {StoreAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private String    districtCode;
    @NotNull(groups = {StoreAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private String    latitude;
    @NotNull(groups = {StoreAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private String    longitude;
    @NotNull(groups = {StoreAddValidatedGroup.class},message = JsonResultMessage.MSG_PARAM_ERROR)
    private String    address;
    private Integer   group;
    private String    service;
    private String    content;
    private Integer   posShopId;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Short     autoPick;
    private Byte      delFlag;
    /** 是否支持同城配送 0否 1支持 */
    private Byte cityService;
    /**
     * The Scan buy.是否支持扫码购.1是,0否
     */
    public Byte scanBuy;
    /**
     * 门店类型 0、普通门店 1、医院
     */
    private Byte storeType;

    /**
     * The Distance.门店距离 单位KM
     */
    public String distance;
    /** 自提取货时间类型 1门店营业时间 2自定义时间 */
    private Byte pickTimeAction;
    /**
     * 门店编码
     */
    private String storeCode;
    /** 自提时间明细 */
    @JsonIgnore
    private String pickTimeDetail;
    private StorePickDetailPojo pickDetail;
    @Override
    public int compareTo(StorePojo storePojo) {
        if (Double.parseDouble(this.distance) >= Double.parseDouble(storePojo.distance)) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * 门店列表已存在医院
     */
    public static final Byte IS_EXIST_HOSPITAL = 2;
}
