package com.meidianyi.shop.service.pojo.shop.area;

import lombok.Data;

import java.util.List;

/**
 * 区域列表弹窗
 *
 * @author liangchen
 * @date 2019年8月27日
 */
@Data
public class AreaCityVo {
    /**
     * 省代码
     */
    private Integer provinceId;
    /**
     * 市代码
     */
    private Integer cityId;
    /**
     * 市名称
     */
    private String cityName;
    /**
     * 区县代码及名称
     */
    public List<AreaDistrictVo> areaDistrict;
}
