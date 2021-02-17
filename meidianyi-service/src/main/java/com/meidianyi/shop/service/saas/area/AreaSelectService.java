package com.meidianyi.shop.service.saas.area;

import com.meidianyi.shop.service.foundation.service.MainBaseService;
import com.meidianyi.shop.service.pojo.shop.area.AreaCityVo;
import com.meidianyi.shop.service.pojo.shop.area.AreaDistrictVo;
import com.meidianyi.shop.service.pojo.shop.area.AreaProvinceVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.main.Tables.*;

;
/**
 *	选择区域弹窗
 * @author liangchen
 * @date 2019年8月27日
 */
@Service
public class AreaSelectService extends MainBaseService{
	
	/**
	 *	查询所有省、市、区、县
	 *
	 * @return List<AreaSelectVo>
	 */
	public List<AreaProvinceVo> getAllArea() {
        // 先查省
        List<AreaProvinceVo> provinceVo = db()
            .select(DICT_PROVINCE.PROVINCE_ID, DICT_PROVINCE.NAME.as("provinceName"))
            .from(DICT_PROVINCE)
            .fetch().into(AreaProvinceVo.class);

        List<AreaCityVo> cityVo = db()
            .select(DICT_CITY.PROVINCE_ID, DICT_CITY.CITY_ID, DICT_CITY.NAME.as("cityName"))
            .from(DICT_CITY)
            .fetch().into(AreaCityVo.class);
        Map<Integer, List<AreaCityVo>> cityMap = cityVo.stream().collect(Collectors.groupingBy(AreaCityVo::getProvinceId));

        List<AreaDistrictVo> districtVo = db()
            .select(DICT_DISTRICT.CITY_ID, DICT_DISTRICT.DISTRICT_ID, DICT_DISTRICT.NAME.as("districtName"))
            .from(DICT_DISTRICT)
            .fetch().into(AreaDistrictVo.class);
        Map<Integer, List<AreaDistrictVo>> areaMap = districtVo.stream().collect(Collectors.groupingBy(AreaDistrictVo::getCityId));

        // 遍历省份中的市
        for (AreaProvinceVo p : provinceVo) {
            // 所有市添加到对应的省
            p.setAreaCity(cityMap.get(p.getProvinceId()));
            // 遍历市中的区县
            for (AreaCityVo c : cityMap.get(p.getProvinceId())) {
                // 所有区县添加到对应的市
                c.setAreaDistrict(areaMap.get(c.getCityId()));
            }
        }
        // 返回省-市-区县三级结构
        return provinceVo;
		
	}
}
