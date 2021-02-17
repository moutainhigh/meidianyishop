package com.meidianyi.shop.service.shop.market.channel;

import static com.meidianyi.shop.common.foundation.util.Util.translateMessage;
import static com.meidianyi.shop.db.shop.Tables.CHANNEL_STATISTICAL;

import java.sql.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jooq.Result;
import org.jooq.SelectWhereStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.ChannelRecord;
import com.meidianyi.shop.db.shop.tables.records.ChannelStatisticalRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelConstant;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelStatisticalChartVo;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelStatisticalConstant;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelStatisticalInfoVo;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelStatisticalParam;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelStatisticalSeriesVo;;

/**
 * @author huangronggang
 * @date 2019年8月15日
 */
@Service
public class ChannelStatisticalService extends ShopBaseService {

	@Autowired
	ChannelService channelService;

	public final static int YESTERDAY_OFFSET = -1;
	public final static int WEEK_OFFSET = -7;
	public final static int MONTH_OFFSET = -30;
	public final static String SEPARATOR = ",";
	public final static String I18N_RESOURCE = "messages";
	public final static String LEGEND_ALL_DATA = "market.channel.statistical.chart.all.data";
	public final static String LEGEND_NO_CHANNEL_DATA = "market.channel.statistical.chart.no.data";

	public final static Byte CHANNEL_SOURCE_TYPE_NO = 2;

	public final static int WEEK_INDEX = 1;
	public final static int MONTH_INDEX = 2;
	public final static int DAY_INDEX = 0;

	/**
	 * 查询某渠道昨日访问量数据，如果该渠道类型为自定义页面类型则传入pageId,如果渠道类型为商品类型则传入商品ID
	 * 
	 * @param channelId
	 * @param pageId
	 * @param goodsId
	 * @return
	 */
	public ChannelStatisticalRecord selectYesterDayRecord(Integer channelId, Integer pageId, Integer goodsId) {
		Byte channelType = null;
		if (pageId != null) {
			channelType = ChannelConstant.SOURCETYPE_CUSTOMIZE;
		} else if (goodsId != null) {
			channelType = ChannelConstant.SOURCETYPE_GOODS;
		} else {
			return null;
		}
		return selectRecord(channelType, yesterDate(), pageId, goodsId);
	}

	/**
	 * 渠道页面数据统计的折线图
	 * 
	 * @param param
	 * @param lang
	 * @return
	 */
	public ChannelStatisticalChartVo createChart(ChannelStatisticalParam param, String lang) {
		Date startDate = null, endDate = null;
		if (ChannelStatisticalConstant.TIME_TYPE_WEEK.equals(param.getTimeType())) {
			startDate = dateOffsetDay(nowDate(), WEEK_OFFSET);
			endDate = yesterDate();
		} else if (ChannelStatisticalConstant.TIME_TYPE_MONTH.equals(param.getTimeType())) {
			startDate = dateOffsetDay(nowDate(), MONTH_OFFSET);
			endDate = yesterDate();
		} else {
//			自定义类型用户手工输入查询范围，类型为util.Date转为sqlDate
			if (param.getStartDate() == null || param.getEndDate() == null) {
				return null;
			}
			startDate = toSqlDate(param.getStartDate());
			endDate = toSqlDate(param.getEndDate());
		}
		List<Date> dateList = dateRange(startDate, endDate);
		if (dateList.isEmpty()) {
			return null;
		}
		ChannelStatisticalChartVo chartVo = new ChannelStatisticalChartVo();
		chartVo.setDateList(dateList);

//		查询所有与选中的渠道页关联了相同页面或商品的渠道
		ChannelRecord selectedChannel = channelService.selectChannelRecord(param.getChannelId());
		if (selectedChannel == null) {
			return null;
		}
		List<Integer> channelIdList = selectStatisticalChannelIdList(selectedChannel, endDate);
		List<ChannelRecord> channelList = channelService.selectChannelRecord(channelIdList);

//		找出时间范围内 指定页面或商品的访问数据
		Map<Date, Result<ChannelStatisticalRecord>> statisticalMap = selectChannelStatisticalRecord(
				selectedChannel.getSourceType(), selectedChannel.getPageId(), selectedChannel.getGoodsId(), startDate,
				endDate);

		transferName(channelList, chartVo.getChannelNameList(), lang);
		createStatisticalSeries(chartVo, channelList, dateList, statisticalMap, lang, param.getIndicator(),
				param.getVisitorType());
		createStatisticalTabel(chartVo, channelList, dateList, statisticalMap, lang);
		return chartVo;

	}

