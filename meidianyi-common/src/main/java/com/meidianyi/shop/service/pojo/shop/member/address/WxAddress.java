package com.meidianyi.shop.service.pojo.shop.member.address;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 小程序调wx地址簿数据接受
 * @author 王帅
 */
@Data
public class WxAddress {
    private String errMsg;
    @NotNull
    private String provinceName;
    @NotNull
    private String cityName;
    @NotNull
    private String countyName;
    private String detailInfo;
    private String nationalCode;
    private String postalCode;
    private String telNumber;
    private String userName;

    public String getCompleteAddress(){
        return new StringBuilder().append(provinceName).append(cityName).append(countyName).append(detailInfo).toString();
    }
}
