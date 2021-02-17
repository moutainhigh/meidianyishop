package com.meidianyi.shop.service.shop.market.live;

import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.pojo.shop.base.BasePageParam;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.shop.tables.records.LiveBroadcastRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.live.LiveListParam;
import com.meidianyi.shop.service.pojo.shop.market.live.LiveListVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.goods.detail.live.RoomDetailMpVo;
import com.meidianyi.shop.service.wechat.api.WxMaLiveService;
import com.meidianyi.shop.service.wechat.bean.open.WxMaLiveInfoResult;
import com.meidianyi.shop.service.wechat.bean.open.WxMaLiveRoomInfo;
import com.meidianyi.shop.service.wechat.bean.open.WxMaLiveRoomInfoGoods;
import me.chanjar.weixin.common.error.WxErrorException;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.meidianyi.shop.db.shop.tables.LiveBroadcast.LIVE_BROADCAST;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;


/**
 * 直播
 *
 * @author zhaojianqiang
 * @time 下午3:42:02
 */
@Service
public class LiveService extends ShopBaseService {


	@Autowired
	public LiveGoodsService liveGoods;

	private static final Byte ONE = 1;

	private static final Byte ZERO = 0;
	/** 直播中 */
	private static final int LIVING_ON = 101;
    /** 直播未开始 */
	private static final int LIVING_NOT_START= 102;
    /** 直播已结束 */
	private static final int LIVING_END = 103;
	/**禁播*/
	private static final int LIVING_FORBIDDEN = 104;
    /** 直播暂停中 */
	private static final int LIVING_PAUSE = 105;
    /** 直播异常 */
	private static final int LIVING_EXCEPTION = 106 ;
	/** 直播已过期*/
    private static final int LIVING_OUT_OF_DATE = 107;

	/**
	 * 直播列表页
	 * @param param
	 * @return
	 */
	public PageResult<LiveListVo> getPageList(LiveListParam param) {
		SelectConditionStep<LiveBroadcastRecord> selectFrom = db().selectFrom(LIVE_BROADCAST)
				.where(LIVE_BROADCAST.ID.gt(0));
		return optionsBuilder(param, selectFrom);
	}

	private PageResult<LiveListVo> optionsBuilder(LiveListParam param, SelectConditionStep<LiveBroadcastRecord> selectFrom) {
		if (!StringUtils.isEmpty(param.getName())) {
			selectFrom.and(LIVE_BROADCAST.NAME.like(likeValue(param.getName())));
		}
		if (!StringUtils.isEmpty(param.getAnchorName())) {
			selectFrom.and(LIVE_BROADCAST.ANCHOR_NAME.like(likeValue(param.getAnchorName())));
		}
		if (param.getLiveStatus() > 0) {
			selectFrom.and(LIVE_BROADCAST.LIVE_STATUS.eq(param.getLiveStatus()));
		}
		if (param.getBeginStartTime() != null) {
			selectFrom.and(LIVE_BROADCAST.START_TIME.ge(param.getBeginStartTime()));
		}
		if (param.getBeginEndTime() != null) {
			selectFrom.and(LIVE_BROADCAST.START_TIME.lt(param.getBeginEndTime()));
		}

		if (param.getFinishStartTime() != null) {
			selectFrom.and(LIVE_BROADCAST.END_TIME.ge(param.getFinishStartTime()));
		}
		if (param.getFinishEndTime() != null) {
			selectFrom.and(LIVE_BROADCAST.END_TIME.lt(param.getFinishEndTime()));
		}
		selectFrom.orderBy(LIVE_BROADCAST.ROOM_ID.desc());
		return this.getPageResult(selectFrom, param.getCurrentPage(), param.getPageRows(), LiveListVo.class);
	}


	/**
	 * 授权后的列表
	 * @param param
	 * @return
	 */
	public PageResult<LiveListVo> getList(LiveListParam param) {
		getLiveList();
		PageResult<LiveListVo> pageList = getPageList(param);
		List<LiveListVo> dataList = pageList.getDataList();
		for (LiveListVo liveListVo : dataList) {
			liveListVo.setGoodsListNum(liveGoods.getPackageGoodsListNum(liveListVo.getId()));
			liveListVo.getLiveStatus();
			liveListVo.setAddCartNum(liveGoods.getAddCartNum(liveListVo.getRoomId(), null));
			liveListVo.setOrderNum(getOrderNum(liveListVo.getRoomId()));
		}
		return pageList;
	}

    /**
     * 商品列表处-获取直播间信息
     * @param pageParam 分页搜索信息
     * @return
     */
    public PageResult<LiveListVo> getListForGoodsEdit(BasePageParam pageParam) {
        SelectSeekStep1<LiveBroadcastRecord, Integer> liveBroadcastRecords = db().selectFrom(LIVE_BROADCAST)
            .where(LIVE_BROADCAST.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(LIVE_BROADCAST.LIVE_STATUS.in(Arrays.asList(LIVING_ON, LIVING_NOT_START, LIVING_END, LIVING_PAUSE, LIVING_EXCEPTION))))
            .orderBy(LIVE_BROADCAST.ROOM_ID.desc());
        return this.getPageResult(liveBroadcastRecords, pageParam.getCurrentPage(), pageParam.getPageRows(), LiveListVo.class);
    }