	/**
	 * 生成渠道页面数据统计页面 折线图填充数据
	 * 
	 * @param chartVo
	 * @param channelList
	 * @param dateList
	 * @param statisticalMap
	 * @param lang
	 * @param indicator
	 * @param visitorType
	 */
	private void createStatisticalSeries(ChannelStatisticalChartVo chartVo, List<ChannelRecord> channelList,
			List<Date> dateList, Map<Date, Result<ChannelStatisticalRecord>> statisticalMap, String lang,
			Byte indicator, Byte visitorType) {
		for (ChannelRecord channelRecord : channelList) {
			ChannelStatisticalSeriesVo seriesVo = new ChannelStatisticalSeriesVo();
			seriesVo.setChannelName(channelRecord.getChannelName());
			List<Integer> accessData = seriesVo.getAccessData();
			Integer channelId = channelRecord.getId();
			for (Date date : dateList) {
				ChannelStatisticalRecord statisticalRecord = getStatisticalByDate(statisticalMap, date);
				accessData.add(getChartData(statisticalRecord, channelId, indicator, visitorType));
			}
			chartVo.getSeries().add(seriesVo);
		}
		ChannelStatisticalSeriesVo allSeries = getAllSeries(statisticalMap, dateList, indicator, lang);
		ChannelStatisticalSeriesVo noSeries = chartVo.calNoChannelSeries(chartVo.getSeries(), allSeries,
				translateMessage(lang, LEGEND_NO_CHANNEL_DATA, I18N_RESOURCE));
		chartVo.getSeries().add(allSeries);
		chartVo.getSeries().add(noSeries);
	}

	/**
	 * 获取统计页面的表格数据
	 * 
	 * @param chartVo
	 * @param channelList
	 * @param dateList
	 * @param statisticalMap
	 * @param lang
	 * @return
	 */
	private List<ChannelStatisticalInfoVo> createStatisticalTabel(ChannelStatisticalChartVo chartVo,
			List<ChannelRecord> channelList, List<Date> dateList,
			Map<Date, Result<ChannelStatisticalRecord>> statisticalMap, String lang) {
		List<ChannelStatisticalInfoVo> result = chartVo.getStatisticalList();
		Integer allPv = 0, allUv = 0, newPv = 0, oldPv = 0;
		for (Date date : dateList) {
			ChannelStatisticalRecord record = getStatisticalByDate(statisticalMap, date);
			if (record == null) {
				continue;
			}
			allPv += valueOfIndex(record.getAllPv(), DAY_INDEX, SEPARATOR);
			allUv += valueOfIndex(record.getAllUv(), DAY_INDEX, SEPARATOR);
			newPv += valueOfIndex(record.getNewPv(), DAY_INDEX, SEPARATOR);
			oldPv += valueOfIndex(record.getOldPv(), DAY_INDEX, SEPARATOR);
		}
		String legendAllName = translateMessage(lang, LEGEND_ALL_DATA, I18N_RESOURCE);
		String legendNoChannelName = translateMessage(lang, LEGEND_NO_CHANNEL_DATA, I18N_RESOURCE);
		ChannelStatisticalInfoVo allInfo = new ChannelStatisticalInfoVo(legendAllName, allPv, allUv, newPv, oldPv);
		ChannelStatisticalInfoVo noChannelInfo = new ChannelStatisticalInfoVo(legendNoChannelName, allPv, allUv, newPv,
				oldPv);
		result.add(allInfo);
		result.add(noChannelInfo);
		for (ChannelRecord channelRecord : channelList) {
			Integer channelAllPv = 0, channelAllUv = 0, channelNewPv = 0, channelOldPv = 0;
			for (Date date : dateList) {
				ChannelStatisticalRecord record = getStatisticalByDate(statisticalMap, date);
				if (record == null) {
					continue;
				}
				channelAllPv += getMapData(record.getChannelAllPv(), channelRecord.getId());
				channelAllUv += getMapData(record.getChannelAllUv(), channelRecord.getId());
				channelNewPv += getMapData(record.getChannelNewPv(), channelRecord.getId());
				channelOldPv += getMapData(record.getChannelOldPv(), channelRecord.getId());
			}
			ChannelStatisticalInfoVo infoVo = new ChannelStatisticalInfoVo(channelRecord.getChannelName(), channelAllPv,
					channelAllUv, channelNewPv, channelOldPv);
			result.add(infoVo);
			allPv -= channelAllPv;
			allUv -= channelAllUv;
			newPv -= channelNewPv;
			oldPv -= channelOldPv;
		}
		noChannelInfo.setAllPv(allPv);
		noChannelInfo.setAllUv(allUv);
		noChannelInfo.setNewPv(newPv);
		noChannelInfo.setOldPv(oldPv);
		return result;
	}

