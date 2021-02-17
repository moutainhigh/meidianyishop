package com.meidianyi.shop.service.shop.market.groupdraw;

import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_STATUS_DISABLE;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_STATUS_NORMAL;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.NAVBAR_TYPE_DISABLED;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.NAVBAR_TYPE_FINISHED;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.NAVBAR_TYPE_NOT_STARTED;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.NAVBAR_TYPE_ONGOING;
import static com.meidianyi.shop.common.foundation.util.Util.currentTimeStamp;
import static com.meidianyi.shop.common.foundation.util.Util.listToString;
import static com.meidianyi.shop.common.foundation.util.Util.stringToList;
import static com.meidianyi.shop.db.shop.tables.Goods.GOODS;
import static com.meidianyi.shop.db.shop.tables.GoodsSpecProduct.GOODS_SPEC_PRODUCT;
import static com.meidianyi.shop.db.shop.tables.GroupDraw.GROUP_DRAW;
import static com.meidianyi.shop.db.shop.tables.JoinDrawList.JOIN_DRAW_LIST;
import static com.meidianyi.shop.db.shop.tables.JoinGroupList.JOIN_GROUP_LIST;
import static com.meidianyi.shop.db.shop.tables.OrderGoods.ORDER_GOODS;
import static com.meidianyi.shop.db.shop.tables.OrderInfo.ORDER_INFO;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.db.shop.tables.UserDetail.USER_DETAIL;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.substring;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Record;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.shop.tables.records.GoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.GoodsSpecProductRecord;
import com.meidianyi.shop.db.shop.tables.records.GroupDrawInviteRecord;
import com.meidianyi.shop.db.shop.tables.records.GroupDrawRecord;
import com.meidianyi.shop.db.shop.tables.records.JoinDrawListRecord;
import com.meidianyi.shop.db.shop.tables.records.JoinGroupListRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderGoodsRecord;
import com.meidianyi.shop.db.shop.tables.records.OrderInfoRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleGroupDraw;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsNumCountParam;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsSmallVo;
import com.meidianyi.shop.service.pojo.shop.goods.goods.GoodsView;
import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.GroupDrawActCopywriting;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.GroupDrawAddParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.GroupDrawListParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.GroupDrawListVo;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.GroupDrawShareParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.GroupDrawUpdateParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.analysis.GroupDrawAnalysisInfo;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.analysis.GroupDrawAnalysisListVo;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.analysis.GroupDrawAnalysisParam;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.analysis.GroupDrawAnalysisStatus;
import com.meidianyi.shop.service.pojo.shop.market.groupdraw.analysis.GroupDrawAnalysisVo;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.DrawUser;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawBotton;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawInfoByOsVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawInfoParam;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawInfoReturnVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawInfoVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawList;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawReturn;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupDrawVo;
import com.meidianyi.shop.service.pojo.wxapp.goods.groupdraw.GroupJoinDetailVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderBeforeParam.Goods;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.image.ImageService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory;

import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 拼团抽奖
 *
 * @author 郑保乐
 */
@Service
@Slf4j
public class GroupDrawService extends ShopBaseService {

	@Autowired
	public GroupDrawJoinUserService groupDrawUsers;
	@Autowired
	public GroupDrawOrderService groupDrawOrders;
	@Autowired
	public GroupDrawGroupService groupDrawGroups;
	@Autowired
	public GroupDrawInviteService groupDrawInvite;
	@Autowired
	public GroupDrawUserService groupDrawUser;

	@Autowired
	public DomainConfig domainConfig;

	private final QrCodeService qrCode;

	@Autowired
	private ImageService imageService;
	@Autowired
	private OrderInfoService orderInfoService;

	@Autowired
	private GoodsService goodsService;

	public GroupDrawService(QrCodeService qrCode) {
		this.qrCode = qrCode;
	}

	private static final byte ZERO = 0;
	private static final byte ONE = 1;
	private static final byte TWO = 2;
	private static final byte THREE = 3;
	private static final byte FOUR = 4;
	private static final byte[] LIST = { 2, 12, 13 };

	/**
	 * 获取小程序码
	 */
	public ShareQrCodeVo getMpQrCode(GroupDrawShareParam param) {
		Integer groupDrawId = param.getGroupDrawId();
		String pathParam = "group_draw_id=" + groupDrawId;
		logger().info("path为：{}",pathParam);
		String imageUrl = qrCode.getMpQrCode(QrCodeTypeEnum.PIN_LOTTERY, pathParam);
		ShareQrCodeVo vo = new ShareQrCodeVo();
		vo.setImageUrl(imageUrl);
		vo.setPagePath(QrCodeTypeEnum.PIN_LOTTERY.getPathUrl(pathParam));
		return vo;
	}

	/**
	 * 停用活动
	 */
	public void disableGroupDraw(Integer id) {
		int result = db().update(GROUP_DRAW).set(GROUP_DRAW.STATUS, ACTIVITY_STATUS_DISABLE)
				.where(GROUP_DRAW.ID.eq(id).and(GROUP_DRAW.STATUS.ne(ACTIVITY_STATUS_DISABLE))).execute();
		if (0 == result) {
			throw new IllegalStateException("Invalid group draw id or it has already been disabled.");
		}
	}
    /**
     * 启用活动
     */
    public void enableGroupDraw(Integer id) {
        int result = db().update(GROUP_DRAW).set(GROUP_DRAW.STATUS, ACTIVITY_STATUS_NORMAL)
            .where(GROUP_DRAW.ID.eq(id).and(GROUP_DRAW.STATUS.ne(ACTIVITY_STATUS_NORMAL))).execute();
        if (0 == result) {
            throw new IllegalStateException("Invalid group draw id or it has already been enabled.");
        }
    }
	/**
	 * 更新活动
	 */
	public void updateGroupDraw(GroupDrawUpdateParam param) {
        String actCopywriting = Util.toJsonNotNull(param.getActCopywriting());
        param.setActivityCopywriting(actCopywriting);
		db().update(GROUP_DRAW).set(GROUP_DRAW.NAME, param.getName()).set(GROUP_DRAW.START_TIME, param.getStartTime())
				.set(GROUP_DRAW.END_TIME, param.getEndTime()).set(GROUP_DRAW.JOIN_LIMIT, param.getJoinLimit())
				.set(GROUP_DRAW.LIMIT_AMOUNT, param.getLimitAmount()).set(GROUP_DRAW.OPEN_LIMIT, param.getOpenLimit())
				.set(GROUP_DRAW.MIN_JOIN_NUM, param.getMinJoinNum()).set(GROUP_DRAW.PAY_MONEY, param.getPayMoney())
				.set(GROUP_DRAW.TO_NUM_SHOW, param.getToNumShow()).set(GROUP_DRAW.ACTIVITY_COPYWRITING, param.getActivityCopywriting())
            .where(GROUP_DRAW.ID.eq(param.getId())).execute();
	}

	/**
	 * 活动明细
	 */
	public GroupDrawListVo getGroupDrawById(Integer id) {
		GroupDrawListParam param = new GroupDrawListParam();
		param.setId(id);
		GroupDrawListVo vo = db().selectFrom(GROUP_DRAW).where(GROUP_DRAW.ID.eq(id)).fetchAnyInto(GroupDrawListVo.class);
		transformStatus(vo);
		return vo;
	}

