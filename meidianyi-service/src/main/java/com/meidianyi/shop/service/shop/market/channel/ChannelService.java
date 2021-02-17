package com.meidianyi.shop.service.shop.market.channel;

import static com.meidianyi.shop.db.shop.Tables.GOODS;
import static com.meidianyi.shop.db.shop.Tables.ORDER_INFO;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.tables.Channel.CHANNEL;
import static com.meidianyi.shop.db.shop.tables.ChannelRecord.CHANNEL_RECORD;
import static com.meidianyi.shop.db.shop.tables.XcxCustomerPage.XCX_CUSTOMER_PAGE;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meidianyi.shop.service.shop.decoration.AdminDecorationService;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Result;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.ChannelRecord;
import com.meidianyi.shop.db.shop.tables.records.ChannelRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.ChannelStatisticalRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelConstant;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelPageInfo;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelPageParam;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelPageVo;
import com.meidianyi.shop.service.pojo.shop.market.channel.ChannelParam;
import com.meidianyi.shop.service.pojo.shop.market.channel.QrCodeShareVo;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.image.QrCodeService;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangronggang
 * @date 2019年8月14日 渠道分析
 */
@Service
public class ChannelService extends ShopBaseService {

	public static final String PAGE_PATH_PARAM_FORMAT = "page=%d&c=%s";
	public static final String GOODS_PATH_PARAM_FORMAT = "gid=%d&c=%s";

	private static final String CUSTOMIZE_PATH="pages/index/index?";
    private static final String GOODS_DETAIL_PATH="pages/item/item?";
	/** 分享码 进制转换使用 */
	private static final String DIGIT = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final BigInteger SCALE = new BigInteger("62");
	@Autowired
    AdminDecorationService adminDecoration;
	@Autowired
	GoodsService goodsService;
	@Autowired
	ChannelStatisticalService channelStatistic;
	@Autowired
	QrCodeService qrCode;

	/**
	 * 渠道页面分析 分页查询
	 *
	 * @param param
	 * @return
	 */
	public PageResult<ChannelPageVo> getPageList(ChannelPageParam param) {
		SelectOnConditionStep<?> step = db()
				.select(CHANNEL.ID, CHANNEL.PAGE_ID,
						DSL.iif(CHANNEL.SOURCE_TYPE.eq(ChannelConstant.SOURCETYPE_CUSTOMIZE),
								XCX_CUSTOMER_PAGE.PAGE_NAME, GOODS.GOODS_NAME).as("pageName"),
						CHANNEL.GOODS_ID, CHANNEL.CHANNEL_NAME, CHANNEL.SOURCE_TYPE, CHANNEL.SHARE, CHANNEL.DEL_FLAG,
						CHANNEL.CREATE_TIME)
				.from(CHANNEL).leftJoin(XCX_CUSTOMER_PAGE).on(CHANNEL.PAGE_ID.eq(XCX_CUSTOMER_PAGE.PAGE_ID))
				.leftJoin(GOODS).on(CHANNEL.GOODS_ID.eq(GOODS.GOODS_ID));
		buildOptions(step, param);
		PageResult<ChannelPageVo> result = getPageResult(step, param.getCurrentPage(), param.getPageRows(),
				ChannelPageVo.class);
		if (result.dataList == null) {
			return result;
		}
		for (ChannelPageVo vo : result.dataList) {
		    if( vo.getSourceType().equals(ChannelConstant.SOURCETYPE_CUSTOMIZE) ){
		        vo.setShare(CUSTOMIZE_PATH+"page="+vo.getPageId());
            }else{
                vo.setShare(GOODS_DETAIL_PATH+"gid="+vo.getGoodsId());
            }
			vo.setOrderNum(getOrderNum(vo.getId(), ChannelConstant.INVITESOURCE));
			vo.setNewUserNum(getUserNum(vo.getId(), ChannelConstant.INVITESOURCE));
			setYesterdayInfo(vo);
		}
		return result;
	}