	/**
	 * @param statisticalRecord
	 * @param indicator
	 * @param dateList
	 * @return
	 */
	private ChannelStatisticalSeriesVo getAllSeries(Map<Date, Result<ChannelStatisticalRecord>> statisticalMap,
			List<Date> dateList, Byte indicator, String lang) {
		ChannelStatisticalSeriesVo seriesVo = new ChannelStatisticalSeriesVo();
		seriesVo.setChannelName(translateMessage(lang, LEGEND_ALL_DATA, I18N_RESOURCE));
		List<Integer> legendAllAccessData = seriesVo.getAccessData();
		for (Date date : dateList) {
			ChannelStatisticalRecord statisticalRecord = getStatisticalByDate(statisticalMap, date);
			int data = 0;
			if (statisticalRecord != null) {
				if (ChannelConstant.ACCESS_TIMES.equals(indicator)) {
					data = valueOfIndex(statisticalRecord.getAllPv(), DAY_INDEX, SEPARATOR);
				} else {
					data = valueOfIndex(statisticalRecord.getAllUv(), DAY_INDEX, SEPARATOR);
				}
			}
			legendAllAccessData.add(data);
		}
		return seriesVo;
	}

	/**
	 * 因为通过jooq 按日期分组来查，查出结果Map中value为Record 结构,所以使用本方法取出Record中的第一个值，
	 * 
	 * @param statisticalMap 访问信息 key 为日期，value为该日访问信息
	 * @param date
	 * @return
	 */
	private ChannelStatisticalRecord getStatisticalByDate(Map<Date, Result<ChannelStatisticalRecord>> statisticalMap,
			Date date) {
		ChannelStatisticalRecord record = null;
		Result<ChannelStatisticalRecord> result = statisticalMap.get(date);
		if (result != null) {
			record = result.get(0);
		}
		return record;
	}

	/**
	 * 根据查询条件中查询指标和 访客类型取对应列中的数据
	 * 
	 * @param temp
	 * @param indicator
	 * @param visitorType
	 */
	private int getChartData(ChannelStatisticalRecord temp, Integer channelId, Byte indicator, Byte visitorType) {
		if (temp == null) {
			return 0;
		}
		if (ChannelConstant.ACCESS_TIMES.equals(indicator)) {
			if (ChannelConstant.NEW_VISITOR.equals(visitorType)) {
				return getMapData(temp.getChannelNewPv(), channelId);
			} else if (ChannelConstant.OLD_VISITOR.equals(visitorType)) {
				return getMapData(temp.getChannelOldPv(), channelId);
			} else {
				return getMapData(temp.getChannelAllPv(), channelId);
			}
		} else {
			if (ChannelConstant.NEW_VISITOR.equals(visitorType)) {
				return getMapData(temp.getChannelNewUv(), channelId);
			} else if (ChannelConstant.OLD_VISITOR.equals(visitorType)) {
				return getMapData(temp.getChannelOldUv(), channelId);
			} else {
				return getMapData(temp.getChannelAllUv(), channelId);
			}
		}
	}

	/**
	 * @param json
	 * @param channelId
	 * @return
	 */
	private Integer getMapData(String json, Integer channelId) {
//		key为channelID，value为访问量数据
		Map<Integer, Integer> accessMap = convertJson2Map(json);
		Integer result = accessMap.get(channelId);
		if (result == null) {
			return 0;
		}
		return result;
	}

