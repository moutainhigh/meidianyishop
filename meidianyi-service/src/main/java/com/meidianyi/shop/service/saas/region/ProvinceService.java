package com.meidianyi.shop.service.saas.region;

import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.db.main.tables.records.DictProvinceRecord;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.meidianyi.shop.db.main.tables.DictCity.DICT_CITY;
import static com.meidianyi.shop.db.main.tables.DictDistrict.DICT_DISTRICT;
import static com.meidianyi.shop.db.main.tables.DictProvince.DICT_PROVINCE;

/**
 * 省
 * @author 新国
 *
 */
@Service

public class ProvinceService extends MainBaseService {
    @Autowired
    public CityService cityService;

    @Autowired
    public DistrictService districtService;

	public Result<DictProvinceRecord> getAll() {
		return db().fetch(DICT_PROVINCE);
	}

	public DictProvinceRecord getProvinceName(Integer provinceId) {
		return db().selectFrom(DICT_PROVINCE).where(DICT_PROVINCE.PROVINCE_ID.eq(provinceId)).fetchAny();
	}

	public DictProvinceRecord getProvinceName(String provinceName) {
		return db().selectFrom(DICT_PROVINCE).where(DICT_PROVINCE.NAME.like(this.likeValue(provinceName))).fetchAny();
	}

    /**
     * 通过名字查询省id
     * @param provinceName 名称
     * @return provinceId or null 没有查到放回null
     */
	public Integer getProvinceIdByName(String provinceName){
	    return db().select(DICT_PROVINCE.PROVINCE_ID).from(DICT_PROVINCE)
                .where(DICT_PROVINCE.NAME.like(likeValue(provinceName)))
                .fetchOne(DICT_PROVINCE.PROVINCE_ID);
    }

    /**
     * 根据provinceId跟新微信地址名称
     * @param provinceId  省id
     * @param provinceName 新名称
     * @return 1 or 0
     */
	public  int updateProvinceName(Integer provinceId,String provinceName)
    {
        return db().update(DICT_PROVINCE)
    	.set(DICT_PROVINCE.NAME,provinceName).where(DICT_PROVINCE.PROVINCE_ID.eq(provinceId)).execute();
    }

    /**
     * Gets full address by id.获取完整的省市区地址信息
     *
     * @param provinceId the province id
     * @param cityId     the city id
     * @param districtId the district id
     * @return the full address by id
     */
    public String getFullAddressById(int provinceId, int cityId, int districtId) {
        String provinceName = Optional.ofNullable(getProvinceName(provinceId)).orElseThrow(() -> new BusinessException(JsonResultCode.CODE_DATA_NOT_EXIST, "provinceId: " + provinceId)).getName();
        String cityName = Optional.ofNullable(cityService.getCityName(cityId)).orElseThrow(() -> new BusinessException(JsonResultCode.CODE_DATA_NOT_EXIST, "cityId: " + cityId)).getName();
        String districtName = Optional.ofNullable(districtService.getDistrictName(districtId)).orElseThrow(() -> new BusinessException(JsonResultCode.CODE_DATA_NOT_EXIST, "districtId: " + districtId)).getName();
        return provinceName + StringUtils.SPACE + cityName + StringUtils.SPACE + districtName + StringUtils.SPACE;
    }

    public String getProvincePinYinByCode(String code) {
        return db().select(DICT_PROVINCE.PINYIN).from(DICT_PROVINCE).where(DICT_PROVINCE.PROVINCE_ID.eq(Integer.valueOf(code))).fetchOneInto(String.class);
    }

    public String getCityPinYinByCode(String code) {
        return db().select(DICT_CITY.PINYIN).from(DICT_CITY).where(DICT_CITY.CITY_ID.eq(Integer.valueOf(code))).fetchOneInto(String.class);
    }

    public String getDistrictPinYinByCode(String code) {
        return db().select(DICT_DISTRICT.PINYIN).from(DICT_DISTRICT).where(DICT_DISTRICT.DISTRICT_ID.eq(Integer.valueOf(code))).fetchOneInto(String.class);
    }


    /**
     * 获取省级名称
     * @param ids
     * @return
     */
    public Map<Integer, String> getProvinceNameByIds(List<Integer> ids){
        return db().select(DICT_PROVINCE.PROVINCE_ID,DICT_PROVINCE.NAME).from(DICT_PROVINCE).where(DICT_PROVINCE.PROVINCE_ID.in(ids))
                .fetchMap(DICT_PROVINCE.PROVINCE_ID,DICT_PROVINCE.NAME);
    }
}