	/**
	 * 列表查询
	 */
	public PageResult<GroupDrawListVo> getGroupDrawList(GroupDrawListParam param) {
		SelectConditionStep<GroupDrawRecord> select = db().selectFrom(GROUP_DRAW).where(GROUP_DRAW.DEL_FLAG.eq(ZERO));
		buildOptions(select, param);
		PageResult<GroupDrawListVo> result = getPageResult(select, param.getCurrentPage(),param.getPageRows(), GroupDrawListVo.class);
		List<GroupDrawListVo> dataList = result.getDataList();
		for (GroupDrawListVo vo : dataList) {
			Integer id = vo.getId();
			vo.setJoinUserCount(groupDrawUsers.getJoinGroupNumByGroupDraw(id));
			vo.setGroupUserCount(groupDrawUser.getSuccessGroupUserNum(id));
			vo.setGroupCount(groupDrawUser.getOpenGroupNumberById(id));
			vo.setDrawUserCount(groupDrawUser.getDrawUserNumById(id));
			GoodsNumCountParam param2=new GoodsNumCountParam();
			List<Integer> goodsIds = stringToList(vo.getGoodsId());
			param2.setGoodsIds(goodsIds);
			Integer goodsNum = goodsService.getGoodsNum(param2);
			vo.setGoodsCount(goodsNum);
			Byte status = vo.getStatus();
			Timestamp timestamp = DateUtils.getSqlTimestamp();
			vo.setIsEnable(ZERO);
			if (status.equals(ACTIVITY_STATUS_NORMAL)) {
				//1
				if (vo.getStartTime().after(timestamp)) {
					vo.setStatus(NAVBAR_TYPE_NOT_STARTED);
				} else if (vo.getStartTime().before(timestamp) && vo.getEndTime().after(timestamp)) {
					vo.setStatus(NAVBAR_TYPE_ONGOING);
				} else {
					vo.setStatus(NAVBAR_TYPE_FINISHED);
				}
			} else if (status.equals(ACTIVITY_STATUS_DISABLE)) {
				//0
				vo.setStatus(NAVBAR_TYPE_DISABLED);
				if(vo.getEndTime().after(timestamp)) {
					vo.setIsEnable(ONE);
				}
			}
		}
		return result;
	}


	/**
	 * 查询条件
	 */
	private void buildOptions(SelectConditionStep<GroupDrawRecord> select, GroupDrawListParam param) {
		String name = param.getActivityName();
		Timestamp startTime = param.getStartTime();
		Timestamp endTime = param.getEndTime();
		Byte status = param.getStatus();
		Integer id = param.getId();
		if (null != id) {
			select.and(GROUP_DRAW.ID.eq(id));
		}
		if (isNotEmpty(name)) {
			select.and(GROUP_DRAW.NAME.like(this.likeValue(name)));
		}
		if (null != startTime) {
			select.and(GROUP_DRAW.START_TIME.ge(startTime));
		}
		if (null != endTime) {
			select.and(GROUP_DRAW.END_TIME.le(endTime));
		}
		if (null != status) {
			switch (status) {
			case NAVBAR_TYPE_ONGOING:
				select.and(GROUP_DRAW.START_TIME.le(currentTimeStamp()))
						.and(GROUP_DRAW.END_TIME.ge(currentTimeStamp()));
				break;
			case NAVBAR_TYPE_NOT_STARTED:
				select.and(GROUP_DRAW.START_TIME.ge(currentTimeStamp()));
				break;
			case NAVBAR_TYPE_FINISHED:
				select.and(GROUP_DRAW.END_TIME.le(currentTimeStamp()));
				break;
			case NAVBAR_TYPE_DISABLED:
				select.and(GROUP_DRAW.STATUS.eq(ACTIVITY_STATUS_DISABLE));
				break;
			default:
			}
			if (NAVBAR_TYPE_DISABLED != status) {
				select.and(GROUP_DRAW.STATUS.eq(ACTIVITY_STATUS_NORMAL));
			}
		}else {
			select.and(GROUP_DRAW.STATUS.eq(ACTIVITY_STATUS_NORMAL).or(GROUP_DRAW.STATUS.eq(ACTIVITY_STATUS_DISABLE)));
		}
		select.orderBy(GROUP_DRAW.CREATE_TIME.desc());
	}

	/**
	 * 状态转换
	 */
	private void transformStatus(GroupDrawListVo vo) {
		Byte status = vo.getStatus();
		Timestamp startTime = vo.getStartTime();
		Timestamp endTime = vo.getEndTime();
		String goodsId = vo.getGoodsId();
		String couponId = vo.getRewardCouponId();
		// 活动状态判断
		if (status.equals(ACTIVITY_STATUS_NORMAL)) {
			if (startTime.after(currentTimeStamp())) {
				vo.setStatus(NAVBAR_TYPE_NOT_STARTED);
			} else if (startTime.before(currentTimeStamp()) && endTime.after(currentTimeStamp())) {
				vo.setStatus(NAVBAR_TYPE_ONGOING);
			} else {
				vo.setStatus(NAVBAR_TYPE_FINISHED);
			}
		} else if (status.equals(ACTIVITY_STATUS_DISABLE)) {
			vo.setStatus(NAVBAR_TYPE_DISABLED);
		}
		GoodsNumCountParam param=new GoodsNumCountParam();
		List<Integer> goodsIds = stringToList(goodsId);
		param.setGoodsIds(goodsIds);
		Integer goodsNum = goodsService.getGoodsNum(param);
		vo.setGoodsCount(goodsNum);
		vo.setGoodsIds(goodsIds);
		if (null != couponId) {
			vo.setCouponIds(stringToList(couponId));
		}
		GroupDrawActCopywriting actCopywriting = Util.json2Object(vo.getActivityCopywriting(),GroupDrawActCopywriting.class,false);
		vo.setActCopywriting(actCopywriting);
	}

	/**
	 * 添加活动
	 */
	public void addGroupDraw(GroupDrawAddParam param) {
		List<Integer> goodsIds = param.getGoodsIds();
		List<Integer> rewardCouponIds = param.getRewardCouponIds();
		if (null == goodsIds || goodsIds.isEmpty()) {
			throw new IllegalArgumentException("Goods ids is required");
		}
		if (null != rewardCouponIds && (!rewardCouponIds.isEmpty())) {
			param.setRewardCouponId(listToString(rewardCouponIds));
		}
		String actCopywriting = Util.toJsonNotNull(param.getActCopywriting());
		param.setActivityCopywriting(actCopywriting);
		param.setGoodsId(listToString(goodsIds));
		GroupDrawRecord newRecord = db().newRecord(GROUP_DRAW,param);
		newRecord.insert();
		//db().insertInto(GROUP_DRAW).set(createGroupDrawRecord(param)).execute();
	}

	/**
	 * 删除活动
	 */
	public void deleteGroupDraw(Integer id) {
		db().update(GROUP_DRAW).set(GROUP_DRAW.DEL_FLAG, (byte) 1).where(GROUP_DRAW.ID.eq(id)).execute();
	}

	/**
	 * 营销活动效果数据
	 * @param param
	 * @return
	 */
	public GroupDrawAnalysisVo getAnalysis(GroupDrawAnalysisParam param) {
		Integer actId = param.getGroupDrawId();
		Timestamp startTime = param.getStartTime();
		Timestamp endTime = param.getEndTime();
		GroupDrawListVo fetch = getGroupDrawById(actId);
		if (fetch == null) {
			return null;
		}
		if (null == startTime) {
			startTime = fetch.getStartTime();
		}
		if (null == endTime) {
			endTime = fetch.getEndTime();
			if (endTime.after(DateUtils.getLocalDateTime())) {
				// 结束日期晚于今天
				endTime = DateUtils.getLocalDateTime();
			}
		}
		return getGroupDrawInfo(actId, startTime, endTime);
	}

