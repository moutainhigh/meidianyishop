package com.meidianyi.shop.service.pojo.wxapp.distribution;

import lombok.Data;

/**
 * @Author 常乐
 * @Date 2020-02-05
 * 用户基本信息
 */
@Data
public class UserBaseInfoVo {
    private String mobile;

    private String realName;

    private String address;

    private Integer provinceCode;

    private Integer cityCode;

    private Integer districtCode;

    private Integer education;

    private String educationName;

    private Integer industryInfo;

    private String industryName;

    private Integer maritalStatus;

    private String maritalName;

    private String sex;
}
