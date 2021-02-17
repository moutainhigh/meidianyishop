package com.meidianyi.shop.service.saas.region;

import com.meidianyi.shop.db.main.tables.records.DictDistrictRecord;
import com.meidianyi.shop.service.foundation.service.MainBaseService;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.meidianyi.shop.db.main.tables.DictCity.DICT_CITY;
import static com.meidianyi.shop.db.main.tables.DictDistrict.DICT_DISTRICT;
import static com.meidianyi.shop.db.main.tables.DictProvince.DICT_PROVINCE;

/**
 * 区县
 * @author 新国
 *
 */
@Service

public class DistrictService extends MainBaseService {

	@Autowired
	private CityService cityService;

	/**
	 * 得到城市的区县列表
	 *
	 * @param cityId
	 * @return
	 */
	public Result<DictDistrictRecord> getDistrictList(Integer cityId) {
		return db().selectFrom(DICT_DISTRICT).where(DICT_DISTRICT.CITY_ID.eq(cityId)).fetch();
	}

	public DictDistrictRecord getDistrictName(Integer districtId) {
		return db().selectFrom(DICT_DISTRICT).where(DICT_DISTRICT.DISTRICT_ID.eq(districtId)).fetchAny();
	}

	public Record getAreaName(Integer districtId) {
		return db()
				.select(DICT_PROVINCE.PROVINCE_ID, DICT_PROVINCE.NAME.as("province_name"), DICT_CITY.CITY_ID,
						DICT_CITY.NAME.as("city_name"), DICT_DISTRICT.DISTRICT_ID,
						DICT_DISTRICT.NAME.as("district_name"))
				.from(DICT_DISTRICT)
				.join(DICT_CITY).on(DICT_DISTRICT.CITY_ID.eq(DICT_CITY.CITY_ID))
				.join(DICT_PROVINCE).on(DICT_PROVINCE.PROVINCE_ID.eq(DICT_CITY.PROVINCE_ID))
				.where(DICT_DISTRICT.DISTRICT_ID.eq(districtId))
				.fetchAny();
	}

	public DictDistrictRecord getDistrictName(String districtName, Integer cityId) {
		return db().selectFrom(DICT_DISTRICT)
				.where(DICT_DISTRICT.CITY_ID.eq(cityId).and(DICT_DISTRICT.NAME.eq(districtName)))
				.fetchAny();
	}

	/**
	 * 获取区县id
	 * @param districtName 区县名称
	 * @param cityId 城市di
	 * @return districtId 可能为NUll
	 */
	public Integer getDistrictIdByNameAndCityId(Integer cityId,String districtName){
		return db().select(DICT_DISTRICT.DISTRICT_ID).from(DICT_DISTRICT)
				.where(DICT_DISTRICT.CITY_ID.eq(cityId))
				.and(DICT_DISTRICT.NAME.like(likeValue(districtName)))
				.fetchOne(DICT_DISTRICT.DISTRICT_ID);
	}

	/**
	 * 获取区县id
	 * @param provinceName
	 * @param cityName
	 * @param districtName
	 * @return
	 */
	public Integer getDistrictIdByNames(String provinceName,String cityName,String districtName ){
		return db().select(DICT_DISTRICT.DISTRICT_ID).from(DICT_DISTRICT)
				.leftJoin(DICT_CITY).on(DICT_CITY.CITY_ID.eq(DICT_DISTRICT.CITY_ID))
				.leftJoin(DICT_PROVINCE).on(DICT_DISTRICT.DISTRICT_ID.eq(DICT_CITY.PROVINCE_ID))
				.where(DICT_PROVINCE.NAME.like(likeValue(provinceName)))
				.and(DICT_CITY.NAME.like(likeValue(cityName)))
				.and(DICT_DISTRICT.NAME.like(likeValue(districtName)))
				.fetchOne(DICT_DISTRICT.DISTRICT_ID);
	}

	/**
	 * 根据省市区名称得到详细信息
	 *
	 * @param provinceName
	 * @param cityName
	 * @param districtName
	 * @return
	 */
	public Record getAreaDetailInfo(String provinceName, String cityName, String districtName) {
		return db()
				.select(DICT_PROVINCE.PROVINCE_ID, DICT_PROVINCE.NAME.as("province_name"), DICT_CITY.CITY_ID,
						DICT_CITY.NAME.as("city_name"), DICT_DISTRICT.DISTRICT_ID,
						DICT_DISTRICT.NAME.as("district_name"))
				.from(DICT_DISTRICT)
				.join(DICT_CITY).on(DICT_DISTRICT.CITY_ID.eq(DICT_CITY.CITY_ID))
				.join(DICT_PROVINCE).on(DICT_PROVINCE.PROVINCE_ID.eq(DICT_CITY.PROVINCE_ID))
				.where(DICT_DISTRICT.NAME.eq(districtName))
				.and(DICT_CITY.NAME.eq(cityName))
				.and(DICT_PROVINCE.NAME.eq(provinceName))
				.fetchAny();
	}

	/**
	 * 添加新区
	 *
	 * @param cityId id
	 * @param districtName 修改name
	 * @return 0 or 1
	 */
	public int addNewDistrict(Integer cityId, String districtName) {
		DictDistrictRecord record = db().selectFrom(DICT_DISTRICT)
				.where(DICT_DISTRICT.CITY_ID.eq(cityId))
				.orderBy(DICT_DISTRICT.DISTRICT_ID.desc())
				.fetchAny();
		Integer districtId =null;
		if (record==null){
			 districtId = cityId + 1;
		}else {
			 districtId = record.getDistrictId() + 1;
		}
		while (this.getDistrictName(districtId) != null) {
			districtId += 1;
		}
		return db().insertInto(DICT_DISTRICT, DICT_DISTRICT.DISTRICT_ID, DICT_DISTRICT.CITY_ID, DICT_DISTRICT.NAME)
				.values(districtId, cityId, districtName).execute();
	}

	/**
	 * 获取区县名称
	 * @param ids
	 * @return
	 */
	public Map<Integer, String> getDictrictNameByids(List<Integer> ids){
		return db().select(DICT_DISTRICT.DISTRICT_ID,DICT_DISTRICT.NAME).from(DICT_DISTRICT).where(DICT_DISTRICT.DISTRICT_ID.in(ids))
				.fetchMap(DICT_DISTRICT.DISTRICT_ID,DICT_DISTRICT.NAME);
	}
}
