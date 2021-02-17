package com.meidianyi.shop.service.shop.market.coopen;

import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_STATUS_DISABLE;
import static com.meidianyi.shop.common.foundation.data.BaseConstant.ACTIVITY_STATUS_NORMAL;
import static com.meidianyi.shop.service.pojo.shop.market.coopen.CoopenConstant.COUPON;
import static com.meidianyi.shop.service.pojo.shop.market.coopen.CoopenConstant.CUSTOMIZE;
import static com.meidianyi.shop.service.pojo.shop.market.coopen.CoopenConstant.DRAW;

import java.sql.Timestamp;
import java.util.List;

import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.CoopenActivity;
import com.meidianyi.shop.db.shop.tables.records.CoopenActivityRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponView;
import com.meidianyi.shop.service.pojo.shop.market.coopen.CoopenListParam;
import com.meidianyi.shop.service.pojo.shop.market.coopen.CoopenListVo;
import com.meidianyi.shop.service.pojo.shop.market.coopen.CoopenParam;
import com.meidianyi.shop.service.pojo.shop.market.coopen.CoopenVo;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.CalendarAction;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import com.meidianyi.shop.service.shop.coupon.CouponService;

/**
 * 开屏有礼(重写)
 *
 * @author kdc
 * @date 2019/11/22 13:50
 */
@Service
public class CoopenService extends ShopBaseService {
    private static CoopenActivity TABLE = CoopenActivity.COOPEN_ACTIVITY;

