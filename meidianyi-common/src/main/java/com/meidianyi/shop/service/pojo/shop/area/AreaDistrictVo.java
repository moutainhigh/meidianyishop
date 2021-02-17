package com.meidianyi.shop.service.pojo.shop.area;

import lombok.Data;

/**
 * 区域列表弹窗
 *
 * @author liangchen
 * @date 2019年8月27日
 */
@Data
public class AreaDistrictVo {
    /**
     * 市代码
     */
    private Integer cityId;
    /**
     * 区县代码
     */
    private Integer districtId;
    /**
     * 区县名称
     */
    private String districtName;
}
