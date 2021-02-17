package com.meidianyi.shop.service.saas.region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.service.foundation.service.MainBaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 新国
 *
 */
@Service

public class RegionService extends MainBaseService {
	@Autowired public ProvinceService province;
	@Autowired public DistrictService district;
	@Autowired public CityService city;


	/**
	 * 获取市区县 名称
	 * @return
	 */
	public List<String> getCityDistrictProvinceName(List<Integer> ids){
		Map<Integer, String> provinceNameMap =province.getProvinceNameByIds(ids);
		Map<Integer, String> cityNameMap = city.getCityNameByIds(ids);
		Map<Integer, String> dictrictNameMap = district.getDictrictNameByids(ids);
		List<String> names =new ArrayList<>();
		ids.forEach(id->{
			if (provinceNameMap.containsKey(id)){
				names.add(provinceNameMap.get(id));
			}else if (cityNameMap.containsKey(id)){
				names.add(cityNameMap.get(id));
			}else {
				names.add(dictrictNameMap.get(id));
			}

		});
		return names;
	}

}