	public GroupDrawAnalysisVo getGroupDrawInfo(Integer actId, Timestamp startTime, Timestamp endTime) {
		GroupDrawAnalysisVo gbaVo = new GroupDrawAnalysisVo();
		gbaVo.setStartTime(startTime);
		gbaVo.setEndTime(endTime);
		SelectConditionStep<?> builder = db().select(ORDER_INFO.CREATE_TIME, ORDER_INFO.USER_ID).from(ORDER_INFO)
				.where(ORDER_INFO.ACTIVITY_ID.eq(actId))
				.and(ORDER_INFO.GOODS_TYPE.likeRegex(
						OrderInfoService.getGoodsTypeToSearch(new Byte[] { BaseConstant.ACTIVITY_TYPE_GROUP_DRAW })))
				.and(ORDER_INFO.ORDER_STATUS.notIn(new Byte[] { 0, 2 }));
		if (null != startTime && null != endTime) {
			builder.and(ORDER_INFO.CREATE_TIME.between(startTime, endTime));
		}
		List<GroupDrawAnalysisInfo> list = builder.fetchInto(GroupDrawAnalysisInfo.class);
		if (list.size() == 0) {
			logger().info("没有数据");
			return gbaVo;
		}
		String format = DateUtils.DATE_FORMAT_SIMPLE;
		for (GroupDrawAnalysisInfo pojo : list) {
			pojo.setStartDate(DateUtils.dateFormat(format, pojo.getCreateTime()));
		}
		List<GroupDrawAnalysisStatus> analysisStatus = db().select(JOIN_GROUP_LIST.STATUS, JOIN_GROUP_LIST.OPEN_TIME)
				.from(JOIN_GROUP_LIST).where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(actId))
				.and(JOIN_GROUP_LIST.STATUS.greaterOrEqual(NumberUtils.BYTE_ZERO))
				.fetchInto(GroupDrawAnalysisStatus.class);
		for (GroupDrawAnalysisStatus item : analysisStatus) {
			item.setStartDate(DateUtils.dateFormat(format, item.getOpenTime()));
		}
		byte one = 1;
		int orderNumber = 0;
		int joinNum = 0;
		int successUserNum = 0;
		int newUser = 0;
		List<String> betweenTime = DateUtils.getBetweenTime(startTime, endTime);
		List<GroupDrawAnalysisListVo> returnVo = new ArrayList<GroupDrawAnalysisListVo>();

