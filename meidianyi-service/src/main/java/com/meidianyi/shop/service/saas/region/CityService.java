package com.meidianyi.shop.service.saas.region;

import com.meidianyi.shop.db.main.tables.records.DictCityRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.main.tables.DictCity.DICT_CITY;

/**
 * 市
 * @author 新国
 *
 */
@Service

public class CityService extends MainBaseService {

	/**
	 * 得到省的城市列表
	 *
	 * @param provinceId
	 * @return
	 */
	public Result<DictCityRecord> getCityList(Integer provinceId) {
		return db().selectFrom(DICT_CITY).where(DICT_CITY.PROVINCE_ID.eq(provinceId)).fetch();
	}

	public DictCityRecord getCityName(Integer cityId) {
		return db().selectFrom(DICT_CITY).where(DICT_CITY.CITY_ID.eq(cityId)).fetchAny();
	}

	public DictCityRecord getCityId(String cityName, Integer provinceId) {
		return db().selectFrom(DICT_CITY)
				.where(DICT_CITY.NAME.like(this.likeValue(cityName)).and(DICT_CITY.PROVINCE_ID.eq(provinceId)))
				.fetchAny();
	}

	/**
	 * 获取城市id
	 * @param name 城市名字
	 * @param provinceId 省id
	 * @return 城市id
	 */
	public Integer getCityIdByNameAndProvinceId(Integer provinceId,String name){
		return db().select(DICT_CITY.CITY_ID).from(DICT_CITY)
				.where(DICT_CITY.NAME.like(likeValue(name)))
				.and(DICT_CITY.PROVINCE_ID.eq(provinceId)).fetchOne(DICT_CITY.CITY_ID);
	}

	public int addNewCity(Integer provinceId, String cityName) {
		DictCityRecord record = db().selectFrom(DICT_CITY)
				.where(DICT_CITY.PROVINCE_ID.eq(provinceId))
				.orderBy(DICT_CITY.CITY_ID.desc())
				.fetchAny();
		Integer cityId =null;
		if (record==null){
			cityId = provinceId + 100;
		}else{
			cityId = record.getCityId() + 100;
		}
		while (getCityName(cityId) != null) {
			cityId += 100;
		}
		return db().insertInto(DICT_CITY, DICT_CITY.PROVINCE_ID, DICT_CITY.CITY_ID, DICT_CITY.NAME)
				.values(provinceId, cityId, cityName).execute();
	}

	/**
	 * 获取市名称
	 * @param ids
	 * @return
	 */
	public Map<Integer, String> getCityNameByIds(List<Integer> ids){
		return db().select(DICT_CITY.CITY_ID,DICT_CITY.NAME).from(DICT_CITY).where(DICT_CITY.CITY_ID.in(ids))
				.fetchMap(DICT_CITY.CITY_ID,DICT_CITY.NAME);
	}
}