    /**
     * 根据直播间id获取直播信息
     * @param roomId 直播间id
     * @return null 表示无效id
     */
    public LiveBroadcastRecord getLiveInfoByRoomId(Integer roomId) {
        return db().selectFrom(LIVE_BROADCAST)
            .where(LIVE_BROADCAST.ROOM_ID.eq(roomId).and(LIVE_BROADCAST.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)))
            .fetchAny();
    }

    /**
     * 根据roomId获取直播信息(未结束、未禁播、未过期、未删除的)
     * @param roomId 直播id
     * @return 直播信息
     */
    public RoomDetailMpVo getLiveInfoForGoodsMpDetail(Integer roomId) {
        LiveBroadcastRecord liveBroadcastRecord = db().selectFrom(LIVE_BROADCAST)
            .where(LIVE_BROADCAST.ROOM_ID.eq(roomId).and(LIVE_BROADCAST.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .and(LIVE_BROADCAST.LIVE_STATUS.notIn(Arrays.asList(LIVING_END, LIVING_FORBIDDEN,LIVING_OUT_OF_DATE))).and(LIVE_BROADCAST.END_TIME.gt(DateUtils.getLocalDateTime())))
            .fetchAny();
        if (liveBroadcastRecord == null) {
            return null;
        }
        return liveBroadcastRecord.into(RoomDetailMpVo.class);
    }
	/**
	 * 获得订单数
	 * @param roomId
	 * @return
	 */
	public Integer getOrderNum(Integer roomId) {
		return db().select(DSL.count()).from(ORDER_INFO).where(ORDER_INFO.ROOM_ID.eq(roomId))
				.fetchAnyInto(Integer.class);
	}


	/**
	 * 【获取直播房间列表】接口，仅供后台调用
	 * @param appId
	 * @param start 起始拉取房间，start = 0 表示从第 1 个房间开始拉取
	 * @param limit 每次拉取的个数上限，不要设置过大，建议 100 以内
	 * @return
	 */
	public WxMaLiveInfoResult getliveinfo(String appId,Integer start,Integer limit) {
		WxMaLiveService service = open.getMaExtService();
		WxMaLiveInfoResult liveInfo=null;
		try {
			 liveInfo = service.getLiveInfo(appId, start, limit);
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
		logger().info("小程序：{}，直播列表为：{}",appId,liveInfo.toString());
		return liveInfo;
	}




	/**
	 *
	 * 获取所有的直播列表
	 * @return
	 */
	public List<WxMaLiveRoomInfo> getliveinfo() {
		MpAuthShopRecord mpAuthShop = saas.shop.mp.getAuthShopByShopId(getShopId());
		List<WxMaLiveRoomInfo> data=new ArrayList<WxMaLiveRoomInfo>();
		if (mpAuthShop == null) {
			 return data;
		}
		String appId = mpAuthShop.getAppId();
		int start = 0;
		int limit = 50;
		while (true) {
			WxMaLiveInfoResult result = getliveinfo(appId, start, limit);
			if (!result.isSuccess()) {
				break;
			}
			List<WxMaLiveRoomInfo> roomInfo = result.getRoomInfo();
			data.addAll(roomInfo);
			Integer total = result.getTotal() == null ? 0 : result.getTotal();
			if ((start + 1) * limit >= total) {
				break;
			}
			start += 1;
		}
		return data;
	}


	/**
	 * 获取所有的直播到数据库中
	 * @return
	 */
	public boolean getLiveList() {
		logger().info("店铺获取直播：{}的开始",getShopId());
		List<WxMaLiveRoomInfo> list = getliveinfo();
		if(list.isEmpty()) {
			logger().info("店铺：{}，当前无直播",getShopId());
			return false;
		}
		List<Integer> successLive=new ArrayList<Integer>();
		for (WxMaLiveRoomInfo live : list) {
			LiveBroadcastRecord record = db().newRecord(LIVE_BROADCAST,live);
			record.setRoomId(live.getRoomid());
			record.setStartTime(new Timestamp(live.getStartTime()*1000));
			record.setEndTime(new Timestamp(live.getEndTime()*1000));
			record.setAnchorImg(StringUtils.isEmpty(live.getAnchorImg()) ? live.getShareImg() : live.getAnchorImg());
			LiveBroadcastRecord roomInfo = db().selectFrom(LIVE_BROADCAST).where(LIVE_BROADCAST.ROOM_ID.eq(live.getRoomid())).fetchAny();
			if(roomInfo!=null) {
				record.setId(roomInfo.getId());
				int update = record.update();
				logger().info("更新直播房间：{}，结果：{}",live.getRoomid(),update);
				if(update>0) {
					successLive.add(record.getId());
				}

			}else {
				int insert = record.insert();
				logger().info("新增直播房间：{}，结果：{}",live.getRoomid(),insert);
				if(insert>0) {
					successLive.add(record.getId());
				}
			}
			List<WxMaLiveRoomInfoGoods> goods = live.getGoods();
			if(!goods.isEmpty()) {
				liveGoods.addRoomGoods(record.getId(), live.getRoomid(), live.getGoods());
			}
		}
		if(!successLive.isEmpty()) {
			int execute = db().update(LIVE_BROADCAST).set(LIVE_BROADCAST.DEL_FLAG, ONE)
					.set(LIVE_BROADCAST.DEL_TIME, DateUtils.getSqlTimestamp())
					.where(LIVE_BROADCAST.ID.notIn(successLive).and(LIVE_BROADCAST.DEL_FLAG.eq(ZERO))).execute();
			logger().info("更新其他直播为失效：{}",execute);
		}
		logger().info("店铺获取直播：{}的结束",getShopId());
		return true;
	}



















}