	/**
	 * 设置昨日访问次数和访问人数信息
	 *
	 * @param vo
	 */
	private void setYesterdayInfo(ChannelPageVo vo) {
		ChannelStatisticalRecord statisticalRecord = channelStatistic.selectYesterDayRecord(vo.getId(), vo.getPageId(),
				vo.getGoodsId());
		if (statisticalRecord == null) {
			vo.setYesterdayAccessNum(0);
			vo.setYesterdayAccessTimes(0);
			return;
		}
		vo.setYesterdayAccessTimes(getMapJsonValue(statisticalRecord.getChannelAllPv(), vo.getId()));
		vo.setYesterdayAccessNum(getMapJsonValue(statisticalRecord.getChannelAllUv(), vo.getId()));

	}

	private int getMapJsonValue( String json,Integer channelId) {
		if (json == null) {
			return 0;
		}
		Map<String, Integer> pvMap = Util.parseJson(json, new TypeReference<Map<String, Integer>>() {
		});
		if (pvMap == null) {
			return 0;
		}
		return pvMap.get(channelId.toString());
	}

	/**
	 * 渠道用户数量
	 *
	 * @param id
	 * @param invitesource
	 * @return
	 */
	private Integer getUserNum(Integer id, String invitesource) {
		return db().select(DSL.count()).from(USER).where(USER.INVITE_SOURCE.eq(invitesource))
				.and(USER.INVITE_ACT_ID.eq(id)).fetchOneInto(Integer.class);
	}

	/**
	 * 渠道订单数量
	 *
	 * @param inviteActId
	 * @param inviteSource
	 * @return
	 */
	private int getOrderNum(Integer inviteActId, String inviteSource) {
		return db().select(DSL.count()).from(USER).leftJoin(ORDER_INFO).on(USER.USER_ID.eq(ORDER_INFO.USER_ID))
				.where(USER.INVITE_SOURCE.eq(inviteSource)).and(USER.INVITE_ACT_ID.eq(inviteActId))
				.fetchOneInto(Integer.class);
	}

	/**
	 * @param step
	 * @param param
	 * @return
	 */
	private void buildOptions(SelectJoinStep<?> step, ChannelPageParam param) {
		if (!StringUtils.isBlank(param.getChannelName())) {
			step.where(CHANNEL.CHANNEL_NAME.like(this.likeValue(param.getChannelName())));
		}
		if (param.getSourceType() != null) {
			step.where(CHANNEL.SOURCE_TYPE.eq(param.getSourceType()));
		}
		if (param.getStartTime() != null) {
			step.where(CHANNEL.CREATE_TIME.greaterOrEqual(param.getStartTime()));
		}
		if (param.getEndTime() != null) {
			step.where(CHANNEL.CREATE_TIME.lessOrEqual(param.getEndTime()));
		}

		if (!StringUtils.isBlank(param.getSourcePage())) {
			List<Integer> idList = adminDecoration.getIdByName(param.getSourcePage());
			if (idList == null || idList.isEmpty()) {
				return;
			}
			step.where(CHANNEL.PAGE_ID.in(idList));
		}
		step.orderBy(CHANNEL.CREATE_TIME.desc());
		return;
	}

	/**
	 * 停用渠道页面
	 *
	 * @param id
	 * @return
	 */
	public int disableChannel(Integer id) {
		return db().update(CHANNEL).set(CHANNEL.DEL_FLAG, DelFlag.DISABLE_VALUE).where(CHANNEL.ID.eq(id))
				.and(CHANNEL.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).execute();
	}

	/**
	 * 启用渠道页面
	 *
	 * @param id
	 * @return
	 */
	public int enableChannel(Integer id) {
		return db().update(CHANNEL).set(CHANNEL.DEL_FLAG, DelFlag.NORMAL_VALUE).where(CHANNEL.ID.eq(id))
				.and(CHANNEL.DEL_FLAG.eq(DelFlag.DISABLE_VALUE)).execute();
	}

