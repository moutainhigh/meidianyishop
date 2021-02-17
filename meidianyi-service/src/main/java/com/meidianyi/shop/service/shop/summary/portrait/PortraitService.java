package com.meidianyi.shop.service.shop.summary.portrait;

import static com.meidianyi.shop.db.main.tables.DictCity.DICT_CITY;
import static com.meidianyi.shop.db.shop.tables.MpUserPortrait.MP_USER_PORTRAIT;
import static com.meidianyi.shop.db.shop.tables.UserDetail.USER_DETAIL;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.main.tables.records.DictCityRecord;
import com.meidianyi.shop.db.main.tables.records.DictProvinceRecord;
import com.meidianyi.shop.db.shop.tables.records.MpUserPortraitRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.summary.KeyValueChart;
import com.meidianyi.shop.service.pojo.shop.summary.portrait.Portrait;
import com.meidianyi.shop.service.pojo.shop.summary.portrait.PortraitDeviceItem;
import com.meidianyi.shop.service.pojo.shop.summary.portrait.PortraitItem;
import com.meidianyi.shop.service.pojo.shop.summary.portrait.PortraitMaxAndMin;
import com.meidianyi.shop.service.pojo.shop.summary.portrait.PortraitParam;
import com.meidianyi.shop.service.pojo.shop.summary.portrait.PortraitSum;
import com.meidianyi.shop.service.pojo.shop.summary.portrait.PortraitVo;
import com.meidianyi.shop.service.pojo.shop.summary.portrait.ProvinceParam;
import com.meidianyi.shop.service.pojo.shop.summary.portrait.ProvinceVo;

/**
 * 用户画像
 *
 * @author 郑保乐
 */
@Service
public class PortraitService extends ShopBaseService {

    public PortraitVo getPortrait(PortraitParam param) {
        Integer type = param.getType();
        PortraitVo vo = new PortraitVo();
        MpUserPortraitRecord portraitResult = getPortraitResult(type);
        if(null==portraitResult) {
        	return vo;
        }
        Portrait visitUv = parseVisitJson(portraitResult.getVisitUv());
        Portrait visitUvNew = parseVisitJson(portraitResult.getVisitUvNew());
        KeyValueChart activeUser = getChart(visitUv);
        KeyValueChart activeUserNew = getChart(visitUvNew);
        visitUv.setAgesFirst(activeUser);
        visitUvNew.setAgesFirst(activeUserNew);
        PortraitMaxAndMin setMaxAndMin = setMaxAndMin(visitUv);
        PortraitMaxAndMin setMaxAndMin2 = setMaxAndMin(visitUvNew);
        //删除省市中value为0的数据
        removeZero(visitUv);
        removeZero(visitUvNew);
        //删除设备中value为0的
        removeZeroByDevices(visitUv);
        removeZeroByDevices(visitUvNew);
        PortraitSum activeUserSum = portraitSumObject(visitUv);
        PortraitSum newAddUserSum = portraitSumObject(visitUvNew);
        //移除省字
        visitUv.setProvince(remove(visitUv));
        visitUvNew.setProvince(remove(visitUvNew));
        vo.setActiveUser(visitUv);
        vo.setNewAddUser(visitUvNew);
        vo.setActiveUserSum(activeUserSum);
        vo.setNewAddUserSum(newAddUserSum);
        vo.setStartDate(showDate(type));
        vo.setEndDate(showDate(0));
        vo.setActiveProRange(setMaxAndMin);
        vo.setNewAddUserProRange(setMaxAndMin2);
        return vo;
    }

    public ProvinceVo getProvincePortrait(ProvinceParam param) {
        String province = param.getProvince();
        Result<Record2<Integer, Integer>> result = getProvinceSumResult(province);
        Map<Integer, String> cityMap = cityMap(province);
        List<PortraitItem> items = result.map(r -> {
            PortraitItem item = new PortraitItem();
            Integer cityId = (Integer) r.get(0);
            item.setId(cityId);
            item.setName(cityMap.get(cityId));
            item.setValue((Integer) r.getValue(1));
            return item;
        });
        ProvinceVo vo = new ProvinceVo();
        vo.setList(items);
        return vo;
    }

    /**
     * 将字段存储的 json 转换成 Portrait 对象
     */
    private Portrait parseVisitJson(String json) {
        return Util.parseJson(json, new TypeReference<Portrait>() {
        });
    }

    /**
     * 生成图表对象
     */
    private KeyValueChart getChart(Portrait portrait) {
        List<PortraitItem> ages = portrait.getAges();
        List<String> names = ages.stream().map(PortraitItem::getName).collect(Collectors.toList());
        List<Integer> values = ages.stream().map(PortraitItem::getValue).collect(Collectors.toList());
        KeyValueChart chart = new KeyValueChart();
        chart.setKeys(names);
        chart.setValues(values);
        return chart;
    }

    /**
     * 计算各个指标的总和
     */
    private Integer portraitSum(List<PortraitItem> items) {
        return items.parallelStream().mapToInt(PortraitItem::getValue).sum();
    }

    /**
     * 计算各个指标的总和（设备）
     */
    private Integer portraitDeviceSum(List<PortraitDeviceItem> items) {
        return items.parallelStream().mapToInt(PortraitDeviceItem::getValue).sum();
    }

