package com.meidianyi.shop.service.pojo.shop.member.address;

import com.meidianyi.shop.service.pojo.shop.config.pledge.group.UpdateGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 添加地址
 * @author 孔德成
 * @date 2020/5/27
 */
@Getter
@Setter
@ToString
public class AddressAddParam {


    private Integer userId;

    /**
     * 地址id
     */
    @NotNull(groups = UpdateGroup.class)
    private Integer addressId;

    /**
     * 电话
     */
    @NotNull
    @NotBlank
    private String mobile;
    /**
     * 姓名
     */
    @NotNull
    @NotBlank
    private String consignee;

    /**
     * 详细地址
     */
    @NotNull
    @NotBlank
    private String address;
    /**
     * 邮编
     */
    private String zipcode;
    /**
     * 省
     */
    @NotNull
    @NotBlank
    private String provinceName;
    /**
     * 省编码
     */
    private Integer provinceCode;
    /**
     * 市
     */
    @NotNull
    @NotBlank
    private String cityName;
    /**
     * 市编码
     */
    private Integer cityCode;
    /**
     * 区县名
     */
    @NotNull
    @NotBlank
    private String districtName;
    /**
     * 区县编码
     */
    private Integer districtCode;
    /**
     * 定位
     */
    private String lat;
    private String lng;
    /**
     * 是否是默认地址
     */
    @NotNull
    private Byte isDefault;

    /**
     * @return 具体地址
     */
    public String getCompleteAddress(){
        return provinceName + cityName + districtName + address;
    }
}
