package com.meidianyi.shop.service.pojo.shop.member.address;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 孔德成
 * @date 2019/11/18 17:15
 */
@Getter
@Setter
public class AddressCode {
    private String provinceName;
    private Integer provinceCode;
    private Integer cityCode;
    private String cityName;
    private Integer districtCode;
    private String districtName;
}