	public QrCodeShareVo shareQrCode(Integer channelId) {
		ChannelRecord record = selectChannelRecord(channelId);
		QrCodeShareVo qrCodeVo = null;
		String pathParam =null;
		String imageUrl =null;
		if (record != null) {
			if(ChannelConstant.SOURCETYPE_CUSTOMIZE.equals(record.getSourceType())) {
				pathParam = String.format(PAGE_PATH_PARAM_FORMAT, record.getPageId(),record.getShare());
				imageUrl = qrCode.getMpQrCode(QrCodeTypeEnum.INDEX, pathParam);
				pathParam = CUSTOMIZE_PATH + pathParam;
			}else {
				pathParam = String.format(GOODS_DETAIL_PATH+GOODS_PATH_PARAM_FORMAT, record.getGoodsId(),record.getShare());
				imageUrl = qrCode.getMpQrCode(QrCodeTypeEnum.GOODS_ITEM, pathParam);
                pathParam = GOODS_DETAIL_PATH + pathParam;
			}

			qrCodeVo = new QrCodeShareVo(imageUrl,pathParam);
		}
		return qrCodeVo;
	}

	public ChannelRecord selectChannelRecord(Integer id) {
		if (id == null) {
			return null;
		}
		return db().selectFrom(CHANNEL).where(CHANNEL.ID.eq(id)).fetchOne();
	}

	/**
	 * 添加渠道页面
	 * @param param
	 * @return
	 */
	public int insert(ChannelParam param) {
		ChannelRecord record = new ChannelRecord();
		if(ChannelConstant.SOURCETYPE_CUSTOMIZE.equals(param.getSourceType())){
			record.setPageId(param.getContentId());
		}else {
			record.setGoodsId(param.getContentId());
		}
		record.setChannelName(param.getChannelName());
		record.setSourceType(param.getSourceType());
		record.setShare(createShare());
		return db().executeInsert(record);
	}
	/**
	 * 生成唯一的渠道码 ：uuid 转62进制。
	 * @return
	 */
	public String createShare() {
		BigInteger hex =new BigInteger(Util.randomId().replace("-", ""),16);
		return convert62(hex).substring(0,6);
	}

	private String convert62(BigInteger from) {
		StringBuilder dest = new StringBuilder();
		String sign = from.signum() ==-1?"-":"";
		while(true) {
			BigInteger[] result = from.divideAndRemainder(SCALE);
			from= result[0];
			dest.append(DIGIT.charAt(result[1].abs().intValue()));
			if(from.equals(BigInteger.ZERO)) {
				break;
			}
		}
		dest.append(sign);
		return dest.reverse().toString();
	}
	public Map<Integer, String> selectChannelName(List<Integer> idList) {
		if(idList.isEmpty()) {
			return Collections.emptyMap();
		}
		return db().selectDistinct(CHANNEL.ID,CHANNEL.CHANNEL_NAME).from(CHANNEL).where(CHANNEL.ID.in(idList)).fetchMap(CHANNEL.ID, CHANNEL.CHANNEL_NAME);
	}
	public List<ChannelRecord> selectChannelRecord(List<Integer> idList){
		if(idList.isEmpty()) {
			return Collections.emptyList();
		}
		return db().select().from(CHANNEL).where(CHANNEL.ID.in(idList)).fetchInto(ChannelRecord.class);
	}
	public String selectChannelName(Integer id) {
		if(id == null) {
			return null;
		}
		return db().select(CHANNEL.CHANNEL_NAME).from(CHANNEL).where(CHANNEL.ID.eq(id)).fetchAnyInto(String.class);
	}

