package com.meidianyi.shop.service.shop.market.live;

import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.LiveGoods.LIVE_GOODS;
import static com.meidianyi.shop.db.shop.tables.Sort.SORT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.db.shop.tables.records.GoodsBrandRecord;
import com.meidianyi.shop.db.shop.tables.records.LiveGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.SortRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.goods.recommend.GoodsLabelsVo;
import com.meidianyi.shop.service.pojo.shop.market.live.LiveRomeGoodListVo;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.shop.goods.GoodsBrandService;
import com.meidianyi.shop.service.shop.goods.mp.GoodsMpService;
import com.meidianyi.shop.service.wechat.bean.open.WxMaLiveRoomInfoGoods;

/**
 * 直播商品
 *
 * @author zhaojianqiang
 * @time 下午4:16:31
 */
@Service
public class LiveGoodsService extends ShopBaseService {

	@Autowired
	private GoodsBrandService goodsBrandService;

	@Autowired
	private GoodsMpService goodsMp;

	private static final Byte ONE = 1;
	private static final Byte ZERO = 0;

	/**
	 * 获得直播间商品列表
	 *
	 * @param id
	 * @return
	 */
	public List<LiveRomeGoodListVo> getRoomGoodsList(Integer id) {
		List<LiveRomeGoodListVo> list = db().select(LIVE_GOODS.fields())
				.select(GOODS.GOODS_NAME, GOODS.GOODS_SN, GOODS.SHOP_PRICE, GOODS.GOODS_NUMBER, GOODS.SORT_ID,
						GOODS.BRAND_ID, GOODS.GOODS_IMG)
				.from(LIVE_GOODS).leftJoin(GOODS).on(LIVE_GOODS.GOODS_ID.eq(GOODS.GOODS_ID))
				.where(LIVE_GOODS.LIVE_ID.eq(id).and(LIVE_GOODS.DEL_FLAG.eq(ZERO))).fetchInto(LiveRomeGoodListVo.class);
		return list;
	}

	/**
	 * 商品列表
	 *
	 * @param id
	 * @return
	 */
	public List<LiveRomeGoodListVo> packageGoodsList(Integer id) {
		List<LiveRomeGoodListVo> goodsList = getRoomGoodsList(id);
		for (LiveRomeGoodListVo goods : goodsList) {
			if (goods.getGoodsId() == null) {
				continue;
			}
			if (goods.getSortId() != null) {
				SortRecord sortRecord = db().selectFrom(SORT).where(SORT.SORT_ID.eq(goods.getSortId())).fetchAny();
				goods.setSortName(sortRecord==null?null:sortRecord.getSortName());
			}
			if (goods.getBrandId() != null) {
				GoodsBrandRecord brand = goodsBrandService.getBrandById(goods.getBrandId());
				goods.setBrandName(brand==null?null:brand.getBrandName());
			}
			List<GoodsLabelsVo> goodsTag = goodsMp.mpGoodsRecommendService.getGoodsLabelsByGoods(goods.getGoodsId(), 5,
					ONE);
			goods.setGoodsTag(goodsTag);
		}
		return goodsList;
	}

	/**
	 * 获取商品列表数量
	 *
	 * @param id
	 * @return
	 */
	public int getPackageGoodsListNum(Integer id) {
		List<LiveRomeGoodListVo> goodsList = getRoomGoodsList(id);
		return goodsList.size();
	}

	/**
	 * 获得加购总数
	 *
	 * @param roomId
	 * @param goodsId
	 * @return
	 */
	public Integer getAddCartNum(Integer roomId, Integer goodsId) {
		SelectConditionStep<Record1<BigDecimal>> where = db().select(DSL.sum(LIVE_GOODS.ADD_CART_NUM)).from(LIVE_GOODS)
				.where(LIVE_GOODS.ROOM_ID.eq(roomId));
		if (goodsId != null) {
			where.and(LIVE_GOODS.GOODS_ID.eq(goodsId));
		}
		return where.fetchAnyInto(Integer.class);
	}

