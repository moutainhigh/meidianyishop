package com.meidianyi.shop.service.pojo.shop.area;

import java.util.List;

import lombok.Data;

/**
 *	区域列表弹窗
 * @author liangchen
 * @date 2019年8月27日
 */
@Data
public class AreaProvinceVo {
	
	/** 省代码 */
	private Integer provinceId;
	/** 省名称 */
	private String provinceName;
	/** 市代码及名称 */
	public List<AreaCityVo> areaCity;

}
