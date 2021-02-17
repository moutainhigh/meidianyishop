package com.meidianyi.shop.service.shop.goods;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Page;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.db.shop.Tables;
import com.meidianyi.shop.db.shop.tables.records.FootprintRecordRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.config.ShowCartConfig;
import com.meidianyi.shop.service.pojo.wxapp.footprint.FootprintDayVo;
import com.meidianyi.shop.service.pojo.wxapp.footprint.FootprintListVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.GoodsBaseMp;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.list.GoodsListMpVo;
import com.meidianyi.shop.service.shop.activity.factory.ProcessorFactoryBuilder;
import com.meidianyi.shop.service.shop.config.ConfigService;
import com.meidianyi.shop.service.shop.goods.mp.GoodsMpService;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.GOODS;
import static com.meidianyi.shop.db.shop.tables.FootprintRecord.FOOTPRINT_RECORD;

/**
 * 足迹
 *
 * @author zhaojianqiang,kdc
 *
 *         2019年10月12日 下午3:08:53
 */
@Service
public class FootPrintService extends ShopBaseService {

	@Autowired
	private GoodsService goodsService;
	@Autowired
	private GoodsMpService goodsMpService;
	@Autowired
	ProcessorFactoryBuilder processorFactoryBuilder;
	@Autowired
	public ConfigService configService;
	/**
	 * 获得用户足迹数
	 * @param userId
	 * @return
	 */
	public Integer getfootPrintNum(Integer userId) {
		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().minusMonths(3L));
		return db().selectCount().from(FOOTPRINT_RECORD)
				.where(FOOTPRINT_RECORD.USER_ID.eq(userId).and(FOOTPRINT_RECORD.UPDATE_TIME.gt(timestamp))).fetchOne()
				.into(Integer.class);
	}


	/**
	 * 添加足迹记录
	 * @param userId 用户id
	 * @param goodsId 商品id
	 *
	 */
	public void addFootprint(Integer userId,Integer goodsId){
		String nowDate = DateUtils.getLocalDateTime().toString().substring(0,10);

		Integer count = db().selectCount().from(Tables.FOOTPRINT_RECORD)
				.where(Tables.FOOTPRINT_RECORD.USER_ID.eq(userId)).and(Tables.FOOTPRINT_RECORD.GOODS_ID.eq(goodsId))
				.and(DslPlus.dateFormatDay(Tables.FOOTPRINT_RECORD.CREATE_TIME).eq(nowDate)).fetchOne().component1();
		if (count!=null&&count>0){
			db().update(Tables.FOOTPRINT_RECORD).set(Tables.FOOTPRINT_RECORD.COUNT, Tables.FOOTPRINT_RECORD.COUNT.add(1))
					.where(Tables.FOOTPRINT_RECORD.USER_ID.eq(userId))
					.and(Tables.FOOTPRINT_RECORD.GOODS_ID.eq(goodsId)).and(DslPlus.dateFormatDay(Tables.FOOTPRINT_RECORD.CREATE_TIME).eq(nowDate)).execute();
		}else {
			FootprintRecordRecord footprint = db().newRecord(Tables.FOOTPRINT_RECORD);
			footprint.setCount(1);
			footprint.setUserId(userId);
			footprint.setGoodsId(goodsId);
			footprint.insert();
		}
	}

	/**
	 * 获取足迹记录
	 * @param userId  用户id1
	 * @param keyWord 关键字
	 * @param currentPage 翻页信息
	 * @param pageRows
	 * @return FootprintListVo
	 */
	public FootprintListVo getFootprintPage(Integer userId, String keyWord, Integer currentPage, Integer pageRows){
		FootprintListVo footprintListVo =new FootprintListVo();
		List<FootprintDayVo> footprintDaylist =new ArrayList<>();
		footprintListVo.setDay(footprintDaylist);
		Timestamp timestamp = DateUtils.getTimeStampPlus(-3, ChronoUnit.MONTHS);
		SelectConditionStep<? extends Record> select = db().select(GOODS.GOODS_ID, DslPlus.dateFormatDay(FOOTPRINT_RECORD.CREATE_TIME).as("date"))
				.from(FOOTPRINT_RECORD)
				.leftJoin(GOODS).on(GOODS.GOODS_ID.eq(FOOTPRINT_RECORD.GOODS_ID))
				.where(FOOTPRINT_RECORD.USER_ID.eq(userId))
				.and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
				.and(FOOTPRINT_RECORD.CREATE_TIME.gt(timestamp));
		if (!StringUtils.isBlank(keyWord)){
			select.and(GOODS.GOODS_NAME.like(likeValue(keyWord)));
		}
		// 总页数
		Integer totalRows = db().fetchCount(select);
		Page page = Page.getPage(totalRows, currentPage, pageRows);
		footprintListVo.setPage(page);
		Result<? extends Record> records = select.orderBy(FOOTPRINT_RECORD.CREATE_TIME.desc()).limit((currentPage - 1)*pageRows, pageRows).fetch();
		List<Integer> goodsIdList = Arrays.asList(records.intoArray(GOODS.GOODS_ID));
		List<FootprintDayVo> footprintList =records.into(FootprintDayVo.class);
        List<? extends GoodsListMpVo> goodsListMpVos = goodsMpService.getGoodsListNormal(goodsIdList, userId,null,null);
		Map<Integer, GoodsListMpVo> goodsListMpVoMap = goodsListMpVos.stream().collect(Collectors.toMap(GoodsListMpVo::getGoodsId, goods->goods));
		footprintList.forEach(footprintGoods->{
			GoodsListMpVo goodsListMpVo = goodsListMpVoMap.get(footprintGoods.getGoodsId());
			footprintGoods.getGoodsList().add(goodsListMpVo);
		});
		byDateGroup(footprintList,footprintDaylist);
		//是否显示划线价开关
		Byte delMarket = configService.shopCommonConfigService.getDelMarket();
		//是否显示购买按钮
		ShowCartConfig showCart = configService.shopCommonConfigService.getShowCart();
		footprintListVo.setShowCart(showCart);
		footprintListVo.setDelMarket(delMarket);
        return footprintListVo;
	}

	/**
	 * 按照日期分类
	 * @param footprintList
	 * @param footprintDaylist
	 */
	public void byDateGroup(List<FootprintDayVo> footprintList,List<FootprintDayVo> footprintDaylist){
		// 安装日期分组
		footprintList.forEach(footprint->{
			FootprintDayVo footprintDay = null;
			for (FootprintDayVo f : footprintDaylist) {
				if (f.getDate().equals(footprint.getDate())) {
					footprintDay = f;
					break;
				}
			}
			if (footprintDay==null){
				footprintDay =new FootprintDayVo();
				footprintDay.setDate(footprint.getDate());
				footprintDaylist.add(footprintDay);
			}
			footprintDay.getGoodsList().addAll(footprint.getGoodsList());
		});
		//去重
		footprintDaylist.forEach(footprintDay->{
			if (footprintDay.getGoodsList()!=null&&footprintDay.getGoodsList().size()>1){
				ArrayList<GoodsListMpVo> collect1 = footprintDay.getGoodsList().stream().filter(Objects::nonNull).collect(
						Collectors.collectingAndThen(
								Collectors.toCollection(() ->
										new TreeSet<GoodsListMpVo>(Comparator.comparing(GoodsBaseMp::getGoodsName))
								), ArrayList::new
						)
				);
				footprintDay.setGoodsList(collect1);
			}
		});
	}

}
