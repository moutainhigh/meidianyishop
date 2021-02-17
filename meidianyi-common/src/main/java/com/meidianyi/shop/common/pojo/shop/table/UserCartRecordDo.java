package com.meidianyi.shop.common.pojo.shop.table;

import lombok.Data;

/**
 * @author 孔德成
 * @date 2020/9/15 16:56
 */
@Data
public class UserCartRecordDo {

    private Integer id;
    private Integer userId;
    private Integer goodsId;
    private Integer prdId;
    private Short num;
    private Short delFlag;
    private String userIp;
    private String provinceCode;
    private String province;
    private String cityCode;
    private String city;
    private String districtCode;
    private String district;
    private String lat;
    private String lng;
    private Short count;
    private String createTime;
    private String updateTime;

}