	public List<ChannelPageInfo> selectAllPageInfo() {
		List<GoodsChannel> goodsChannelList = db().select(CHANNEL.ID, CHANNEL.GOODS_ID, GOODS.GOODS_NAME).from(CHANNEL).innerJoin(GOODS)
				.on(CHANNEL.GOODS_ID.eq(GOODS.GOODS_ID)).where(CHANNEL.SOURCE_TYPE.eq(ChannelConstant.SOURCETYPE_GOODS))
				.and(CHANNEL.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).and(GOODS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
				.fetchInto(GoodsChannel.class);
		List<ChannelPageInfo> result = new ArrayList<>();
//		根据商品ID去重
		Map<Integer,ChannelPageInfo> temp = new HashMap<>(goodsChannelList.size());
		for (GoodsChannel goodsChannel : goodsChannelList) {
			temp.put(goodsChannel.getGoodsId(), new ChannelPageInfo(goodsChannel.getId(), goodsChannel.getGoodsName()));
		}
		result.addAll(temp.values());

		List<PageChannel> pageChannelList = db().select(CHANNEL.ID, CHANNEL.PAGE_ID, XCX_CUSTOMER_PAGE.PAGE_NAME).from(CHANNEL).innerJoin(XCX_CUSTOMER_PAGE)
				.on(CHANNEL.PAGE_ID.eq(XCX_CUSTOMER_PAGE.PAGE_ID)).where(CHANNEL.SOURCE_TYPE.eq(ChannelConstant.SOURCETYPE_CUSTOMIZE))
				.and(CHANNEL.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
				.fetchInto(PageChannel.class);
//		根据页面ID去重
		temp.clear();
		for(PageChannel pageChannel:pageChannelList) {
			temp.put(pageChannel.getPageId(),new ChannelPageInfo(pageChannel.getId(),pageChannel.getPageName()));
		}
		result.addAll(temp.values());
		return result;

	}

	public ChannelRecord getChannelInfo(String share) {
		return db().selectFrom(CHANNEL).where(CHANNEL.SHARE.eq(share)).and(CHANNEL.DEL_FLAG.eq((byte)0)).fetchAny();
	}

	/**
	 * 渠道统计
	 * @param share
	 * @param userId
	 * @param type
	 */
	public void recordChannel(String share,Integer userId,Byte type) {
		ChannelRecord channelRecord = db().selectFrom(CHANNEL).where(CHANNEL.SHARE.eq(share)).fetchAny();
		Result<ChannelRecordRecord> records = db().selectFrom(CHANNEL_RECORD)
				.where(CHANNEL_RECORD.USER_ID.eq(userId).and(CHANNEL_RECORD.CHANNEL_ID.eq(channelRecord.getId()))
						.and(dateFormat(CHANNEL_RECORD.CREATE_TIME, DateUtils.DATE_MYSQL_SIMPLE).eq(DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE))))
				.fetch();
		if(records.size()>0) {
			db().update(CHANNEL_RECORD).set(CHANNEL_RECORD.COUNT, records.size() + 1)
					.where(CHANNEL_RECORD.USER_ID.eq(userId).and(CHANNEL_RECORD.CHANNEL_ID.eq(channelRecord.getId()))
							.and(dateFormat(CHANNEL_RECORD.CREATE_TIME, DateUtils.DATE_MYSQL_SIMPLE)
									.eq(DateUtils.dateFormat(DateUtils.DATE_FORMAT_SIMPLE)))
							.and(CHANNEL_RECORD.TYPE.eq((byte) 0)))
					.execute();
		}else {
			if(channelRecord!=null) {
				ChannelRecordRecord newRecord = db().newRecord(CHANNEL_RECORD);
				newRecord.setChannelId(channelRecord.getId());
				newRecord.setUserId(userId);
				newRecord.setType(type);
				newRecord.setCount(1);
				int insert = newRecord.insert();
				logger().info("插入结果：{}",insert);
			}

		}
	}
}
@Data
@NoArgsConstructor
class GoodsChannel{
	private Integer id;
	private Integer goodsId;
	private String goodsName;
}
@Data
@NoArgsConstructor
class PageChannel{
	private Integer id;
	private Integer pageId;
	private String pageName;
}