    @Autowired
    private CouponService couponService;
    /**
     * 列表查询
     */
    public PageResult<CoopenListVo> getPageList(CoopenListParam param) {
        SelectConditionStep<? extends Record> select = db().select(TABLE.ID,TABLE.NAME,TABLE.ACTION,TABLE.FIRST,TABLE.STATUS,
                TABLE.START_DATE,TABLE.END_DATE,TABLE.IS_FOREVER, TABLE.ACTIVITY_ACTION)
                        .from(TABLE).where(TABLE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE));
        buildOptions(select, param);
        select.orderBy(TABLE.FIRST.desc(),TABLE.ID.desc());
        PageResult<CoopenListVo> pageResult = getPageResult(select, param, CoopenListVo.class);
        pageResult.getDataList().forEach(coopen->{
            coopen.setCurrentState(Util.getActStatus(coopen.getStatus(),coopen.getStartDate(),coopen.getEndDate(),coopen.getIsForever()));
        });
        return pageResult;
    }

    /**
     * 查询条件
     */
    private void buildOptions(SelectConditionStep<? extends Record> select, CoopenListParam param) {
        if (param.getNvaType()!=null){
            switch (param.getNvaType()) {
                case BaseConstant.NAVBAR_TYPE_ONGOING:
                    select.and((TABLE.START_DATE.le(Util.currentTimeStamp()).and(TABLE.END_DATE.gt(Util.currentTimeStamp()))).or(TABLE.IS_FOREVER.eq(BaseConstant.ACTIVITY_IS_FOREVER.intValue())))
                            .and(TABLE.STATUS.eq(ACTIVITY_STATUS_NORMAL));
                    break;
                case BaseConstant.NAVBAR_TYPE_NOT_STARTED:
                    select.and(TABLE.STATUS.eq(ACTIVITY_STATUS_NORMAL))
                            .and(TABLE.START_DATE.gt(Util.currentTimeStamp()));
                    break;
                case BaseConstant.NAVBAR_TYPE_FINISHED:
                    select.and(TABLE.STATUS.eq(ACTIVITY_STATUS_NORMAL))
                            .and(TABLE.END_DATE.lt(Util.currentTimeStamp())).and(TABLE.IS_FOREVER.eq(BaseConstant.ACTIVITY_NOT_FOREVER.intValue()));
                    break;
                case BaseConstant.NAVBAR_TYPE_DISABLED:
                    select.and(TABLE.STATUS.eq(BaseConstant.ACTIVITY_STATUS_DISABLE));
                    break;
                default:
            }
        }
    }

    /**
     * 停用活动
     */
    public void disableActivity(Integer activityId) {
        db().update(TABLE).set(TABLE.STATUS, ACTIVITY_STATUS_DISABLE).where(TABLE.ID.eq(activityId)).execute();
    }

    /**
     * 启用活动
     */
    public void enableActivity(Integer id) {
        db().update(TABLE).set(TABLE.STATUS, ACTIVITY_STATUS_NORMAL).where(TABLE.ID.eq(id)).execute();
    }
    /**
     * 删除活动
     */

    public void deleteActivity(Integer activityId) {
        db().update(TABLE).set(TABLE.DEL_FLAG, DelFlag.DISABLE_VALUE).where(TABLE.ID.eq(activityId)).execute();
    }

    /**
     * 添加活动
     */
    public void addActivity(CoopenParam param) {
        validateParam(param);
        validateTimeRange(param);
        CoopenActivityRecord coopenRecord =db().newRecord(TABLE,param);
        if (param.getStartDate()==null){
            Timestamp localDateTime = DateUtils.getLocalDateTime();
            coopenRecord.setStartDate(localDateTime);
            coopenRecord.setEndDate(localDateTime);
        }
        coopenRecord.setId(null);
        coopenRecord.insert();
    }

    /**
     * 参数校验
     */
    private void validateParam(final CoopenParam param) {
        byte type = param.getActivityAction();
        switch (type) {
            case COUPON:
                Assert.notNull(param.getMrkingVoucherId(), "Missing parameter MrkingVoucherId");
                Assert.notNull(param.getTitle(), "Missing parameter Title");
                break;
            case DRAW:
                Assert.notNull(param.getLotteryId(), "Missing parameter LotteryId");
                break;
            case CUSTOMIZE:
                Assert.notNull(param.getCustomizeUrl(), "Missing parameter customizeImgUrl");
                Assert.notNull(param.getCustomizeImgPath(), "Missing parameter customizePagePath");
                break;
            default:
        }
    }

    /**
     * 修改活动
     */
    public void updateActivity(CoopenParam param) {
        validateParam(param);
        validateTimeRange(param, true);
        CoopenActivityRecord coopenRecord =db().newRecord(TABLE,param);
        coopenRecord.update();
    }

    /**
     * 检查该时段内是否有其它开屏有礼活动
     */
    private void validateTimeRange(CoopenParam param) {
        validateTimeRange(param, false);
    }

    /**
     * 检查该时段内是否有其它开屏有礼活动
     */
    private void validateTimeRange(CoopenParam param, boolean update) {
        Timestamp startDate = param.getStartDate();
        Timestamp endDate = param.getEndDate();
        SelectConditionStep<Record1<Integer>> condition = DSL.select(TABLE.ID).from(TABLE)
                .where(TABLE.STATUS.eq(ACTIVITY_STATUS_NORMAL).and(TABLE.START_DATE.ge(startDate)).and(TABLE.END_DATE.le(endDate)));
        if (update) {
            condition.and(TABLE.ID.ne(param.getId()));
        }

    }

    /**
     * 获取活动明细
     */
    public CoopenVo getActivityDetail(Integer id) {
        CoopenVo conpenVo = getActivity(id).fetchOneInto(CoopenVo.class);
        List<CouponView> couponViewByIds =couponService.getCouponViewByIds(Util.splitValueToList(conpenVo.getMrkingVoucherId()));
        conpenVo.setCouponView(couponViewByIds);
        return conpenVo;
    }

    /**
     * 获取活动
     */
    private SelectConditionStep<CoopenActivityRecord> getActivity(Integer id) {
        return db().selectFrom(TABLE).where(TABLE.ID.eq(id));
    }

    /**
     * 营销日历用id查询活动
     * @param id
     * @return
     */
    public MarketVo getActInfo(Integer id) {
		return db().select(TABLE.ID, TABLE.NAME.as(CalendarAction.ACTNAME), TABLE.START_DATE.as(CalendarAction.STARTTIME),
				TABLE.END_DATE.as(CalendarAction.ENDTIME),TABLE.IS_FOREVER.as(CalendarAction.ISPERMANENT)).from(TABLE).where(TABLE.ID.eq(id)).fetchAnyInto(MarketVo.class);
    }

    /**
     * 营销日历用查询目前正常的活动
     * @param param
     * @return
     */
	public PageResult<MarketVo> getListNoEnd(MarketParam param) {
		SelectSeekStep1<Record5<Integer, String, Timestamp, Timestamp, Integer>, Integer> select = db()
				.select(TABLE.ID, TABLE.NAME.as(CalendarAction.ACTNAME), TABLE.START_DATE.as(CalendarAction.STARTTIME),
						TABLE.END_DATE.as(CalendarAction.ENDTIME),TABLE.IS_FOREVER.as(CalendarAction.ISPERMANENT))
				.from(TABLE)
				.where(TABLE.DEL_FLAG.eq(DelFlag.NORMAL_VALUE).and(TABLE.STATUS
						.eq(BaseConstant.ACTIVITY_STATUS_NORMAL).and(TABLE.END_DATE.gt(DateUtils.getSqlTimestamp()))))
				.orderBy(TABLE.ID.desc());
		PageResult<MarketVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				MarketVo.class);
		return pageResult;
	}
}