		for (String date : betweenTime) {
			GroupDrawAnalysisListVo vo = new GroupDrawAnalysisListVo();
			vo.setDateTime(date);
			for (GroupDrawAnalysisInfo pojo : list) {
				if (pojo.getStartDate().equals(date)) {
					Integer oldOrderNumber = db().select(DSL.count(ORDER_INFO.ORDER_ID)).from(ORDER_INFO)
							.where(ORDER_INFO.USER_ID.eq(pojo.getUserId())).and(ORDER_INFO.ORDER_STATUS.gt(TWO))
							.and(ORDER_INFO.CREATE_TIME.lt(pojo.getCreateTime())).fetchOneInto(Integer.class);
					if(oldOrderNumber==0) {
						vo.setNewUser(vo.getNewUser() == 0 ? 1 : vo.getNewUser() + 1);
					}
					vo.setOrderNumber(vo.getOrderNumber() == 0 ? 1 : vo.getOrderNumber() + 1);
				}
			}
			orderNumber = orderNumber + vo.getOrderNumber();
			newUser = newUser + vo.getNewUser();
			returnVo.add(vo);
		}
		for (GroupDrawAnalysisListVo vo : returnVo) {
			for (GroupDrawAnalysisStatus item : analysisStatus) {
				if (item.getStartDate().equals(vo.getDateTime())) {
					vo.setJoinNum(vo.getJoinNum() == 0 ? 1 : vo.getJoinNum() + 1);
					if(item.getStatus().equals(one)) {
						vo.setSuccessUserNum(vo.getSuccessUserNum() == 0 ? 1 : vo.getSuccessUserNum() + 1);
					}
				}
			}
			joinNum = joinNum + vo.getJoinNum();
			successUserNum = successUserNum + vo.getSuccessUserNum();
		}
		gbaVo.setList(returnVo);
		gbaVo.setOrderNumber(orderNumber);
		gbaVo.setJoinNum(joinNum);
		gbaVo.setSuccessUserNum(successUserNum);
		gbaVo.setNewUser(newUser);
		return gbaVo;
	}

	/**
	 * map排序
	 *
	 * @param disorderMap 无序map
	 * @return key升序map
	 */
	private Map<String, Integer> sortMap(Map<String, Integer> disorderMap) {
		List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(disorderMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/**
	 * 小程序装修拼团抽奖模块显示异步调用
	 *
	 * @param moduleGroupDraw
	 * @return
	 */
	public ModuleGroupDraw getPageIndexGroupDraw(ModuleGroupDraw moduleGroupDraw) {
		GroupDrawRecord groupDraw = db().selectFrom(GROUP_DRAW)
				.where(GROUP_DRAW.ID.eq(moduleGroupDraw.getGroupDrawId())).fetchAny();
		if (groupDraw != null) {
			moduleGroupDraw.setName(groupDraw.getName());
			moduleGroupDraw.setStatus(groupDraw.getStatus());
			moduleGroupDraw.setStartTime(groupDraw.getStartTime());
			moduleGroupDraw.setEndTime(groupDraw.getEndTime());
			moduleGroupDraw.setToNumShow(groupDraw.getToNumShow());
		}

		if (groupDraw.getStatus().equals(ACTIVITY_STATUS_DISABLE)) {
			moduleGroupDraw.setState(TWO);
		} else if (groupDraw.getEndTime().before(DateUtils.getLocalDateTime())) {
			moduleGroupDraw.setState(FOUR);
		} else if (groupDraw.getStartTime().after(DateUtils.getLocalDateTime())) {
			moduleGroupDraw.setState(THREE);
		} else {
			moduleGroupDraw.setState(ZERO);
			moduleGroupDraw.setSurplusSecond(
					(groupDraw.getEndTime().getTime() - Calendar.getInstance().getTimeInMillis()) / 1000);
		}

		int joinUserNumber = groupDrawUsers.getJoinGroupNumByGroupDraw(moduleGroupDraw.getGroupDrawId());
		if (groupDraw.getToNumShow() <= joinUserNumber) {
			moduleGroupDraw.setJoinUserNum(joinUserNumber);
		}

		if (StringUtil.isNotEmpty(moduleGroupDraw.getModuleImg())) {
			moduleGroupDraw.setModuleImg(domainConfig.imageUrl(moduleGroupDraw.getModuleImg()));
		}

		return moduleGroupDraw;
	}

	/**
	 * 小程序端获取列表
	 *
	 * @param groupDrawId
	 * @return
	 */
	public GroupDrawVo groupDrawList(Integer groupDrawId) {
		GroupDrawRecord groupDraw = db().selectFrom(GROUP_DRAW).where(GROUP_DRAW.ID.eq(groupDrawId)).fetchAny();
		if (groupDraw == null) {
			// 活动不存在
			logger().info("活动不存在1");
			return null;
		}
		Timestamp endTime = groupDraw.getEndTime();
		Byte status = groupDraw.getStatus();
		Byte delFlag = groupDraw.getDelFlag();
		Timestamp nowTime = DateUtils.getLocalDateTime();
		GroupDrawInfoVo into = groupDraw.into(GroupDrawInfoVo.class);
		if (endTime.before(nowTime) || status.equals(ACTIVITY_STATUS_DISABLE) || delFlag.equals(ONE)) {
			// 活动不存在
			logger().info("活动已结束，活动不存在");
			return null;
		}
		Timestamp startTime = groupDraw.getStartTime();
		GroupDrawVo vo = new GroupDrawVo();
		if (startTime.after(nowTime)) {
			// 活动还没开始
			logger().info("活动还没开始");
		} else {
			into.setSurplusSecond((endTime.getTime() - nowTime.getTime()) / 1000);
		}
		String goodsId = groupDraw.getGoodsId();
		if (StringUtil.isNotEmpty(goodsId)) {
			String[] goodsIds = goodsId.split(",");
			List<Integer> idList = new ArrayList<Integer>();
			for (String string : goodsIds) {
				idList.add(Integer.parseInt(string));
			}
			List<GoodsSmallVo> goodsList = saas.getShopApp(getShopId()).goods.getGoodsList(idList, true);
			if (goodsList.size() <= 0) {
				logger().info("没有可参与的活动商品");
				return null;
			}
			for (GoodsSmallVo goodsSmallVo : goodsList) {
				BigDecimal goodsPriceMax = getGoodsPriceMax(goodsSmallVo.getGoodsId());
				if(goodsPriceMax!=null) {
					goodsSmallVo.setShopPrice(goodsPriceMax);
				}
				goodsSmallVo.setGoodsImg(imageService.imageUrl(goodsSmallVo.getGoodsImg()));
			}
			vo.setList(goodsList);
			vo.setGroupDraw(into);
			logger().info("返回");
			return vo;
		}
		// return 没有可参与的活动商品
		logger().info("goodsId没有可参与的活动商品");
		return null;
	}

	/**
	 * 校验活动是否存在
	 *
	 * @param
	 * @return
	 * @return
	 */
	public GroupDrawReturn checkGroupDraw(GroupDrawInfoParam param, Integer userId) {
		Integer groupDrawId = param.getGroupDrawId();
		Integer goodsId = param.getGoodsId();
		Integer groupId = param.getGroupId();
		GroupDrawReturn result = new GroupDrawReturn();
		GroupDrawRecord groupDrawRecord = db().selectFrom(GROUP_DRAW).where(GROUP_DRAW.ID.eq(groupDrawId)).fetchAny();
		if (groupDrawRecord == null || groupDrawRecord.getStatus().equals(ZERO)
				|| groupDrawRecord.getDelFlag().equals(ONE)) {
			// 活动不存在
			logger().info("活动不存在");
			result.setCode(JsonResultCode.ACTIVITY_NOT_EXIST);
			return result;
		}
		GroupDrawInfoVo groupDraw = groupDrawRecord.into(GroupDrawInfoVo.class);
		GoodsRecord goodsRecord = saas.getShopApp(getShopId()).goods.getGoodsRecordById(goodsId);
		if (goodsRecord == null || goodsRecord.getDelFlag().equals(DelFlag.DISABLE.getCode())) {
			logger().info("商品不存在");
			result.setCode(JsonResultCode.PRODUCT_NOT_EXIST);
			return result;

		}
		GoodsView goods = goodsRecord.into(GoodsView.class);
		goods.setGoodsImg(imageService.imageUrl(goods.getGoodsImg()));
		JoinGroupListRecord groupInfo = getGroupInfo(groupDrawId, groupId);
		if (groupInfo == null) {
			logger().info("拼团信息不存在");
			result.setCode(JsonResultCode.INFORMATION_NOT_EXIST);
			return result;
		}

        return getGroupDrawReturn(param, userId, groupDrawId, groupId, result, groupDraw, goods, groupInfo);
    }

    private GroupDrawReturn getGroupDrawReturn(GroupDrawInfoParam param, Integer userId, Integer groupDrawId, Integer groupId, GroupDrawReturn result, GroupDrawInfoVo groupDraw, GoodsView goods, JoinGroupListRecord groupInfo) {
        logger().info("options处理");
        Map<String, String> options = param.getOptions();
        if (!options.isEmpty() && StringUtils.isNotEmpty(options.get("group_draw_id"))
                && StringUtils.isNotEmpty(options.get("goods_id"))
                && StringUtils.isNotEmpty(options.get("invite_id"))) {
            options.put("user_id", String.valueOf(userId));
            groupDrawInvite.createInviteRecord("pages1/pinlotteryinfo/pinlotteryinfo",
                    Integer.valueOf(options.get("group_draw_id")), options, ZERO);
        }
        String orderSn = groupInfo.getOrderSn();
        logger().info("获取订单信息");
        OrderGoodsRecord orderGoods = db().selectFrom(ORDER_GOODS).where(ORDER_GOODS.ORDER_SN.eq(orderSn)).fetchAny();
        Integer productId = orderGoods.getProductId();
        groupDraw.setProductId(productId == null ? 0 : productId);
        GoodsSpecProductRecord productInfo = db().selectFrom(GOODS_SPEC_PRODUCT)
                .where(GOODS_SPEC_PRODUCT.PRD_ID.eq(productId)).fetchAny();
        if (productInfo != null) {
            goods.setShopPrice(productInfo.getPrdPrice());
        }

        Timestamp endTime = groupDraw.getEndTime();
        Timestamp nowTime = DateUtils.getLocalDateTime();
        long surplusSecond = 0;
        if (nowTime.before(endTime)) {
            surplusSecond = (endTime.getTime() - nowTime.getTime()) / 1000;
        } else {
            groupDraw.setStatus(TWO);
        }

        logger().info("参团详情");
        GroupJoinDetailVo groupJoinDetail = getGroupJoinDetail(userId, groupDrawId, groupId);
        List<GoodsSmallVo> drawGoods = new ArrayList<GoodsSmallVo>();
        String goodsId2 = groupDraw.getGoodsId();
        if (StringUtil.isNotEmpty(goodsId2)) {
            String[] goodsIds = goodsId2.split(",");
            List<Integer> idList = new ArrayList<Integer>();
            for (String string : goodsIds) {
                idList.add(Integer.parseInt(string));
            }
            drawGoods = saas.getShopApp(getShopId()).goods.getGoodsList(idList, true);
        }
        JoinGroupListRecord userGroupInfoRecord = getUserJoinGroupInfo(userId, groupDrawId, groupId, true);
        GroupDrawList userGroupInfo = new GroupDrawList();
        if (userGroupInfoRecord != null) {
            userGroupInfo = userGroupInfoRecord.into(GroupDrawList.class);
        }
        String rewardCouponId = groupDraw.getRewardCouponId();
        Byte groupStatus = groupJoinDetail.getGroupStatus();

        if (StringUtils.isNotEmpty(rewardCouponId) || check(groupStatus)) {
            String[] split = rewardCouponId.split(",");
            groupDraw.setRewardNum(split.length);
        }
        GroupDrawInfoReturnVo vo = new GroupDrawInfoReturnVo(goods, groupDraw, drawGoods, groupJoinDetail,
                surplusSecond, userGroupInfo);
        result.setCode(JsonResultCode.CODE_SUCCESS);
        result.setVo(vo);
        return result;
    }

    private boolean check(Byte groupStatus) {
		for (Byte b : LIST) {
			if (b.equals(groupStatus)) {
				return true;
			}
		}
		return false;
	}

	private JoinGroupListRecord getGroupInfo(Integer groupDrawId, Integer groupId) {
		return db().selectFrom(JOIN_GROUP_LIST).where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId)
				.and(JOIN_GROUP_LIST.GROUP_ID.eq(groupId)).and(JOIN_GROUP_LIST.IS_GROUPER.eq(ONE))).fetchAny();
	}

	/**
	 * 拼团详情
	 *
	 * @param userId
	 * @param groupDrawId
	 * @param groupId
	 * @return
	 */
	public GroupJoinDetailVo getGroupJoinDetail(Integer userId, Integer groupDrawId, Integer groupId) {
		GroupDrawRecord groupDraw = db().selectFrom(GROUP_DRAW).where(GROUP_DRAW.ID.eq(groupDrawId)).fetchAny();
		JoinGroupListRecord groupInfo = getGroupInfo(groupDrawId, groupId);
		List<GroupDrawList> groupList = getGroupList(groupDrawId, groupId, null);
		JoinGroupListRecord userJoinGroup = getUserJoinGroupInfo(userId, groupDrawId, groupId, true);
		GroupDrawBotton botton = new GroupDrawBotton(ZERO, ONE, ONE);
		if (userJoinGroup != null) {
			botton.setIsJoinDraw(ZERO);
			botton.setIsOpenDraw(ZERO);
			botton.setIsToInvite(ONE);
		}
		GroupJoinDetailVo vo = new GroupJoinDetailVo();
		Byte status = groupInfo.getStatus();
		if (status.equals(ZERO)) {
			logger().info("拼团中");
			int surplusGroupNum = groupDraw.getLimitAmount() - groupList.size();
			vo.setButton(botton);
			vo.setGroupStatus(ZERO);
			vo.setSurplusGroupNum(surplusGroupNum);
		}
		if (status.equals(ONE)) {
			logger().info("已成团");
			Byte drawStatus = groupInfo.getDrawStatus();
			if (drawStatus.equals(ONE)) {
				logger().info("已开奖");
				botton.setIsJoinDraw(ZERO);
				botton.setIsOpenDraw(ZERO);
				botton.setIsToInvite(ZERO);
				if (userJoinGroup.getIsWinDraw().equals(ONE)) {
					logger().info("已中奖");
					vo.setGroupStatus((byte) 11);
					vo.setButton(botton);
				} else {
					logger().info("未中奖");
					GroupDrawList drawUser = getDrawUser(groupDrawId, groupId);
					if (drawUser != null) {
						vo.setGroupStatus((byte) 12);
						vo.setDrawUser(new DrawUser(drawUser.getUserAvatar(), drawUser.getUserName()));
					} else {
						logger().info("没有中奖用户");
					}
				}
			}else {
				if (drawStatus.equals(TWO)) {
					logger().info("未达到开奖条件-未中奖");
					botton.setIsToInvite(ZERO);
					botton.setIsJoinDraw(ZERO);
					vo.setGroupStatus((byte) 12);
					vo.setButton(botton);
				} else {
					logger().info("未开奖");
					vo.setGroupStatus((byte) 10);
					vo.setButton(botton);
				}
			}
		}
		if (status.equals(TWO)) {
			logger().info("拼团失败");
			botton.setIsJoinDraw(ZERO);
			botton.setIsOpenDraw(ZERO);
			botton.setIsToInvite(ZERO);
			vo.setGroupStatus(TWO);
			vo.setButton(botton);
		}
		vo.setUserList(groupList);
		return vo;
	}

    /**
     * 获得中奖用户
     * @param groupDrawId
     * @param groupId
     * @return
     */
	public GroupDrawList getDrawUser(Integer groupDrawId, Integer groupId) {
		GroupDrawList fetchAnyInto = db()
				.select(JOIN_GROUP_LIST.asterisk(), USER_DETAIL.USERNAME.as("userName"), USER_DETAIL.USER_AVATAR)
				.from(JOIN_GROUP_LIST, USER_DETAIL)
				.where(JOIN_GROUP_LIST.USER_ID.eq(USER_DETAIL.USER_ID)
						.and(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId).and(JOIN_GROUP_LIST.GROUP_ID.eq(groupId)))
						.and(JOIN_GROUP_LIST.IS_WIN_DRAW.eq(ONE)))
				.fetchAnyInto(GroupDrawList.class);
		if (fetchAnyInto != null) {
			fetchAnyInto.setUserAvatar(imageService.imageUrl(fetchAnyInto.getUserAvatar()));
		}
		return fetchAnyInto;
	}

	/**
	 * 获得用户参团信息
	 *
	 * @param userId
	 * @param groupDrawId
	 * @param groupId
	 * @param isHasNotPay 0:true; 1:false php不传时候输入true
	 * @return
	 */
	public JoinGroupListRecord getUserJoinGroupInfo(Integer userId, Integer groupDrawId, Integer groupId,
			Boolean isHasNotPay) {
		SelectConditionStep<JoinGroupListRecord> selectConditionStep = db().selectFrom(JOIN_GROUP_LIST)
				.where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId)
						.and(JOIN_GROUP_LIST.GROUP_ID.eq(groupId).and(JOIN_GROUP_LIST.USER_ID.eq(userId))));
		if (isHasNotPay) {
			selectConditionStep.and(JOIN_GROUP_LIST.STATUS.ge(ZERO));
		}
		return selectConditionStep.fetchOne();
	}

	/**
	 * 获得参团数量
	 *
	 * @param userId
	 * @param groupDrawId
	 * @return
	 */
	public int getJoinGroupNumber(Integer userId, Integer groupDrawId) {
		Result<JoinGroupListRecord> fetch = db().selectFrom(JOIN_GROUP_LIST)
				.where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId)
						.and(JOIN_GROUP_LIST.USER_ID.eq(userId)
								.and(JOIN_GROUP_LIST.STATUS.eq(ZERO).and(JOIN_GROUP_LIST.IS_GROUPER.eq(ZERO)))))
				.fetch();
		if (fetch != null) {
			return fetch.size();
		}
		return 0;
	}

	/**
	 * 获得开团数量
	 *
	 * @param userId
	 * @param groupDrawId
	 * @return
	 */
	public int getOpenGroupNumber(Integer userId, Integer groupDrawId) {
		Result<JoinGroupListRecord> fetch = db()
				.selectFrom(JOIN_GROUP_LIST).where(
						JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId)
								.and(JOIN_GROUP_LIST.USER_ID.eq(userId)
										.and(JOIN_GROUP_LIST.STATUS.eq(ZERO).and(JOIN_GROUP_LIST.IS_GROUPER.eq(ONE)))))
				.fetch();
		if (fetch != null) {
			return fetch.size();
		}
		return 0;
	}

	/**
	 * 通过商品获得用户参团信息
	 *
	 * @param userId
	 * @param groupDrawId
	 * @param goodsId
	 * @return
	 */
	public JoinGroupListRecord getUserJoinGroupInfoByGoodsId(Integer userId, Integer groupDrawId, Integer goodsId) {
		SelectConditionStep<JoinGroupListRecord> selectConditionStep = db().selectFrom(JOIN_GROUP_LIST)
				.where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId)
						.and(JOIN_GROUP_LIST.GOODS_ID.eq(goodsId).and(JOIN_GROUP_LIST.USER_ID.eq(userId))));
		selectConditionStep.and(JOIN_GROUP_LIST.STATUS.ge(ZERO));
		return selectConditionStep.fetchOne();
	}

	/**
	 * 获得团列表
	 *
	 * @param groupDrawId
	 * @param groupId
	 * @param status
	 * @return
	 */
	public List<GroupDrawList> getGroupList(Integer groupDrawId, Integer groupId, Byte status) {
		SelectConditionStep<Record> selectConditionStep = db()
				.select(JOIN_GROUP_LIST.asterisk(), USER_DETAIL.USERNAME.as("userName"), USER_DETAIL.USER_AVATAR).from(JOIN_GROUP_LIST)
				.leftJoin(USER_DETAIL).on(JOIN_GROUP_LIST.USER_ID.eq(USER_DETAIL.USER_ID))
				.where((JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId).and(JOIN_GROUP_LIST.GROUP_ID.eq(groupId))));
		if (status != null) {
			selectConditionStep.and(JOIN_GROUP_LIST.STATUS.eq(status));
		} else {
			selectConditionStep.and(JOIN_GROUP_LIST.STATUS.ge(ZERO));
		}
		List<GroupDrawList> list = selectConditionStep.orderBy(JOIN_GROUP_LIST.IS_GROUPER.desc())
				.fetchInto(GroupDrawList.class);
		for (GroupDrawList groupDrawList : list) {
			String userName = groupDrawList.getUserName();
			if (StringUtils.isEmpty(userName)) {
				groupDrawList.setUserName("神秘小伙伴");
			}
			String userAvatar = groupDrawList.getUserAvatar();
            String pathSegment = "/";
            if (StringUtils.isNotEmpty(userAvatar) && userAvatar.substring(0, 1).equals(pathSegment)) {
				groupDrawList.setUserAvatar(imageService.imageUrl("/image/admin/head_icon.png"));
			}
		}
		return list;
	}

	/**
	 * 检查是否可创建拼团抽奖订单
	 *
	 * @param userId
	 * @param groupDrawId
	 * @param goodsId
	 * @param groupId
	 * @param isFromNotPay 0 true;1 false
	 * @return
	 */
	public GroupDrawReturn canCreateGroupOrder(Integer userId, Integer groupDrawId, Integer goodsId, Integer groupId,
			Boolean isFromNotPay) {
		GroupDrawRecord groupDraw = db().selectFrom(GROUP_DRAW).where(GROUP_DRAW.ID.eq(groupDrawId)).fetchAny();
		GroupDrawReturn vo = new GroupDrawReturn();
		if (groupDraw == null) {
			// 活动不存在
			logger().info("活动不存在");
			vo.setCode(JsonResultCode.ACTIVITY_NOT_EXIST);
			return vo;
		}
		Timestamp endTime = groupDraw.getEndTime();
		Byte status = groupDraw.getStatus();
		Byte delFlag = groupDraw.getDelFlag();
		Timestamp nowTime = DateUtils.getLocalDateTime();
		if (endTime.before(nowTime) || status.equals(ACTIVITY_STATUS_DISABLE) || delFlag.equals(ONE)) {
			// 活动不存在
			logger().info("活动已结束");
			vo.setCode(JsonResultCode.EVENT_IS_OVER);
			return vo;
		}
		Timestamp startTime = groupDraw.getStartTime();
		if (startTime.after(nowTime)) {
			// 活动还没开始
			logger().info("活动还没开始");
			vo.setCode(JsonResultCode.EVENT_NOT_STARTED);
			return vo;
		}
		if (isFromNotPay) {
			OrderInfoRecord orderInfo = orderInfoService.getWaitPayOrderByActivityId(userId, goodsId,
					BaseConstant.ACTIVITY_TYPE_GROUP_DRAW, groupDrawId);
			if (orderInfo != null) {
				logger().info("存在未支付订单");
				vo.setCode(JsonResultCode.HAVE_UNPAID_ORDERS);
				return vo;
			}
		}

		JoinGroupListRecord joinGroupInfo = getUserJoinGroupInfoByGoodsId(userId, groupDrawId, goodsId);
		if (joinGroupInfo != null) {
			logger().info("已参与该活动");
			vo.setCode(JsonResultCode.PARTICIPATED_IN_EVENT);
			String url = "pages1/pinlotteryinfo/pinlotteryinfo?group_id=" + joinGroupInfo.getGroupId()
					+ "&group_draw_id=" + groupDrawId + "&goods_id=" + goodsId;
			vo.setUrl(url);
			return vo;
		}
		if(groupId!=null) {
			JoinGroupListRecord userJoinGroupInfo = getUserJoinGroupInfo(userId, groupDrawId, groupId, false);
			// 不是团长
			if (userJoinGroupInfo != null && userJoinGroupInfo.getIsGrouper().equals(ZERO)) {
				int joinGroupNumber = getJoinGroupNumber(userId, groupDrawId);
				Short joinLimit = groupDraw.getJoinLimit();
				if (joinGroupNumber >= Integer.valueOf(String.valueOf(joinLimit))) {
					logger().info("参团已达上限");
					vo.setCode(JsonResultCode.PARTICIPANTS_IS_MAX);
					return vo;
				}
			}
		}
		int openGroupNumber = getOpenGroupNumber(userId, groupDrawId);
		Short openLimit = groupDraw.getOpenLimit();
		if (openGroupNumber >= Integer.valueOf(String.valueOf(openLimit))) {
			logger().info("开团已达上限");
			vo.setCode(JsonResultCode.GROUP_UPPER_LIMIT);
			return vo;
		}
		logger().info("成功");
		vo.setCode(JsonResultCode.CODE_SUCCESS);
		GroupDrawInfoReturnVo vo2 = new GroupDrawInfoReturnVo();
		vo2.setGroupDraw(groupDraw.into(GroupDrawInfoVo.class));
		vo.setVo(vo2);
		return vo;

	}

	public JoinGroupListRecord getGroupInfoByOrderSn(String orderSn) {
		return db().selectFrom(JOIN_GROUP_LIST).where(JOIN_GROUP_LIST.ORDER_SN.eq(orderSn)).fetchAny();
	}

	/**
	 * 生成团分组Id
	 *
	 * @return
	 */
	public int generateGroupId() {
		Integer into = db().select(DSL.max(JOIN_GROUP_LIST.GROUP_ID)).from(JOIN_GROUP_LIST).fetchAnyInto(Integer.class);
		return into==null?1:into+1;
	}

	/**
	 * 生成抽奖码
	 *
	 * @param groupDrawId
	 * @param goodsId
	 * @return
	 */
	public int generateGroupId(Integer groupDrawId, Integer goodsId) {
		 Integer value = db().select(DSL.max(JOIN_DRAW_LIST.DRAW_ID)).from(JOIN_DRAW_LIST)
				.where(JOIN_DRAW_LIST.GROUP_DRAW_ID.eq(groupDrawId).and(JOIN_DRAW_LIST.GOODS_ID.eq(goodsId)))
				.fetchAnyInto(Integer.class);
		 if(null==value) {
			 return 1;
		 }
		 return value + 1;
	}

	/**
	 * 生成抽奖码记录
	 *
	 * @param userId
	 * @param groupDrawId
	 * @param goodsId
	 * @param groupId
	 */
	public void generateDrawRecord(Integer userId, Integer groupDrawId, Integer goodsId, Integer groupId) {
		JoinDrawListRecord newRecord = db().newRecord(JOIN_DRAW_LIST);
		newRecord.setUserId(userId);
		newRecord.setGroupDrawId(groupDrawId);
		newRecord.setGoodsId(goodsId);
		newRecord.setGroupId(groupId);
		newRecord.setDrawId(generateGroupId(groupDrawId, goodsId));
		int insert = newRecord.insert();
		log.info("生成抽奖码记录" + insert);

	}

	/**
	 * 增加邀请用户数
	 *
	 * @param userId
	 * @param groupDrawId
	 * @param groupId
	 */
	public void increaseUserNum(Integer userId, Integer groupDrawId, Integer groupId) {
		JoinGroupListRecord record = db().selectFrom(JOIN_GROUP_LIST).where(JOIN_GROUP_LIST.GROUP_DRAW_ID
				.eq(groupDrawId).and(JOIN_GROUP_LIST.GROUP_ID.eq(groupId).and(JOIN_GROUP_LIST.USER_ID.eq(userId))))
				.fetchAny();
		if (record != null) {
			int num=record.getInviteUserNum() + 1;
			record.setInviteUserNum(num);
			int update = record.update();
			log.info("增加邀请用户数" + update);
		}
	}


	/**
	 * 生成团记录
	 * @param order
	 * @param groupId
	 * @param status
	 * @param sendGoodsId ORDER_GOODS有数据时候sendGoodsId传null
	 */
	public void generateGroupRecord(OrderInfoRecord order, Integer groupId, Byte status,Integer sendGoodsId) {
		log.info("进入generateGroupRecord"+sendGoodsId);
		Integer goodsId=sendGoodsId;
		if(null==sendGoodsId) {
			OrderGoodsRecord orderGoods = db().selectFrom(ORDER_GOODS).where(ORDER_GOODS.ORDER_SN.eq(order.getOrderSn()))
					.fetchAny();
			logger().info("orderGoods"+orderGoods);
			goodsId = orderGoods.getGoodsId();
		}
		Byte isGrouper = groupId == null ? ONE : ZERO;
		groupId = groupId == null ? generateGroupId() : groupId;
		Integer groupDrawId = order.getActivityId();
		// 记录邀请用户
		GroupDrawInviteRecord inviteUserInfo = groupDrawInvite.getAvailableInviteUser(groupDrawId,
				goodsId, order.getUserId());
		Integer inviteUserId = 0;
		inviteUserId = inviteUserInfo == null ? 0 : inviteUserInfo.getInviteUserId();
		log.info("groupDrawId为：{}，goodsId：{}，userId：{}，邀请人id：{}",groupDrawId,goodsId,order.getUserId(),inviteUserId);
		Integer userId = order.getUserId();

		JoinGroupListRecord newRecord = db().newRecord(JOIN_GROUP_LIST);
		newRecord.setGroupDrawId(groupDrawId);
		newRecord.setGoodsId(goodsId);
		newRecord.setGroupId(groupId);
		newRecord.setUserId(userId);
		newRecord.setIsGrouper(isGrouper);
		newRecord.setInviteUserId(inviteUserId);
		newRecord.setOrderSn(order.getOrderSn());
		newRecord.setStatus(status);
		newRecord.setOpenTime(DateUtils.getSqlTimestamp());
//TODO
		int insert = newRecord.insert();
		log.info("插入结果" + insert);
		if (status.equals(ZERO)) {
			generateDrawRecord(userId, groupDrawId, goodsId, groupId);
			if (inviteUserId != 0) {
				generateDrawRecord(inviteUserId, groupDrawId, goodsId, groupId);
				Byte isNew = inviteUserInfo.getIsNew();
				if (Objects.equal(ONE, isNew)) {
					generateDrawRecord(userId, groupDrawId, goodsId, groupId);
					generateDrawRecord(inviteUserId, groupDrawId, goodsId, groupId);
				}
				groupDrawInvite.updateInviteRow(inviteUserInfo.getId(), ONE);
				increaseUserNum(userId, groupDrawId, groupId);
			}
			successGroupDraw(groupDrawId, groupId);
		}
	}

	public void successGroupDraw(Integer groupDrawId, Integer groupId) {
		GroupDrawRecord groupDraw = db().selectFrom(GROUP_DRAW).where(GROUP_DRAW.ID.eq(groupDrawId)).fetchAny();
		List<GroupDrawList> groupDrawUserList = getGroupList(groupDrawId, groupId, null);
		int size = groupDrawUserList.size();
		Short limitAmount = groupDraw.getLimitAmount();
		if (size >= Integer.valueOf(String.valueOf(limitAmount))) {
			// 修改订单状态
			List<GroupDrawList> groupUserList = getGroupList(groupDrawId, groupId, ZERO);
			List<String> list=new ArrayList<String>();
			for (int i = 0; i < groupUserList.size(); i++) {
				String orderSn = groupUserList.get(i).getOrderSn();
				list.add(orderSn);
			}
			// order_info
			int execute = db().update(ORDER_INFO).set(ORDER_INFO.ORDER_STATUS, OrderConstant.ORDER_PIN_SUCCESSS)
					.set(ORDER_INFO.ORDER_STATUS_NAME, "已成团").where(ORDER_INFO.ORDER_SN.in(list)).execute();
			log.info("已成团" + list.toString() + "结果" + execute);
			// 修改参团状态
			int execute2 = db().update(JOIN_GROUP_LIST).set(JOIN_GROUP_LIST.STATUS, ONE)
					.set(JOIN_GROUP_LIST.END_TIME, DateUtils.getLocalDateTime())
					.where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId)
							.and(JOIN_GROUP_LIST.GROUP_ID.eq(groupId).and(JOIN_GROUP_LIST.STATUS.eq(ZERO))))
					.execute();
			logger().info("修改参团状态" + execute2);
			// 发送模板消息
			sendMsg(groupDrawUserList, groupDraw);
		}
	}

	public void sendMsg(List<GroupDrawList> groupDrawUserList, GroupDrawRecord groupDraw) {
		log.info("发送模板消息");
		String first = "您好，您有新的拼团成功订单";
		for (GroupDrawList groupDrawList : groupDrawUserList) {
			Integer userId = groupDrawList.getUserId();
			Integer goodsId = groupDrawList.getGoodsId();
			GoodsRecord goodsInfo = getGood(goodsId);
			log.info("获取商品信息：" + goodsInfo);
			String page = "pages1/pinlotteryinfo/pinlotteryinfo?group_id=" + groupDrawList.getGroupId()
					+ "&group_draw_id=" + groupDrawList.getGroupDrawId() + "&goods_id=" + goodsId;
			JoinGroupListRecord groupInfo = getGroupInfo(groupDrawList.getGroupDrawId(), groupDrawList.getGroupId());
			UserRecord grouper = db().selectFrom(USER).where(USER.USER_ID.eq(groupInfo.getUserId())).fetchAny();
			List<Integer> userIdList = new ArrayList<Integer>();
			userIdList.add(userId);
			logger().info("userIdList" + userIdList);
			sendMaMp(groupDraw, first, userId, goodsInfo, page, grouper, userIdList);
		}
	}

	protected GoodsRecord getGood(Integer goodsId) {
		return db().selectFrom(GOODS).where(GOODS.GOODS_ID.eq(goodsId)).fetchAny();
	}

	/**
	 * 发送小程序和公众号
	 *
	 * @param groupDraw
	 * @param first
	 * @param userId
	 * @param goodsInfo
	 * @param page
	 * @param grouper
	 * @param userIdList
	 */
	private void sendMaMp(GroupDrawRecord groupDraw, String first, Integer userId, GoodsRecord goodsInfo, String page,
			UserRecord grouper, List<Integer> userIdList) {
		logger().info("sendMaMp发送");
		String prizeName = "已成团，等待开奖";
		String[][] maData = new String[][] { { groupDraw.getName() }, { prizeName },
				{ Util.getdate("yyyy-MM-dd HH:mm:ss") } };
		String[][] mpData = new String[][] { { first, "#173177" }, { goodsInfo.getGoodsName(), "#173177" },
				{ grouper.getUsername(), "#173177" }, { String.valueOf(groupDraw.getLimitAmount()), "#173177" },
				{ "", "#173177" } };

		MaSubscribeData data = MaSubscribeData.builder().data307(maData).build();
		RabbitMessageParam param = RabbitMessageParam.builder()
				.maTemplateData(
						MaTemplateData.builder().config(SubcribeTemplateCategory.INVITE_SUCCESS).data(data).build())
				.mpTemplateData(MpTemplateData.builder().config(MpTemplateConfig.GROUP_SUCCESS).data(mpData).build())
				.page(page).shopId(getShopId()).userIdList(userIdList)
				.type(RabbitParamConstant.Type.SUCCESS_TEAM).build();
		saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(),
				TaskJobEnum.SEND_MESSAGE.getExecutionType());
	}

	/**
	 * 通过订单号更新团信息
	 *
	 * @param orderSn
	 * @param status
	 */
	public void updateGroupInfoByOrderSn(String orderSn, Byte status) {
		log.info("orderSn" + orderSn + "通过订单号更新团信息");
		int execute = db().update(JOIN_GROUP_LIST).set(JOIN_GROUP_LIST.STATUS, status)
				.where(JOIN_GROUP_LIST.ORDER_SN.eq(orderSn)).execute();
		log.info("订单号：" + orderSn + "；更新状态为：" + status + "；结果：" + execute);
		if (status.equals(ZERO)) {
			JoinGroupListRecord groupInfo = getGroupInfoByOrderSn(orderSn);
			Integer groupDrawId = groupInfo.getGroupDrawId();
			Integer goodsId = groupInfo.getGoodsId();
			Integer userId = groupInfo.getUserId();
			Integer groupId = groupInfo.getGroupId();
			GroupDrawInviteRecord inviteUserInfo = groupDrawInvite.getAvailableInviteUser(groupDrawId, goodsId, userId);
			log.info("inviteUserInfo"+inviteUserInfo);
			generateDrawRecord(userId, groupDrawId, goodsId, groupId);
			if (groupDrawId != null && inviteUserInfo != null) {
				Integer inviteUserId = inviteUserInfo.getInviteUserId();
				generateDrawRecord(inviteUserId, groupDrawId, goodsId, groupId);
				Byte isNew = inviteUserInfo.getIsNew();
				if (Objects.equal(ONE, isNew)) {
					generateDrawRecord(userId, groupDrawId, goodsId, groupId);
					generateDrawRecord(inviteUserId, groupDrawId, goodsId, groupId);
				}
				groupDrawInvite.updateInviteRow(inviteUserInfo.getId(), ONE);
				increaseUserNum(userId, groupDrawId, groupId);
			}
			successGroupDraw(groupDrawId, groupId);
		}
	}

	public GroupDrawRecord getById(Integer id) {
		return db().selectFrom(GROUP_DRAW).where(GROUP_DRAW.ID.eq(id)).fetchAny();
	}

    /**
     * 根据订单号获取改订单是否中奖
     * @param orderSn
     * @return
     */
    public boolean isWinDraw(String orderSn) {
        JoinGroupListRecord groupInfoByOrderSn = getGroupInfoByOrderSn(orderSn);
        if(groupInfoByOrderSn == null || OrderConstant.NO == groupInfoByOrderSn.getIsWinDraw()){
            return false;
        }
        return true;
    }

    /**
     * 订单号查询拼团信息
     * @param orderSn
     * @param flag 为true校验拼团活动是否在，为false不校验
     * @return
     */
    public GroupDrawInfoByOsVo getGroupByOrderSn(String orderSn,Boolean flag) {
    	JoinGroupListRecord fetchAny = db().selectFrom(JOIN_GROUP_LIST).where(JOIN_GROUP_LIST.ORDER_SN.eq(orderSn)).fetchAny();
    	if(fetchAny==null) {
    		logger().info("订单{}不存在",orderSn);
    		return null;
    	}
    	if(flag) {
    		GroupDrawVo byId = groupDrawList(fetchAny.getGroupDrawId());
    		if(byId==null) {
    			logger().info("拼团活动{}不存在",fetchAny.getGroupDrawId());
    			return null;
    		}
    	}
    	GroupDrawInfoByOsVo vo=new GroupDrawInfoByOsVo();
    	vo.setActivityId(fetchAny.getGroupDrawId());
    	vo.setActivityType(BaseConstant.ACTIVITY_TYPE_GROUP_DRAW);
    	vo.setDrawStatus(fetchAny.getDrawStatus());
    	vo.setStatus(fetchAny.getStatus());
    	vo.setGroupId(fetchAny.getGroupId());
    	vo.setIsGrouper(fetchAny.getIsGrouper());
		return vo;
    }

	/**
	 * 获取规格最高价
	 */
	private BigDecimal getGoodsPriceMax(Integer goodId) {
		return db().select(DSL.max(GOODS_SPEC_PRODUCT.PRD_PRICE)).from(GOODS_SPEC_PRODUCT).where(GOODS_SPEC_PRODUCT.GOODS_ID.eq(goodId))
				.fetchOneInto(BigDecimal.class);
	}

    /**
     * 营销日历用id查询活动
     * @param id
     * @return
     */
    public MarketVo getActInfo(Integer id) {
		return db().select(GROUP_DRAW.ID, GROUP_DRAW.NAME.as(CalendarAction.ACTNAME), GROUP_DRAW.START_TIME,
				GROUP_DRAW.END_TIME).from(GROUP_DRAW).where(GROUP_DRAW.ID.eq(id)).fetchAnyInto(MarketVo.class);
    }

    /**
     * 营销日历用查询目前正常的活动
     * @param param
     * @return
     */
	public PageResult<MarketVo> getListNoEnd(MarketParam param) {
		SelectSeekStep1<Record4<Integer, String, Timestamp, Timestamp>, Integer> select = db()
				.select(GROUP_DRAW.ID, GROUP_DRAW.NAME.as(CalendarAction.ACTNAME), GROUP_DRAW.START_TIME,
						GROUP_DRAW.END_TIME)
				.from(GROUP_DRAW)
				.where(GROUP_DRAW.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(GROUP_DRAW.STATUS
						.eq(BaseConstant.ACTIVITY_STATUS_NORMAL).and(GROUP_DRAW.END_TIME.gt(DateUtils.getSqlTimestamp()))))
				.orderBy(GROUP_DRAW.ID.desc());
		PageResult<MarketVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				MarketVo.class);
		return pageResult;
	}

	/**
	 * 获取抽奖码数量
	 * @param groupDrawId
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public int getDrawNum(Integer groupDrawId, Integer userId, Integer goodsId) {
		Integer num = db().select(DSL.count()).from(JOIN_DRAW_LIST)
				.where(JOIN_DRAW_LIST.GROUP_DRAW_ID.eq(groupDrawId)
						.and(JOIN_DRAW_LIST.USER_ID.eq(userId).and(JOIN_DRAW_LIST.GOODS_ID.eq(goodsId))))
				.fetchAnyInto(Integer.class);
		log.info("获取抽奖码数量:{}",num);
		return num == null ? 0 : num;
	}

	/**
	 * 获取邀请人数
	 * @param groupDrawId
	 * @param userId
	 * @param goodsId
	 * @return
	 */
	public int getInviteNum(Integer groupDrawId, Integer userId, Integer goodsId) {
		Integer num = db().select(DSL.count()).from(JOIN_GROUP_LIST)
				.where(JOIN_GROUP_LIST.GROUP_DRAW_ID.eq(groupDrawId)
						.and(JOIN_GROUP_LIST.INVITE_USER_ID.eq(userId).and(JOIN_GROUP_LIST.GOODS_ID.eq(goodsId))))
				.fetchAnyInto(Integer.class);
		log.info("获取邀请人数:{}",num);
		return num == null ? 0 : num;
	}

	/**
	 * 添加邀请信息
	 * @param param
	 */
	public void createInviteRecord(OrderBeforeParam param) {
		log.info("插入邀请记录");
		if(param.getInviteId()!=0) {
			logger().info("邀请人id：{}",param.getInviteId());
			Map<String, String> query = toMap(param);
			groupDrawInvite.createInviteRecord("pages1/pinlotteryinfo/pinlotteryinfo",
					Integer.valueOf(query.get("group_draw_id")), query, ZERO);
		}
		log.info("插入邀请记录结束");
	}

	private Map<String, String> toMap(OrderBeforeParam param) {
		Map<String, String> query = new HashMap<String, String>();
		List<Goods> goods = param.getGoods();
		Integer goodsId = goods.get(0).getGoodsId();
		query.put("group_draw_id", String.valueOf(param.getActivityId()));
		query.put("goods_id", String.valueOf(goodsId));
		query.put("group_id", String.valueOf(param.getGroupId()));
		query.put("invite_id", String.valueOf(param.getInviteId()));
		query.put("user_id", String.valueOf(param.getWxUserInfo().getUserId()));
		return query;
	}
}