	/**
	 * 创建直播间商品
	 * @param liveId
	 * @param roomId
	 * @param goodsList
	 * @return
	 */
	public List<Integer> addRoomGoods(Integer liveId,Integer roomId,List<WxMaLiveRoomInfoGoods> goodsList ) {
		logger().info("liveid:{};roomId:{}",liveId,roomId);
		List<Integer> successGoodIds=new ArrayList<Integer>();
		for (WxMaLiveRoomInfoGoods goods : goodsList) {
			LiveGoodsRecord goodsData = db().newRecord(LIVE_GOODS,goods);
			goodsData.setLiveId(liveId);
			goodsData.setRoomId(roomId);
			goodsData.setPrice(toYuan(goods.getPrice()));
			goodsData.setPriceEnd(toYuan(goods.getPrice2()));
			goodsData.setGoodsId(parseGoodsId(goods.getUrl()));
			LiveGoodsRecord roomGoodsInfo = getRoomGoodsInfo(roomId, liveId, goods.getUrl());
			if(roomGoodsInfo!=null) {
				goodsData.setId(roomGoodsInfo.getId());
				int update = goodsData.update();
				logger().info("房间：{}，更新商品id：{}，结果：{}",roomId,goodsData.getGoodsId(), update);
				if(update>0) {
					successGoodIds.add(goodsData.getId());
				}
			}else {
				int insert = goodsData.insert();
				logger().info("房间：{}，插入商品id：{}，结果：{}",roomId,goodsData.getGoodsId(), insert);
				if(insert>0) {
					successGoodIds.add(goodsData.getId());
				}
			}
		}

		if(!successGoodIds.isEmpty()) {
			int execute = db().update(LIVE_GOODS).set(LIVE_GOODS.DEL_FLAG, ONE).set(LIVE_GOODS.DEL_TIME, DateUtils.getSqlTimestamp())
					.where(LIVE_GOODS.ID.notIn(successGoodIds).and(LIVE_GOODS.ROOM_ID.eq(roomId).and(LIVE_GOODS.DEL_FLAG.eq(ZERO)))).execute();
			logger().info("更新失效的商品：{}",execute);
		}
		return successGoodIds;
	}



	public LiveGoodsRecord getRoomGoodsInfo(Integer roomId, Integer liveId, String url) {
		return db().selectFrom(LIVE_GOODS)
				.where(LIVE_GOODS.LIVE_ID.eq(liveId).and(LIVE_GOODS.ROOM_ID.eq(roomId).and(LIVE_GOODS.URL.eq(url))))
				.fetchAny();
	}

	/**
	 * 根据地址反找商品id
	 * @param url
	 * @return
	 */
	public Integer parseGoodsId(String url) {
		// pages/item/item?gid=204&aid=&atp=
		if (StringUtils.isEmpty(url)) {
			logger().info("没有1：{}", url);
			return 0;
		}
		boolean isHasIntegral = url.contains(QrCodeTypeEnum.INTEGRAL_ITEM_INFO.getUrl());
		boolean isHasPresale = url.contains(QrCodeTypeEnum.DOWN_PAYMENT_INFO.getUrl());
		Map<String, String> param = getParam(url);
		String gidString = param.get("gid");
		String intGidString = param.get("intGId");
		if ((!isHasPresale) && StringUtils.isNotEmpty(gidString)) {
			// 普通商品
			logger().info("商品id：{}", gidString);
			return Integer.valueOf(gidString);
		}
		if (isHasIntegral && StringUtils.isNotEmpty(intGidString)) {
			// 积分商品详情
			logger().info("积分商品商品id：{}", intGidString);
			return Integer.valueOf(intGidString);
		}

		if (isHasPresale && StringUtils.isNotEmpty(gidString)) {
			logger().info("定金膨胀商品id：{}", gidString);
			return Integer.valueOf(gidString);
		}
		logger().info("没有2：{}", url);
		return 0;
	}

	/**
	 * 对url进行拆解
	 * @param url
	 * @return
	 */
	public Map<String, String> getParam(String url) {
	    String params = url.substring(url.indexOf("?") + 1, url.length());
	    Map<String, String> split = Splitter.on("&").withKeyValueSeparator("=").split(params);
	    return split;
	}

	/**
	 * 金额：分转元
	 * @param money
	 * @return
	 */
	private BigDecimal toYuan(BigDecimal money) {
		return money.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
	}
	/**
	 * 增加直播间商品加购数
	 * @param roomId
	 * @param goodsId
	 */
	public void incrementAddCartNum(Integer roomId, Integer goodsId) {
		int execute = db().update(LIVE_GOODS).set(LIVE_GOODS.ADD_CART_NUM, LIVE_GOODS.ADD_CART_NUM.add(1))
				.where(LIVE_GOODS.ROOM_ID.eq(roomId).and(LIVE_GOODS.GOODS_ID.eq(goodsId))).execute();
		logger().info("直播加购商品id：{}，房间号：{}，结果：{}",goodsId,roomId,execute);
	}
}