    /**
     * 生成总和对象
     */
    private PortraitSum portraitSumObject(Portrait portrait) {
        PortraitSum sum = new PortraitSum();
        sum.setProvince(portraitSum(portrait.getProvince()));
        sum.setAges(portraitSum(portrait.getAges()));
        sum.setCity(portraitSum(portrait.getCity()));
        sum.setDevices(portraitDeviceSum(portrait.getDevices()));
        sum.setGenders(portraitSum(portrait.getGenders()));
        sum.setPlatforms(portraitSum(portrait.getPlatforms()));
        return sum;
    }

    private MpUserPortraitRecord getPortraitResult(Integer type) {
        List<MpUserPortraitRecord> vo = new ArrayList<>();
        List<MpUserPortraitRecord> recordList = db().select(MP_USER_PORTRAIT.VISIT_UV, MP_USER_PORTRAIT.VISIT_UV_NEW,MP_USER_PORTRAIT.TYPE)
                .from(MP_USER_PORTRAIT)
                .where(MP_USER_PORTRAIT.CREATE_TIME.lessOrEqual(Timestamp.from(Instant.now())))
                .orderBy(MP_USER_PORTRAIT.ID.desc())
                .limit(3)
                .fetchInto(MP_USER_PORTRAIT);
        if (null!=recordList&&recordList.size()>0){
            recordList.forEach(r->{
                if (type.byteValue()== r.getType()){
                    vo.add(r);
                }
            });
        }
        MpUserPortraitRecord result = new MpUserPortraitRecord();
        if (vo.size() > 0){
            result = vo.get(0);
        }
        return result;
    }

    /**
     * 按省份统计人数查询
     *
     * @param provinceName 省份名称
     *
     * @return province_id count
     */
    private Result<Record2<Integer, Integer>> getProvinceSumResult(String provinceName) {
    	DictProvinceRecord province = saas.region.province.getProvinceName(provinceName);

        return db().select(USER_DETAIL.CITY_CODE, DSL.count(USER_DETAIL.ID))
                .from(USER_DETAIL)
                .where(USER_DETAIL.PROVINCE_CODE.equal(province.getProvinceId()))
                .and(USER_DETAIL.CITY_CODE.notEqual(0))
                .groupBy(USER_DETAIL.CITY_CODE)
                .fetch();
    }

    /**
     * 获取城市 id 名称 map
     *
     * @param provinceName 省份名称
     */
    private Map<Integer, String> cityMap(String provinceName) {
    	DictProvinceRecord province = saas.region.province.getProvinceName(provinceName);
    	Result<DictCityRecord> cityList = saas.region.city.getCityList(province.getProvinceId());
    	return cityList.intoMap(DICT_CITY.CITY_ID, DICT_CITY.NAME);
    }

    /**
     * 根据类型显示开始时间和结束时间
     * @param type
     * @return
     */
	private static String showDate(Integer type) {
		Timestamp time = null;
		switch (type) {
		case 0:
			// 昨天
			time = DateUtils.getTimeStampPlus(-1, ChronoUnit.DAYS);
			break;
		case 1:
			// 最近七天
			time = DateUtils.getTimeStampPlus(-7, ChronoUnit.DAYS);
			break;
		case 2:
			// 最近三十天
			time = DateUtils.getTimeStampPlus(-30, ChronoUnit.DAYS);
			break;
		default:
			time = Timestamp.valueOf(LocalDateTime.now());
			break;
		}
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime localDateTime2=time.toLocalDateTime();
		String format2 = df.format(localDateTime2);
		return format2;

	}

    /**
     * 移除省份中的省字，地图匹配用
     * @param portrait
     * @return
     */
	private List<PortraitItem> remove(Portrait portrait) {
		List<PortraitItem> provinceList = portrait.getProvince();
		Boolean flag = true;
		for (PortraitItem item : provinceList) {
			if (item.getName().contains("省")) {
				item.setName(item.getName().replace("省", ""));
			}
			if (item.getName().contains("西藏")) {
				flag = false;
			}
		}
		if (!flag) {
			provinceList.add(new PortraitItem(666, "西藏", 0));
		}
		return provinceList;
	}

    /**
     * 删除省市中数量为0的值
     * @param portrait
     */
	private void removeZero(Portrait portrait) {
		Iterator<PortraitItem> iterator = portrait.getProvince().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getValue().equals(0)) {
				iterator.remove();
			}
		}
		Iterator<PortraitItem> iterator2 = portrait.getCity().iterator();
		while (iterator2.hasNext()) {
			if (iterator2.next().getValue().equals(0)) {
				iterator2.remove();
			}
		}
	}

    /**
     * 删除设备中数量为0的值
     * @param portrait
     */
	private void removeZeroByDevices(Portrait portrait) {
		Iterator<PortraitDeviceItem> iterator = portrait.getDevices().iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getValue().equals(0)) {
				iterator.remove();
			}
		}
	}

	/**
	 * 获取最大值
	 * @param items
	 * @return
	 */
    private Integer portraitMax(List<PortraitItem> items) {
        return items.parallelStream().mapToInt(PortraitItem::getValue).max().getAsInt();
    }


	/**
	 * 获取最小值
	 * @param items
	 * @return
	 */
    private Integer portraitMin(List<PortraitItem> items) {
        return items.parallelStream().mapToInt(PortraitItem::getValue).min().getAsInt();
    }

	private PortraitMaxAndMin setMaxAndMin(Portrait portrait) {
		List<PortraitItem> province = portrait.getProvince();
		int size = province.size();
		/** 省的数量34个*/
		int min = size >= 34 ? portraitMin(province) : 0;
		return new PortraitMaxAndMin(portraitMax(province), min);
	}
}
