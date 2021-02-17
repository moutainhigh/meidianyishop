package com.meidianyi.shop.service.pojo.shop.member.address;

import lombok.Data;

/**
 * 地址
 * @author 王帅
 *
 */
@Data
public class UserAddressVo {

	private Integer addressId;
	private String address;
	private String mobile;
	private String consignee;
    private String completeAddress;
    private String zipcode;
	private String provinceName;
	private Integer provinceCode;
	private Integer cityCode;
	private String cityName;
	private Integer districtCode;
	private String districtName;
	private String lat;
	private String lng;
	private Byte isDefault;
}