	/**
	 * 获取一段时间内某个页面的访问信息的统计记录
	 * 
	 * @param sourceType
	 * @param pageId
	 * @param goodsId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Map<Date, Result<ChannelStatisticalRecord>> selectChannelStatisticalRecord(Byte sourceType, Integer pageId,
			Integer goodsId, Date startDate, Date endDate) {
		SelectWhereStep<ChannelStatisticalRecord> select = db().selectFrom(CHANNEL_STATISTICAL);
		buildByChannelType(select, sourceType, pageId, goodsId);
		Map<Date, Result<ChannelStatisticalRecord>> channelStatisticalMap = select
				.where(CHANNEL_STATISTICAL.REF_DATE.between(startDate, endDate))
				.fetchGroups(CHANNEL_STATISTICAL.REF_DATE);
		return channelStatisticalMap;
	}

	private List<Integer> selectStatisticalChannelIdList(ChannelRecord channelRecord, Date date) {
		if (channelRecord == null) {
			return Collections.emptyList();
		}
		ChannelStatisticalRecord statisticalRecord = selectRecord(channelRecord.getSourceType(), date,
				channelRecord.getPageId(), channelRecord.getGoodsId());
		if (statisticalRecord == null) {
			return Collections.emptyList();
		}
		List<Integer> channelIdList = Util.valueOf(statisticalRecord.getChannelId().split(SEPARATOR));
		return channelIdList;
	}

	/**
	 * 生成统计页面折线图的图例信息
	 * 
	 * @param channelRecordList
	 * @param nameList
	 * @param lang
	 */
	private void transferName(List<ChannelRecord> channelRecordList, List<String> nameList, String lang) {
		if (channelRecordList != null) {
			for (ChannelRecord channelRecord : channelRecordList) {
				nameList.add(channelRecord.getChannelName());
			}
		}
		String legendAllName = translateMessage(lang, LEGEND_ALL_DATA, I18N_RESOURCE);
		String legendNoChannelName = translateMessage(lang, LEGEND_NO_CHANNEL_DATA, I18N_RESOURCE);
		nameList.add(legendAllName);
		nameList.add(legendNoChannelName);
	}

	/**
	 * @param step
	 * @param sourceType
	 * @param pageId
	 * @param goodsId
	 */
	private void buildByChannelType(SelectWhereStep<?> select, Byte sourceType, Integer pageId, Integer goodsId) {
		if (ChannelConstant.SOURCETYPE_CUSTOMIZE.equals(sourceType)) {
			select.where(CHANNEL_STATISTICAL.PAGE_ID.eq(pageId));
		} else {
			select.where(CHANNEL_STATISTICAL.GOODS_ID.eq(goodsId));
		}
	}

	public ChannelStatisticalRecord selectRecord(Byte channelSourceType, Date date, Integer pageId, Integer goodsId) {
		SelectWhereStep<ChannelStatisticalRecord> select = db().selectFrom(CHANNEL_STATISTICAL);
		buildByChannelType(select, channelSourceType, pageId, goodsId);
		ChannelStatisticalRecord statisticalRecord = select.where(CHANNEL_STATISTICAL.REF_DATE.eq(date)).fetchAny();
		return statisticalRecord;
	}

	private List<Date> dateRange(Date start, Date end) {
		if (start.after(end)) {
			return Collections.emptyList();
		}
		List<Date> range = new LinkedList<>();
		Calendar calendar = Calendar.getInstance();
		while (true) {
			if (!end.before(start)) {
				range.add(new Date(start.getTime()));
				calendar.setTime(start);
				calendar.add(Calendar.DATE, 1);
				start = new Date(calendar.getTimeInMillis());
			} else {
				break;
			}
		}
		return range;
	}

	private static Date toSqlDate(java.util.Date date) {
		return new Date(date.getTime());
	}

	private Date dateOffsetDay(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		return new Date(calendar.getTimeInMillis());
	}

	private Date nowDate() {
		return new Date(System.currentTimeMillis());
	}

	private Date yesterDate() {
		return dateOffsetDay(nowDate(), YESTERDAY_OFFSET);
	}

	private int valueOfIndex(String valueString, int index, String separator) {
		if (valueString == null) {
			return 0;
		}
		String[] split = valueString.split(separator);
		if (split.length <= index) {
			return 0;
		}
		String string = split[index];
		return Integer.parseInt(string);
	}

	private Map<Integer, Integer> convertJson2Map(String json) {
		if (json == null) {
			return Collections.emptyMap();
		}
		Map<String, Integer> pvMap = Util.parseJson(json, new TypeReference<Map<String, Integer>>() {
		});
		if (pvMap == null) {
			return Collections.emptyMap();
		}
		Map<Integer, Integer> resultMap = new HashMap<>(pvMap.size());
		for (String s : pvMap.keySet()) {
			resultMap.put(Integer.parseInt(s), pvMap.get(s));
		}
		return resultMap;
	}
}
