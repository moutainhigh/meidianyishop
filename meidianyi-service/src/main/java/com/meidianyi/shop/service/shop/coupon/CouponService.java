package com.meidianyi.shop.service.shop.coupon;

import static com.meidianyi.shop.common.foundation.util.Util.listToString;
import static com.meidianyi.shop.common.foundation.util.Util.stringToList;
import static com.meidianyi.shop.db.shop.Tables.CARD_EXAMINE;
import static com.meidianyi.shop.db.shop.Tables.CUSTOMER_AVAIL_COUPONS;
import static com.meidianyi.shop.db.shop.Tables.DIVISION_RECEIVE_RECORD;
import static com.meidianyi.shop.db.shop.Tables.GOODS;
import static com.meidianyi.shop.db.shop.Tables.MEMBER_CARD;
import static com.meidianyi.shop.db.shop.Tables.MRKING_VOUCHER;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.Tables.USER_CARD;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;
import static org.apache.commons.lang3.math.NumberUtils.BYTE_ZERO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.db.shop.tables.MrkingVoucher;
import com.meidianyi.shop.db.shop.tables.records.CustomerAvailCouponsRecord;
import com.meidianyi.shop.db.shop.tables.records.DivisionReceiveRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.MemberCardRecord;
import com.meidianyi.shop.db.shop.tables.records.MrkingVoucherRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponAllParam;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponAllVo;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponAndVoucherDetailVo;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponConstant;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponGetDetailParam;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponListParam;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponListVo;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponParam;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponParamVo;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponView;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponWxUserImportVo;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponWxVo;
import com.meidianyi.shop.service.pojo.shop.coupon.MpGetCouponParam;
import com.meidianyi.shop.service.pojo.shop.coupon.hold.CouponHoldListParam;
import com.meidianyi.shop.service.pojo.shop.coupon.hold.CouponHoldListVo;
import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.coupon.AvailCouponDetailParam;
import com.meidianyi.shop.service.pojo.wxapp.coupon.AvailCouponDetailVo;
import com.meidianyi.shop.service.pojo.wxapp.coupon.AvailCouponListVo;
import com.meidianyi.shop.service.pojo.wxapp.coupon.AvailCouponParam;
import com.meidianyi.shop.service.pojo.wxapp.coupon.AvailCouponVo;
import com.meidianyi.shop.service.pojo.wxapp.coupon.ExpireTimeVo;
import com.meidianyi.shop.service.pojo.wxapp.order.goods.OrderGoodsBo;
import com.meidianyi.shop.service.pojo.wxapp.order.marketing.coupon.OrderCouponVo;
import com.meidianyi.shop.service.shop.image.QrCodeService;

import jodd.util.StringUtil;

/**
 * 优惠券管理
 *
 * @author 常乐
 * 2019年7月16日
 */
@Service

public class CouponService extends ShopBaseService {
    @Autowired
    public CouponGiveService couponGiveService;

    @Autowired
    public CouponHoldService couponHold;

    @Autowired
    private QrCodeService qrCode;

    private String aliasCode;

	@Autowired
	public CouponMpService couponMpService;

    /**可用会员卡*/
    public static final byte COUPON_IS_USED_STATUS_AVAIL = 0;
    /**优惠券状态：使用*/
    public static final byte COUPON_IS_USED_STATUS_USED = 1;
    /**普通优惠券*/
    public static final byte COUPON_TYPE_NORMAL = 0;

    /**优惠类型 0:减价;1打折*/
    public static final byte COUPON_TYPE_ = 0;

    /**
     * 创建优惠券
     *
     * @param couponInfo
     * @return
     */
    public Boolean couponAdd(CouponParam couponInfo) {
        MrkingVoucherRecord record = new MrkingVoucherRecord();
        record.setSurplus(couponInfo.getTotalAmount());
        record.setAliasCode(this.generateAliasCode());
        this.assign(couponInfo, record);
        return db().executeInsert(record) > 0 ? true : false;
    }


    /**
     * 生成优惠券唯一活动吗
     *
     * @return
     */
    public String generateAliasCode() {
        do {
            int randomNum = new Random().nextInt(10000000) + 10000000;
            this.aliasCode = "b" + randomNum;
        } while (this.hasAliasCode(aliasCode) > 0);
        return aliasCode;
    }

    /**
     * 判断优惠券唯一活动码是否存在
     *
     * @return
     */
    public int hasAliasCode(String aliasCode) {
        int res = db().selectCount().from(MRKING_VOUCHER).where(MRKING_VOUCHER.ALIAS_CODE.eq(aliasCode)).fetchOne().into(Integer.class);
        return res;
    }

    /**
     * 获取优惠券分页列表
     *
     * @param param
     * @return
     */
    public PageResult<CouponListVo> getCouponList(CouponListParam param) {
        SelectJoinStep<Record> select = db().select().from(MRKING_VOUCHER);

        //条件查询
        SelectConditionStep<Record> sql = buildOptions(select, param);
        sql.orderBy(MRKING_VOUCHER.CREATE_TIME.desc());
        PageResult<CouponListVo> couponList = this.getPageResult(sql, param.getCurrentPage(), param.getPageRows(), CouponListVo.class);
        for (CouponListVo list : couponList.dataList) {
            int used = db().selectCount().from(CUSTOMER_AVAIL_COUPONS).where(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(list.getId())).and(CUSTOMER_AVAIL_COUPONS.IS_USED.eq((byte) 1)).fetchOne().into(Integer.class);
            list.setUsed(used);
            if(list.getValidityType().equals(BaseConstant.COUPON_VALIDITY_TYPE_FIXED)){
                list.setCurrentState(Util.getActStatus(list.getEnabled(),list.getStartTime(),list.getEndTime()));
            }else {
                list.setCurrentState(Util.getActStatus(list.getEnabled(),list.getStartTime(),list.getEndTime(),BaseConstant.COUPON_VALIDITY_TYPE_FLEXIBLE));
            }
        }
        return couponList;
    }

    /**
     * 优惠券列表根据条件筛选
     *
     * @param select
     * @param param
     * @return
     */
    public SelectConditionStep<Record> buildOptions(SelectJoinStep<Record> select, CouponListParam param) {
        SelectConditionStep<Record> sql = select.where(MRKING_VOUCHER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE));
        if(param.getCouponType() != 2){
            sql = sql.and(MRKING_VOUCHER.TYPE.eq(param.getCouponType()));
        }
        if (param.getActName() != null) {
            sql = sql.and(MRKING_VOUCHER.ACT_NAME.contains(param.getActName()));
        }

        Timestamp nowDate = new Timestamp(System.currentTimeMillis());

        if (param.getNav() != null && param.getNav() > 0) {
            switch (param.getNav()) {
                //进行中
                case 1:
                    sql = sql.and((MRKING_VOUCHER.START_TIME.le(nowDate)).and(MRKING_VOUCHER.END_TIME.ge(nowDate)).or(MRKING_VOUCHER.VALIDITY_TYPE.eq(BaseConstant.COUPON_VALIDITY_TYPE_FLEXIBLE)))
                            .and(MRKING_VOUCHER.ENABLED.eq(BaseConstant.COUPON_ENABLED_NORMAL));
                    break;
                //未开始
                case 2:
                    sql = sql.and(MRKING_VOUCHER.START_TIME.ge(nowDate)).and(MRKING_VOUCHER.ENABLED.eq(BaseConstant.COUPON_ENABLED_NORMAL)).and(MRKING_VOUCHER.ENABLED.eq(BaseConstant.COUPON_ENABLED_NORMAL)).and(MRKING_VOUCHER.VALIDITY_TYPE.eq(BaseConstant.COUPON_VALIDITY_TYPE_FIXED));
                    break;
                //已过期
                case 3:
                    sql = sql.and(MRKING_VOUCHER.END_TIME.le(nowDate)).and(MRKING_VOUCHER.ENABLED.eq(BaseConstant.COUPON_ENABLED_NORMAL)).and(MRKING_VOUCHER.VALIDITY_TYPE.eq(BaseConstant.COUPON_VALIDITY_TYPE_FIXED));
                    break;
                //已停用
                case 4:
                    sql = sql.and(MRKING_VOUCHER.ENABLED.eq(BaseConstant.COUPON_ENABLED_DISABLED));
                    break;
                default:

            }
        }
        return sql;
    }

    /**
     * 获取单条优惠券信息
     *
     * @param couponId
     * @return
     */
    public List<CouponParamVo> getOneCouponInfo(Integer couponId) {
		/** 单条返回列表，之前写的人写的有问题，前端调用太多，有缘人改吧*/
		List<CouponParamVo> list = db().selectFrom(MRKING_VOUCHER).where(MRKING_VOUCHER.ID.eq(couponId))
				.fetchInto(CouponParamVo.class);
		for (CouponParamVo item : list) {
			item.setStatus(couponMpService.couponGetStatus(new MpGetCouponParam(couponId, null)));
		}
		return list;
    }

    /**
     * 获取单个优惠券信息
     *
     * @param couponId
     * @return
     */
    public MrkingVoucherRecord getOneCouponById(Integer couponId) {
        return db().selectFrom(MRKING_VOUCHER).where(MRKING_VOUCHER.ID.eq(couponId)).fetchAny();
    }

    /**
     * 根据优惠券id对应的优惠券所关联的商品和商家分类id获取对应的过滤条件
     * @param couponId 优惠券id
     * @return 过滤条件
     */
    public Condition buildGoodsSearchCondition(Integer couponId) {
        MrkingVoucherRecord voucherRecord = getOneCouponById(couponId);
        if (voucherRecord == null) {
            logger().debug("优惠券跳转商品搜索页-优惠券id无效");
            return DSL.falseCondition();
        } else {
            Condition condition = DSL.noCondition();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(voucherRecord.getRecommendGoodsId()) || org.apache.commons.lang3.StringUtils.isNotBlank(voucherRecord.getRecommendSortId())) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(voucherRecord.getRecommendGoodsId())) {
                    List<Integer> goodsIds = Arrays.stream(voucherRecord.getRecommendGoodsId().split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
                    condition = condition.or(GOODS.GOODS_ID.in(goodsIds));
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(voucherRecord.getRecommendSortId())) {
                    List<Integer> sortIds = Arrays.stream(voucherRecord.getRecommendSortId().split(",")).mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
                    condition = condition.or(GOODS.SORT_ID.in(sortIds));
                }
            }
            return condition;
        }
    }
    /**
     * 保存编辑信息
     *
     * @param param
     * @return
     */
    public boolean saveCouponInfo(CouponParam param) {
        MrkingVoucherRecord record = new MrkingVoucherRecord();
        this.assign(param, record);
        return db().executeUpdate(record) > 0 ? true : false;
    }

    /**
     * 停用优惠券
     *
     * @param couponId
     * @return
     */
    public boolean couponPause(Integer couponId) {
        int result = db().update(MRKING_VOUCHER)
            .set(MRKING_VOUCHER.ENABLED, BaseConstant.COUPON_ENABLED_DISABLED)
            .where(MRKING_VOUCHER.ID.eq(couponId))
            .execute();
        return result > 0 ? true : false;
    }

    /**
     * 启用优惠券
     *
     * @param couponId
     * @return
     */
    public boolean couponOpen(Integer couponId) {
        int result = db().update(MRKING_VOUCHER)
                .set(MRKING_VOUCHER.ENABLED, BaseConstant.COUPON_ENABLED_NORMAL)
                .where(MRKING_VOUCHER.ID.eq(couponId))
                .execute();
        return result > 0 ? true : false;
    }

    /**
     * 删除优惠券（假删除）
     *
     * @param couponId
     * @return
     */
    public boolean couponDel(Integer couponId) {
        int result = db().update(MRKING_VOUCHER)
            .set(MRKING_VOUCHER.DEL_FLAG, DelFlag.DISABLE_VALUE)
            .where(MRKING_VOUCHER.ID.eq(couponId))
            .execute();
        return result > 0 ? true : false;
    }

    /**
     * 优惠券领取明细分页列表
     *
     * @param param
     * @return
     */
    public PageResult<CouponHoldListVo> getDetail(CouponGetDetailParam param) {
        CouponHoldListParam couponParam = new CouponHoldListParam();
        couponParam.setActId(param.getId());
        couponParam.setCouponType(param.getCouponType());
        couponParam.setMobile(param.getMobile());
        couponParam.setUsername(param.getUserName());
        couponParam.setStatus(param.getIsUsed());
        couponParam.setCurrentPage(param.getCurrentPage());
        couponParam.setPageRows(param.getPageRows());
        return couponHold.getCouponHoldList(couponParam);

    }

    /**
     * 领取分裂优惠券用户详情
     * @param param
     * @return
     */
    public PageResult<CouponHoldListVo> getSplitCoupinUserDetail(CouponGetDetailParam param){
        SelectConditionStep<? extends Record> select = db()
            .select(CUSTOMER_AVAIL_COUPONS.ID,USER.USERNAME, USER.MOBILE, CUSTOMER_AVAIL_COUPONS.GET_SOURCE, CUSTOMER_AVAIL_COUPONS.IS_USED,
                MRKING_VOUCHER.ACT_CODE, MRKING_VOUCHER.DENOMINATION, CUSTOMER_AVAIL_COUPONS.START_TIME, CUSTOMER_AVAIL_COUPONS.END_TIME,CUSTOMER_AVAIL_COUPONS.USED_TIME,CUSTOMER_AVAIL_COUPONS.DEL_FLAG,
                    CUSTOMER_AVAIL_COUPONS.CREATE_TIME,CUSTOMER_AVAIL_COUPONS.AMOUNT).from(DIVISION_RECEIVE_RECORD)
            .leftJoin(CUSTOMER_AVAIL_COUPONS).on(DIVISION_RECEIVE_RECORD.COUPON_SN.eq(CUSTOMER_AVAIL_COUPONS.COUPON_SN))
            .leftJoin(USER).on(CUSTOMER_AVAIL_COUPONS.USER_ID.eq(USER.USER_ID))
            .leftJoin(MRKING_VOUCHER).on(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(MRKING_VOUCHER.ID))
            .where(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(param.getId())).and(DIVISION_RECEIVE_RECORD.USER.eq(param.getShareId())).and(DIVISION_RECEIVE_RECORD.RECEIVE_COUPON_SN.eq(param.getCouponSn()));
        SelectConditionStep<? extends Record> sql = detailBuildOptions(select, param);
        PageResult<CouponHoldListVo> info = this.getPageResult(sql, param.getCurrentPage(), param.getPageRows(), CouponHoldListVo.class);
        info.dataList.forEach(data->{
            //优惠卷的金额在领取记录里
            data.setDenomination(data.getAmount());
        });
        return info;
    }

    /**
     * 领取分裂优惠券用户详情列表条件查询
     * @param select
     * @param param
     * @return
     */
    public SelectConditionStep<? extends Record> detailBuildOptions(SelectConditionStep<? extends Record> select,CouponGetDetailParam param){
        //手机号
        if(StringUtil.isNotEmpty(param.getMobile())) {
            select.and(USER.MOBILE.like(this.likeValue(param.getMobile())));
        }
        //用户昵称
        if(StringUtil.isNotEmpty(param.getUserName())) {
            select.and(USER.USERNAME.like(this.likeValue(param.getUserName())));
        }
        //使用状态 1 未使用 2 使用 3 过期 4 废除
        if(param.getIsUsed() != null) {
            Timestamp nowTime =new Timestamp(System.currentTimeMillis());
            if (param.getIsUsed()==1){
                select.and(CUSTOMER_AVAIL_COUPONS.IS_USED.eq((byte) 0))
                    .and(CUSTOMER_AVAIL_COUPONS.END_TIME.ge(nowTime))
                    .and(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq((byte) 0));
            }else if (param.getIsUsed()==2){
                select.and(CUSTOMER_AVAIL_COUPONS.IS_USED.eq((byte) 1))
                    .and(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq((byte) 0));
            }else if (param.getIsUsed()==3){
                select.and(CUSTOMER_AVAIL_COUPONS.IS_USED.eq((byte) 0))
                    .and(CUSTOMER_AVAIL_COUPONS.END_TIME.lt(nowTime))
                    .and(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq((byte) 0));
            }else if (param.getIsUsed()==4){
                select.and(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq((byte) 1));
            }
        }
        return select;
    }

    /**
     * 取单个优惠券的基本信息
     *
     * @param id
     * @return
     */
	public CouponView getCouponViewById(int id) {
		CouponView into = db().selectFrom(MRKING_VOUCHER).where(MRKING_VOUCHER.ID.eq(id))
				.and(MRKING_VOUCHER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).fetchOneInto(CouponView.class);
		if(null!=into) {
			into.setStatus(couponMpService.couponGetStatus(new MpGetCouponParam(id, null)));
		}
		return into;
	}

    /**
     * 根据id批量获取优惠卷信息
     *
     * @param ids
     * @return
     */
    public List<CouponView> getCouponViewByIds(List<Integer> ids) {
    	List<CouponView> list = db().select(MRKING_VOUCHER.ID, MRKING_VOUCHER.ACT_NAME,MRKING_VOUCHER.ACT_CODE, MRKING_VOUCHER.DENOMINATION,
                MRKING_VOUCHER.USE_CONSUME_RESTRICT, MRKING_VOUCHER.LEAST_CONSUME, MRKING_VOUCHER.SURPLUS,
                MRKING_VOUCHER.VALIDITY_TYPE, MRKING_VOUCHER.START_TIME, MRKING_VOUCHER.END_TIME, MRKING_VOUCHER.VALIDITY,
                MRKING_VOUCHER.VALIDITY_HOUR, MRKING_VOUCHER.VALIDITY_MINUTE,MRKING_VOUCHER.RANDOM_MAX,MRKING_VOUCHER.RANDOM_MIN,MRKING_VOUCHER.SUIT_GOODS)
            .from(MRKING_VOUCHER)
            .where(MRKING_VOUCHER.ID.in(ids)).and(MRKING_VOUCHER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
            .fetchInto(CouponView.class);
    	for (CouponView couponView : list) {
    		couponView.setStatus(couponMpService.couponGetStatus(new MpGetCouponParam(couponView.getId(), null)));
		}
        return list;
    }

    /**
     * 用户优惠券列表
     *
     * @param param
     * @return
     */
    public PageResult<AvailCouponVo> getCouponByUser(AvailCouponParam param) {
        //某用户全部优惠券
        SelectJoinStep<? extends Record> select = db().select(CUSTOMER_AVAIL_COUPONS.ID,CUSTOMER_AVAIL_COUPONS.ACT_ID,CUSTOMER_AVAIL_COUPONS.COUPON_SN, CUSTOMER_AVAIL_COUPONS.TYPE, CUSTOMER_AVAIL_COUPONS.AMOUNT, CUSTOMER_AVAIL_COUPONS.START_TIME,
            CUSTOMER_AVAIL_COUPONS.END_TIME, CUSTOMER_AVAIL_COUPONS.IS_USED, CUSTOMER_AVAIL_COUPONS.LIMIT_ORDER_AMOUNT, MRKING_VOUCHER.ACT_NAME,MRKING_VOUCHER.RECOMMEND_GOODS_ID,MRKING_VOUCHER.RECOMMEND_CAT_ID,MRKING_VOUCHER.RECOMMEND_SORT_ID,
           MRKING_VOUCHER.CARD_ID,MRKING_VOUCHER.TYPE.as("couponType"),MRKING_VOUCHER.ACT_CODE,CUSTOMER_AVAIL_COUPONS.DIVISION_ENABLED,MRKING_VOUCHER.RECEIVE_PER_NUM,MRKING_VOUCHER.RECEIVE_NUM,MRKING_VOUCHER.RANDOM_MAX,MRKING_VOUCHER.COUPON_OVERLAY)
            .from(CUSTOMER_AVAIL_COUPONS
                .leftJoin(MRKING_VOUCHER).on(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(MRKING_VOUCHER.ID)));

        //根据优惠券使用状态、过期状态条件筛选
        mpBuildOptions(select, param);
        PageResult<AvailCouponVo> lists = getPageResult(select, param.getCurrentPage(), param.getPageRows(), AvailCouponVo.class);
        for (AvailCouponVo list:lists.dataList){
            //0不可以分享；1可以分享
            list.setCanShare(0);
            //分裂优惠券属性
            if(list.getCouponType() == 1){
                Record record = db().select().from(DIVISION_RECEIVE_RECORD).where(DIVISION_RECEIVE_RECORD.USER.eq(param.getUserId()))
                    .and(DIVISION_RECEIVE_RECORD.COUPON_SN.eq(list.getCouponSn())).and(DIVISION_RECEIVE_RECORD.TYPE.eq((byte) 0))
                    .fetchOne();
                if(record != null){
                    DivisionReceiveRecordRecord into = record.into(DivisionReceiveRecordRecord.class);
                    //发放人
                    list.setIsGrant(1);
                    //0:未分享；1：已分享
                    list.setIsShare(into.getIsShare());
                }else{
                    list.setIsShare((byte)0);
                    //被发放
                    list.setIsGrant(0);
                }

                int hasReceive = hasReceive(param.getUserId(), list.getCouponSn());
                //判断分裂优惠券是否限制领取 0：不限制；1限制 和 已领取数是否大于限制数
                if(!(list.getReceivePerNum() == 1 && hasReceive >= list.getReceiveNum())){
                    list.setCanShare(1);
                }
            }
            ExpireTimeVo remain = getExpireTime(list.getEndTime());
            if(remain != null){
                list.setRemainDays(remain.getRemainDays());
                list.setRemainHours(remain.getRemainHours());
                list.setRemainMinutes(remain.getRemainMinutes());
                list.setRemainSeconds(remain.getRemainSeconds());
            }
        }
        return lists;
    }

    /**
     * 优惠券到期倒计时
     * @param  endDate 到期时间
     * @return
     */
    public ExpireTimeVo getExpireTime(Timestamp endDate){
        //当前时间戳
        long time = System.currentTimeMillis();
        //优惠券到期时间戳
        long time1 = endDate.getTime();
        //还剩总过期秒数
        long remainSecondsAll = time1 - time;
        if(remainSecondsAll > 0){
            //剩余天数
            long remainDays = remainSecondsAll / (24 * 3600 * 1000);
            //去除天数的剩余秒
            long dSeconds = remainSecondsAll % (24 * 3600 * 1000);

            //剩余小时
            long remainHours = dSeconds / (3600 *1000);
            //去除小时剩余秒数
            long hSeconds = dSeconds % (3600 * 1000);

            //剩余分钟数
            long remainMinutes = hSeconds / (60 * 1000);
            //去除分钟剩余秒数
            long remainSeconds = dSeconds % (60 * 1000) / 1000;
            ExpireTimeVo expireTime = new ExpireTimeVo();
            expireTime.setRemainDays(remainDays);
            expireTime.setRemainHours(remainHours);
            expireTime.setRemainMinutes(remainMinutes);
            expireTime.setRemainSeconds(remainSeconds);
            return expireTime;
        }else{
            return null;
        }
    }

    /**
     * 用户优惠券状态筛选条件
     * @param select
     * @param param
     */
    public void mpBuildOptions(SelectJoinStep<? extends Record> select, AvailCouponParam param) {
    	Byte isUsed = param.getNav();
    	Timestamp now = Timestamp.valueOf(LocalDateTime.now());
    	if(isUsed == 0 || isUsed == 1) {  //未使用、已使用状态
    		select.where(CUSTOMER_AVAIL_COUPONS.IS_USED.eq(isUsed).and(CUSTOMER_AVAIL_COUPONS.END_TIME.ge(now)));
    		if(isUsed == 0){
                select.where(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq((byte)0));
            }
    	}else {  //已过期状态
    		//根据有效 开始时间、结束时间判断
            select.where(CUSTOMER_AVAIL_COUPONS.END_TIME.le(now));
            select.where(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq((byte)0));
    	}
    	if(param.getUserId() != null) {
            select.where(CUSTOMER_AVAIL_COUPONS.USER_ID.eq(param.getUserId()));
        }
        if(org.apache.commons.lang3.StringUtils.isNotBlank(param.getCouponSn())) {
            select.where(CUSTOMER_AVAIL_COUPONS.COUPON_SN.eq(param.getCouponSn()));
        }
        if(param.getIsShowEnabledShareSplit() != null) {
            select.where(CUSTOMER_AVAIL_COUPONS.DIVISION_ENABLED.eq(param.getIsShowEnabledShareSplit()));
        }
    	select.orderBy(CUSTOMER_AVAIL_COUPONS.ID.desc());
    }

    /**
     * 优惠券详情
     *
     * @param param ：couponSn优惠券编码
     * @return
     */
    public AvailCouponDetailVo getCouponDetail(AvailCouponDetailParam param) {
        Record record;
        if(param.getCouponId() != null){
             record = db().select().from(MRKING_VOUCHER)
                .where(MRKING_VOUCHER.ID.eq(param.couponId))
                .fetchOne();
        }else{
             record = db().select(CUSTOMER_AVAIL_COUPONS.ID, CUSTOMER_AVAIL_COUPONS.ACT_ID,CUSTOMER_AVAIL_COUPONS.COUPON_SN, CUSTOMER_AVAIL_COUPONS.TYPE, CUSTOMER_AVAIL_COUPONS.AMOUNT, CUSTOMER_AVAIL_COUPONS.START_TIME,
                CUSTOMER_AVAIL_COUPONS.END_TIME, CUSTOMER_AVAIL_COUPONS.IS_USED, CUSTOMER_AVAIL_COUPONS.LIMIT_ORDER_AMOUNT, MRKING_VOUCHER.ACT_NAME,MRKING_VOUCHER.USE_SCORE,MRKING_VOUCHER.SCORE_NUMBER,MRKING_VOUCHER.LEAST_CONSUME,
                MRKING_VOUCHER.RECOMMEND_GOODS_ID,MRKING_VOUCHER.RECOMMEND_CAT_ID,MRKING_VOUCHER.RECOMMEND_SORT_ID,MRKING_VOUCHER.USE_CONSUME_RESTRICT,MRKING_VOUCHER.USE_EXPLAIN,MRKING_VOUCHER.VALIDATION_CODE,MRKING_VOUCHER.CARD_ID,MRKING_VOUCHER.TYPE.as("couponType"),
                 MRKING_VOUCHER.RECEIVE_NUM,MRKING_VOUCHER.RECEIVE_PER_NUM,MRKING_VOUCHER.RANDOM_MAX,CUSTOMER_AVAIL_COUPONS.DIVISION_ENABLED,MRKING_VOUCHER.ACT_CODE,MRKING_VOUCHER.COUPON_OVERLAY,MRKING_VOUCHER.VALIDATION_CODE,CUSTOMER_AVAIL_COUPONS.ACCESS_MODE,CUSTOMER_AVAIL_COUPONS.CREATE_TIME,CUSTOMER_AVAIL_COUPONS.ACCESS_ID)
                .from(CUSTOMER_AVAIL_COUPONS
                    .leftJoin(MRKING_VOUCHER).on(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(MRKING_VOUCHER.ID)))
                .where(CUSTOMER_AVAIL_COUPONS.COUPON_SN.eq(param.couponSn))
                .fetchOne();
        }
        if(record != null){
            AvailCouponDetailVo list = record.into(AvailCouponDetailVo.class);
            if(param.getCouponId() == null) {
                ExpireTimeVo remain = getExpireTime(list.getEndTime());
                if (remain != null) {
                    list.setRemainDays(remain.getRemainDays());
                    list.setRemainHours(remain.getRemainHours());
                    list.setRemainMinutes(remain.getRemainMinutes());
                    list.setRemainSeconds(remain.getRemainSeconds());
                }
            }
            //常用链接-领取优惠券
            if(param.getCouponId() != null){
                //优惠券规则
                Integer perNum = db().select(MRKING_VOUCHER.RECEIVE_PER_PERSON).from(MRKING_VOUCHER).where(MRKING_VOUCHER.ID.eq(param.couponId)).fetchOne().into(Integer.class);
                Integer hasNum = db().selectCount().from(CUSTOMER_AVAIL_COUPONS).where(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(param.couponId)).fetchOne().into(Integer.class);
                boolean canReceive = perNum == 0 || (perNum != 0 && hasNum < perNum);
                if(canReceive){
                    list.setCanReceive(1);
                }
                list.setLinkSource(1);
            }
            if(param.getCouponId() == null) {
                list.setCanShare(0); //0不可以分享；1可以分享
                if (list.getCouponType() == 1) {
                    DivisionReceiveRecordRecord canShare = isCanShare(list.getCouponSn());
                    if(canShare != null) {
                        list.setIsShare(canShare.getIsShare());
                    } else {
                        list.setIsShare((byte)0);
                    }
                    int hasReceive = hasReceive(param.getUserId(), param.couponSn);
                    if (!(list.getReceivePerNum() == 1 && hasReceive >= list.getReceiveNum())) {
                        list.setCanShare(1);
                    }
                }
            }
            return list;
        }else{
            return null;
        }
    }

    /**
     * 判断分裂优惠券是否可以分享
     * @param couponSn
     * @return
     */
    public DivisionReceiveRecordRecord isCanShare(String couponSn){
        Record record = db().select().from(DIVISION_RECEIVE_RECORD).where(DIVISION_RECEIVE_RECORD.COUPON_SN.eq(couponSn)).and(DIVISION_RECEIVE_RECORD.TYPE.eq((byte) 0)).fetchOne();
        if(record != null){
            return record.into(DivisionReceiveRecordRecord.class);
        }else{
            return null;
        }
    }

    /**
     * 单用户分裂优惠券已领取数
     * @param userId
     * @param couponSn
     * @return
     */
    public int hasReceive(Integer userId,String couponSn){
        Integer hasRecivie = db().selectCount().from(DIVISION_RECEIVE_RECORD).where(DIVISION_RECEIVE_RECORD.COUPON_SN.eq(couponSn))
            .and(DIVISION_RECEIVE_RECORD.USER.eq(userId)).and(DIVISION_RECEIVE_RECORD.TYPE.eq((byte) 1)).fetchOne().into(Integer.class);
        return hasRecivie;
    }

    /**
     * 分裂优惠券已领取数
     * @param userId
     * @return
     */
    public int hasReceive(Integer userId,Integer couponId){
        Result<Record1<Integer>> fetch = db().select(DIVISION_RECEIVE_RECORD.USER_ID).from(DIVISION_RECEIVE_RECORD)
            .where(DIVISION_RECEIVE_RECORD.COUPON_ID.eq(couponId))
            .and(DIVISION_RECEIVE_RECORD.USER.eq(userId))
            .groupBy(DIVISION_RECEIVE_RECORD.USER_ID)
            .fetch();
        int hasReceive = fetch.size();
        return hasReceive;
    }

    /**
     * 积分兑换优惠券，兑换详情页
     * @param param
     * @return
     */
    public AvailCouponDetailVo getCouponDetailByScore(AvailCouponDetailParam param){
        ArrayList getCard = new ArrayList();
        Record record = db().select(MRKING_VOUCHER.ID,MRKING_VOUCHER.ACT_NAME,MRKING_VOUCHER.ACT_CODE,MRKING_VOUCHER.DENOMINATION, MRKING_VOUCHER.USE_SCORE,MRKING_VOUCHER.SCORE_NUMBER,MRKING_VOUCHER.VALIDITY_TYPE,MRKING_VOUCHER.VALIDITY,
            MRKING_VOUCHER.VALIDITY_HOUR,MRKING_VOUCHER.VALIDITY_MINUTE,MRKING_VOUCHER.START_TIME,MRKING_VOUCHER.END_TIME,MRKING_VOUCHER.RECOMMEND_GOODS_ID,
            MRKING_VOUCHER.RECOMMEND_CAT_ID,MRKING_VOUCHER.RECOMMEND_SORT_ID,MRKING_VOUCHER.USE_CONSUME_RESTRICT,MRKING_VOUCHER.LEAST_CONSUME,MRKING_VOUCHER.CARD_ID,MRKING_VOUCHER.USE_EXPLAIN,MRKING_VOUCHER.COUPON_OVERLAY,MRKING_VOUCHER.VALIDATION_CODE)
            .from(MRKING_VOUCHER).where(MRKING_VOUCHER.ID.eq(param.getCouponId())).fetchOne();
        if(record != null){
            AvailCouponDetailVo info = record.into(AvailCouponDetailVo.class);
            List<Integer> cardIds = stringToList(info.getCardId());
            //0：不能直接领取；1：可以直接领取
            int cardStatus = 0;
            //判断用户-会员卡详情
            for(Integer cardId : cardIds){
                MemberCardRecord cardInfo = db().select().from(MEMBER_CARD).where(MEMBER_CARD.ID.eq(cardId)).fetchOne().into(MemberCardRecord.class);
                //用户是否拥有该会员卡
                if(cardInfo.getExamine() == 1){
                    Record record1 = db().select().from(USER_CARD.leftJoin(CARD_EXAMINE).on(USER_CARD.CARD_ID.eq(CARD_EXAMINE.CARD_ID)))
                        .where(USER_CARD.USER_ID.eq(param.userId))
                        .and(USER_CARD.CARD_ID.eq(cardId))
                        .and(CARD_EXAMINE.STATUS.eq((byte) 2))
                        .fetchOne();
                    if(!Objects.isNull(record1)){
                        cardStatus = 1;
                    }else{//没有该会员卡，需要先领取会员卡
                        if(cardInfo.getCardType()!=2){
                            getCard.add(cardId);
                        }
                    }
                }else{
                    Record record1 = db().select().from(USER_CARD)
                        .where(USER_CARD.USER_ID.eq(param.userId))
                        .and(USER_CARD.CARD_ID.eq(cardId))
                        .fetchOne();
                    if(!Objects.isNull(record1)){
                        cardStatus = 1;
                    }else{//没有该会员卡，需要先领取
                        if(cardInfo.getCardType()!=2){
                            getCard.add(cardId);
                        }
                    }
                }
            }
            String needGetCard = listToString(getCard);
            info.setCardStatus(cardStatus);
            info.setNeedGetCard(needGetCard);
            return info;
        }else{
            return null;
        }
    }

    /**
     * 获取各状态下优惠券数量
     * @param userId
     */
    public AvailCouponListVo getEachStatusNum(Integer userId,AvailCouponListVo red){
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        System.out.println(userId);
        //未使用
        Integer unused = db().selectCount().from(CUSTOMER_AVAIL_COUPONS).where(CUSTOMER_AVAIL_COUPONS.USER_ID.eq(userId).and(CUSTOMER_AVAIL_COUPONS.IS_USED.eq((byte) 0)
                .and(CUSTOMER_AVAIL_COUPONS.END_TIME.gt(now)))).and(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq((byte)0))
                .fetch().get(0).into(Integer.class);
        //已使用
        Integer used = db().selectCount().from(CUSTOMER_AVAIL_COUPONS).where(CUSTOMER_AVAIL_COUPONS.USER_ID.eq(userId).and(CUSTOMER_AVAIL_COUPONS.IS_USED.eq((byte) 1)
                .and(CUSTOMER_AVAIL_COUPONS.END_TIME.gt(now))))
                .fetch().get(0).into(Integer.class);
        //已过期
        Integer expire = db().selectCount().from(CUSTOMER_AVAIL_COUPONS).where(CUSTOMER_AVAIL_COUPONS.USER_ID.eq(userId).and(CUSTOMER_AVAIL_COUPONS.END_TIME.le(now)))
            .and(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq((byte)0))
            .fetch().get(0).into(Integer.class);

        red.setUnusedNum(unused);
        red.setUsedNum(used);
        red.setExpiredNum(expire);
        return red;
    }


    /**
     * 获得可使用的优惠券数
     *
     * @param userId
     * @return
     */
    public Integer getCanUseCouponNum(Integer userId) {
        Result<Record1<Integer>> record = db().selectCount().from(CUSTOMER_AVAIL_COUPONS)
            .where(CUSTOMER_AVAIL_COUPONS.USER_ID.eq(userId).and(CUSTOMER_AVAIL_COUPONS.IS_USED.eq((byte) 0)
            		.and(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .and(CUSTOMER_AVAIL_COUPONS.END_TIME.gt(DateUtils.getSqlTimestamp()))))
            .fetch();
        return record.get(0).into(Integer.class);
    }

    /**
     * 获取商品可展示的最紧密的优惠券
     * 现根据商品act_code类型排序voucher是减金额，discount打折，目前逻辑是先金额，金额减的多的靠前，然后再是打折
     * @param goodsId
     */
    public MrkingVoucherRecord getGoodsCouponFirst(Integer goodsId, Integer catId, Integer sortId, Byte suit) {
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        // 时间限时条件拼接
        // 第一种指定开始结束时间优惠券
        Condition dateCondition = MRKING_VOUCHER.END_TIME.ge(now);
        // 第二种从领取日指定可使用时间
        Condition limitTimeCondition = MRKING_VOUCHER.VALIDITY.gt(0).or(MRKING_VOUCHER.VALIDITY_HOUR.gt(0)).or(MRKING_VOUCHER.VALIDITY_MINUTE.gt(0));
        Condition timeCondition = dateCondition.or(limitTimeCondition);

        // 优惠券指定的关系条件过滤
        Condition recommendNullCondition = MRKING_VOUCHER.RECOMMEND_GOODS_ID.isNull().and(MRKING_VOUCHER.RECOMMEND_CAT_ID.isNull()).and(MRKING_VOUCHER.RECOMMEND_SORT_ID.isNull());
        Condition recommendCondition = DslPlus.findInSet(goodsId, MRKING_VOUCHER.RECOMMEND_GOODS_ID);
        if (catId != null) {
            recommendCondition = recommendCondition.or(DslPlus.findInSet(catId, MRKING_VOUCHER.RECOMMEND_CAT_ID));
        }
        if (sortId != null) {
            recommendCondition = recommendCondition.or(DslPlus.findInSet(sortId, MRKING_VOUCHER.RECOMMEND_SORT_ID));
        }
        recommendCondition = recommendNullCondition.or(recommendCondition);

        // 优惠券基本条件过滤
        Condition otherCondition = MRKING_VOUCHER.ENABLED.eq(BaseConstant.COUPON_ENABLED_NORMAL).and(MRKING_VOUCHER.SURPLUS.gt(0))
            .and(MRKING_VOUCHER.TYPE.eq(BaseConstant.COUPON_TYPE_NORMAL)).and(MRKING_VOUCHER.DEL_FLAG.eq(DelFlag.NORMAL.getCode()));
        if (suit != null) {
            otherCondition.and(MRKING_VOUCHER.SUIT_GOODS.eq(suit));
        }
        // 现根据商品act_code类型排序voucher是减金额，discount打折，目前逻辑是先金额，金额减的多的靠前，然后再是打折
        MrkingVoucherRecord mrkingVoucherRecord =
            db().selectFrom(MRKING_VOUCHER).where(timeCondition.and(otherCondition).and(recommendCondition))
            .orderBy(MRKING_VOUCHER.ACT_CODE.desc(),MRKING_VOUCHER.DENOMINATION,MRKING_VOUCHER.CREATE_TIME.desc()).fetchAny();

        return mrkingVoucherRecord;
    }

    /**
     * 获取所有可用给的优惠卷
     * @param param 是否限制库存,启用时间
     * @return
     */
    public List<CouponAllVo> getCouponAll(CouponAllParam param) {
        Timestamp nowDate = new Timestamp(System.currentTimeMillis());
        SelectConditionStep<Record6<Integer, String, String, Byte, Integer, Byte>> couponAllVos = db()
                .select(MRKING_VOUCHER.ID, MRKING_VOUCHER.ACT_NAME, MRKING_VOUCHER.ALIAS_CODE, MRKING_VOUCHER.TYPE, MRKING_VOUCHER.SURPLUS, MRKING_VOUCHER.LIMIT_SURPLUS_FLAG)
                .from(MRKING_VOUCHER)
                .where(MRKING_VOUCHER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE));
        switch (param.getStatus()){
            case BaseConstant.NAVBAR_TYPE_AVAILABLE:
                couponAllVos.and((MRKING_VOUCHER.END_TIME.ge(nowDate))
                        .or(MRKING_VOUCHER.VALIDITY_TYPE.eq(BaseConstant.COUPON_VALIDITY_TYPE_FLEXIBLE)))
                        .and(MRKING_VOUCHER.ENABLED.eq(BaseConstant.COUPON_ENABLED_NORMAL));
                break;
            case  BaseConstant.NAVBAR_TYPE_ONGOING:
                couponAllVos.and((MRKING_VOUCHER.START_TIME.le(nowDate))
                        .and(MRKING_VOUCHER.END_TIME.ge(nowDate))
                        .or(MRKING_VOUCHER.VALIDITY_TYPE.eq(BaseConstant.COUPON_VALIDITY_TYPE_FLEXIBLE)))
                        .and(MRKING_VOUCHER.ENABLED.eq(BaseConstant.COUPON_ENABLED_NORMAL));
                break;
            default:
                couponAllVos.and((MRKING_VOUCHER.START_TIME.le(nowDate))
                        .and(MRKING_VOUCHER.END_TIME.ge(nowDate))
                        .or(MRKING_VOUCHER.VALIDITY_TYPE.eq(BaseConstant.COUPON_VALIDITY_TYPE_FLEXIBLE)))
                        .and(MRKING_VOUCHER.ENABLED.eq(BaseConstant.COUPON_ENABLED_NORMAL));
        }
        if (param.getIsHasStock()){
            couponAllVos.and(MRKING_VOUCHER.LIMIT_SURPLUS_FLAG.eq(BaseConstant.COUPON_LIMIT_SURPLUS_FLAG_UNLIMITED).or(MRKING_VOUCHER.SURPLUS.gt(0)));
        }
        return couponAllVos.fetchInto(CouponAllVo.class);
    }

    /**
     * 获取用户可用优惠券列表
     * 王帅
     * @param userId
     * @return 当前用户可用优惠卷
     */
    public List<OrderCouponVo> getValidCoupons(Integer userId){
        AvailCouponParam param = new AvailCouponParam();
        param.setUserId(userId);
        param.setCurrentPage(1);
        param.setNav((byte)0);
        param.setPageRows(99);
        param.setIsShowEnabledShareSplit((byte)0);
        PageResult<AvailCouponVo> coupons = getCouponByUser(param);
        if(CollectionUtils.isEmpty(coupons.dataList)) {
            return null;
        }
        ArrayList<OrderCouponVo> result = new ArrayList<>(coupons.dataList.size());
        for (AvailCouponVo coupon: coupons.dataList) {
            result.add(new OrderCouponVo().init(coupon));
        }
        return result;
    }

    /**
     * 获取优惠券
     * 王帅
     * @param couponSn no
     * @return 优惠卷
     */
    public OrderCouponVo getValidCoupons(String couponSn){
        AvailCouponParam param = new AvailCouponParam();
        param.setCurrentPage(1);
        param.setNav((byte)0);
        param.setPageRows(1);
        param.setCouponSn(couponSn);
        PageResult<AvailCouponVo> coupons = getCouponByUser(param);
        if(CollectionUtils.isEmpty(coupons.dataList)) {
            return null;
        }
        return new OrderCouponVo().init(coupons.dataList.get(0));
    }

    /**
     * 判断该product是否可以使用该优惠卷
     * 王帅
     * @param coupon 优惠卷
     * @param bo bo
     * @return 是否可以使用
     */
    public boolean isContainsProduct(OrderCouponVo coupon, OrderGoodsBo bo){
        logger().info("判断该product是否可以使用该优惠卷strat,优惠卷：{}，商品：{}", coupon, bo);
        //全部为空为全部商品
        if(StringUtils.isBlank(coupon.getInfo().getRecommendGoodsId() + coupon.getInfo().getRecommendSortId())){
            return true;
        }
        if(StringUtil.isNotBlank(coupon.getInfo().getRecommendGoodsId()) && Arrays.asList(coupon.getInfo().getRecommendGoodsId().split(",")).contains(bo.getGoodsId().toString())){
            return true;
        }

        if(StringUtil.isNotBlank(coupon.getInfo().getRecommendSortId()) && Arrays.asList(coupon.getInfo().getRecommendSortId().split(",")).contains(bo.getSortId().toString())){
            return true;
        }
        //crm相关
        /*if(StringUtil.isNotBlank(coupon.getInfo().getRecommendProductId()) && Arrays.asList(coupon.getInfo().getRecommendProductId().split(",")).contains(bo.getProductId().toString())){
            return true;
        }*/
        return true;
    }

    /**
     * 王帅
     * @param coupon 优惠卷
     * @param totalPrice 商品总价
     * @return 折扣价格
     */
    public BigDecimal getDiscountAmount(OrderCouponVo coupon, BigDecimal totalPrice){
        logger().info("优惠券折扣金额计算（CouponService）start");
        if(OrderConstant.T_CAC_TYPE_REDUCTION == coupon.getInfo().getType()){
            //代金券
            return BigDecimalUtil.compareTo(coupon.getInfo().getLimitOrderAmount(), totalPrice) < 1 ? coupon.getInfo().getAmount() : BigDecimal.ZERO;
        }else if(OrderConstant.T_CAC_TYPE_DISCOUNT == coupon.getInfo().getType()){
            //打折券 return = 价格 * （10 - 折扣（eg:6.66） / 10）
            return BigDecimalUtil.compareTo(coupon.getInfo().getLimitOrderAmount(), totalPrice) < 1 ?
                BigDecimalUtil.multiplyOrDivideByMode(RoundingMode.DOWN,
                    BigDecimalUtil.BigDecimalPlus.create(totalPrice, BigDecimalUtil.Operator.multiply),
                    BigDecimalUtil.BigDecimalPlus.create(
                        BigDecimalUtil.addOrSubtrac(
                            BigDecimalUtil.BigDecimalPlus.create(BigDecimal.TEN, BigDecimalUtil.Operator.subtrac),
                            BigDecimalUtil.BigDecimalPlus.create(coupon.getInfo().getAmount(), null)),
                        BigDecimalUtil.Operator.divide),
                    BigDecimalUtil.BigDecimalPlus.create(BigDecimal.TEN, null))
            : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    /**
     * 王帅
     * 使用优惠券
     * @param id id
     * @param orderSn 订单号
     */
    public void use(Integer id, String orderSn){
        db().update(CUSTOMER_AVAIL_COUPONS)
            .set(CUSTOMER_AVAIL_COUPONS.IS_USED, COUPON_IS_USED_STATUS_USED)
            .set(CUSTOMER_AVAIL_COUPONS.USED_TIME, DateUtils.getSqlTimestamp())
            .set(CUSTOMER_AVAIL_COUPONS.ORDER_SN, orderSn)
            .where(CUSTOMER_AVAIL_COUPONS.ID.eq(id))
            .execute();
    }

    /**
     * 王帅
     * 释放使用优惠券
     * @param orderSn
     */
    public void releaserCoupon(String orderSn){
        logger().info("释放优惠券start");
        CustomerAvailCouponsRecord couponInfo= getCouponByOrderSn(orderSn);
        if(couponInfo == null) {
            logger().info("优惠券不存在，释放优惠券结束");
            return;
        }
        MrkingVoucherRecord couponInfoCfg = getCouponByOrderSn(couponInfo.getActId());
        if(couponInfoCfg == null) {
            logger().info("优惠券不存在，释放优惠券结束");
            return;
        }
        //释放使用数量
        couponInfoCfg.setUsedAmount(Integer.valueOf(couponInfoCfg.getUsedAmount() > 0 ? couponInfoCfg.getUsedAmount() - 1 : couponInfoCfg.getUsedAmount()).shortValue());
        couponInfoCfg.update();
        //恢复未使用
        couponInfo.setIsUsed(OrderConstant.NO);
        couponInfo.setUsedTime(null);
        couponInfo.setOrderSn(org.apache.commons.lang3.StringUtils.EMPTY);
        couponInfo.update();
    }

    /**
     * 王帅
     * @param orderSn
     * @return
     */
    private CustomerAvailCouponsRecord getCouponByOrderSn(String orderSn) {
        return db().selectFrom(CUSTOMER_AVAIL_COUPONS).where(CUSTOMER_AVAIL_COUPONS.ORDER_SN.eq(orderSn)).fetchAny();
    }

    /**
     * 王帅
     * @param id
     * @return
     */
    private MrkingVoucherRecord getCouponByOrderSn(Integer id) {
        return db().selectFrom(MRKING_VOUCHER).where(MRKING_VOUCHER.ID.eq(id)).fetchAny();
    }

    /**
     * Get small inventory coupon map.获取库存偏小的优惠券（排除已过期和已停用的）
     * （limit_surplus_flag：是否限制库存）不限制库存的排外
     * @param num the num
     * @return the map
     */
    public Map<Integer, String> getSmallInventoryCoupon(Integer num) {
        Condition condition = MRKING_VOUCHER.VALIDITY_TYPE.eq(BYTE_ZERO)
            .and(MRKING_VOUCHER.END_TIME.greaterThan(Timestamp.valueOf(LocalDateTime.now())));
        Condition condition1 = MRKING_VOUCHER.VALIDITY_TYPE.eq(BYTE_ONE);
        return db().select(MRKING_VOUCHER.ID, MrkingVoucher.MRKING_VOUCHER.ACT_NAME)
            .from(MRKING_VOUCHER)
            .where(MRKING_VOUCHER.SURPLUS.lessOrEqual(num))
            // 已启用
            .and(MRKING_VOUCHER.ENABLED.eq(BYTE_ONE))
            // 未过期
            .and(condition.or(condition1))
            // 不限制库存的排外
            .and(MRKING_VOUCHER.LIMIT_SURPLUS_FLAG.eq(BYTE_ZERO))
            .and(MRKING_VOUCHER.DEL_FLAG.eq(BYTE_ZERO))
            .and(MRKING_VOUCHER.CREATE_TIME.add(MRKING_VOUCHER.VALIDATION_CODE).greaterThan(Timestamp.valueOf(LocalDateTime.now())))
            .orderBy(MRKING_VOUCHER.SURPLUS, MRKING_VOUCHER.CREATE_TIME)
            .limit(5)
            .fetchMap(MRKING_VOUCHER.ID, MRKING_VOUCHER.ACT_NAME);
    }
    /**发放*/
    private static final Byte COUPON_GIVE = 0;
    /**领取*/
    private static final Byte COUPON_RECEIVE = 1;

    /**
     * 发放/领取优惠券后更新优惠券表发放/领取的数量
     * @author liangchen
     * @param accessMode 0:发放,1:领取
     * @param couponArray 优惠券数组
     */
    public void updateCouponGiveOrReceiveNum(Byte accessMode,String[] couponArray){
        //遍历优惠券
        for(String couponId : couponArray){
            //更新优惠券人数和数量
            selectCouponNum(accessMode,Integer.valueOf(couponId));
        }
    }

    /**
     * 查询数量变化
     * @author liangchen
     * @param accessMode 0:发放,1:领取
     * @param couponId 当前操作的优惠券id
     */
    private void selectCouponNum(Byte accessMode, Integer couponId){
        //查询人数
        short peopleNum = db().select(DSL.countDistinct(CUSTOMER_AVAIL_COUPONS.USER_ID))
                .from(CUSTOMER_AVAIL_COUPONS)
                .where(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(couponId))
                .and(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq(BYTE_ZERO))
                .and(CUSTOMER_AVAIL_COUPONS.ACCESS_MODE.eq(accessMode))
                .fetchOneInto(short.class);
        //查询数量
        short couponNum = db().select(DSL.count(CUSTOMER_AVAIL_COUPONS.USER_ID))
                .from(CUSTOMER_AVAIL_COUPONS)
                .where(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(couponId))
                .and(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq(BYTE_ZERO))
                .and(CUSTOMER_AVAIL_COUPONS.ACCESS_MODE.eq(accessMode))
                .fetchOneInto(short.class);
        updateCouponNum(accessMode,couponId,peopleNum,couponNum);
    }

    /**
     * 更改数量变化
     * @author liangchen
     * @param accessMode 0:发放,1:领取
     * @param couponId 当前操作的优惠券id
     * @param peopleNum 最新的人数
     * @param couponNum 最新的数量
     */
    private void updateCouponNum(Byte accessMode, Integer couponId, short peopleNum, short couponNum){
        //更新发放数量
        if (accessMode.equals(COUPON_GIVE)){
            db().update(MRKING_VOUCHER)
                .set(MRKING_VOUCHER.GIVEOUT_PERSON,peopleNum)
                .set(MRKING_VOUCHER.GIVEOUT_AMOUNT,couponNum)
                .where(MRKING_VOUCHER.ID.eq(couponId))
                .execute();
        }
        //更新领取数量
        if (accessMode.equals(COUPON_RECEIVE)){
            db().update(MRKING_VOUCHER)
                .set(MRKING_VOUCHER.RECEIVE_PERSON,peopleNum)
                .set(MRKING_VOUCHER.RECEIVE_AMOUNT,couponNum)
                .where(MRKING_VOUCHER.ID.eq(couponId))
                .execute();
        }
    }

    /**
     * 获取明天即将过期的优惠券
     * @return
     */
	public List<CouponWxVo> getExpiringCouponList() {
		LocalDateTime date = LocalDateTime.now();
		LocalDateTime localDateTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 00, 00,00);
		LocalDateTime localDateTime2 = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59,59);
		Timestamp time = Timestamp.valueOf(localDateTime.plus(1, ChronoUnit.DAYS));
		Timestamp time2 = Timestamp.valueOf(localDateTime2.plus(1, ChronoUnit.DAYS));
		Result<Record> fetch = db().select(CUSTOMER_AVAIL_COUPONS.asterisk(), MRKING_VOUCHER.ACT_NAME, USER.WX_OPENID,USER.WX_UNION_ID)
				.from(CUSTOMER_AVAIL_COUPONS, MRKING_VOUCHER, USER)
				.where(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(MRKING_VOUCHER.ID)
						.and(USER.USER_ID.eq(CUSTOMER_AVAIL_COUPONS.USER_ID))
						.and(CUSTOMER_AVAIL_COUPONS.START_TIME.lt(DateUtils.getLocalDateTime())
								.and(CUSTOMER_AVAIL_COUPONS.END_TIME.ge(time)).and(CUSTOMER_AVAIL_COUPONS.END_TIME
										.le(time2).and(CUSTOMER_AVAIL_COUPONS.IS_USED.eq((byte) 0)))))
				.fetch();
		List<CouponWxVo> into = new ArrayList<CouponWxVo>();
		if (fetch != null) {
			into = fetch.into(CouponWxVo.class);
		}
		return into;
	}

    /**
     * 删除用户优惠券
     * @param id
     * @return
     */
	public boolean availCouponDel(Integer id){
	    db().update(CUSTOMER_AVAIL_COUPONS)
            .set(CUSTOMER_AVAIL_COUPONS.DEL_FLAG,(byte)1)
            .where(CUSTOMER_AVAIL_COUPONS.ID.eq(id))
            .execute();
	    return true;
    }

	/**
	 * 小程序端会员激活使用
	 * @param couponId
	 * @return
	 * @return
	 */
	public CouponWxUserImportVo getOneMvById(Integer couponId, String lang) {
		MrkingVoucherRecord record = db().selectFrom(MRKING_VOUCHER).where(MRKING_VOUCHER.ID.eq(couponId)).fetchOne();
		if (record == null) {
			return null;
		}
		CouponWxUserImportVo into = record.into(CouponWxUserImportVo.class);
		Integer day = record.getValidity();
		Integer hour = record.getValidityHour();
		Integer minute = record.getValidityMinute();
		if (day > 0 || hour > 0 || minute > 0) {
			into.setStartTime(DateUtils.getSqlTimestamp());
			Timestamp endTime = Timestamp.valueOf(LocalDateTime.now().plus(day, ChronoUnit.DAYS)
					.plus(hour, ChronoUnit.HOURS).plus(minute, ChronoUnit.MINUTES));
			into.setEndTime(endTime);
		}
		String couponRule = null;
		if (into.getUseConsumeRestrict().equals(COUPON_TYPE_NORMAL)
				|| into.getLeastConsume().equals(new BigDecimal("0"))) {
			couponRule = "无门槛";
		} else {
			String actCode = into.getActCode();
			BigDecimal leastConsume = into.getLeastConsume();
			BigDecimal denomination = into.getDenomination();
			BigDecimal randomMax = into.getRandomMax();
            if (actCode.equals(CouponConstant.ACT_CODE_VOUCHER)) {
				couponRule=Util.translateMessage(lang, JsonResultMessage.CODE_EXCEL_VOUCHER, "excel", new Object[] {leastConsume,denomination});
			}
            if (actCode.equals(CouponConstant.ACT_CODE_RANDOM)) {
				couponRule=Util.translateMessage(lang, JsonResultMessage.CODE_EXCEL_RANDOM, "excel", new Object[] {leastConsume,randomMax});
			}else {
				couponRule=Util.translateMessage(lang, JsonResultMessage.CODE_EXCEL_OTHER, "excel", new Object[] {leastConsume,denomination});
			}
		}
		into.setCouponRule(couponRule);
		return into;
	}

    /**
     * 获得用户发放的礼包某种优惠券数量
     * @param packId
     * @param userId
     * @param voucherId
     * @return
     */
	public int getUserCouponCountByPackId(int packId,int userId,int voucherId,String orderSn){
	    SelectWhereStep select = (SelectWhereStep) db().selectCount().from(CUSTOMER_AVAIL_COUPONS).
            where(CUSTOMER_AVAIL_COUPONS.ACCESS_MODE.eq(CouponConstant.ACCESS_MODE_COUPON_PACK)).
            and(CUSTOMER_AVAIL_COUPONS.ACCESS_ID.eq(packId)).
            and(CUSTOMER_AVAIL_COUPONS.USER_ID.eq(userId)).
            and(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).
            and(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(voucherId));
	    if(StringUtil.isNotBlank(orderSn)){
	        select.where(CUSTOMER_AVAIL_COUPONS.ACCESS_ORDER_SN.eq(orderSn));
        }
        return (int) select.fetchOptionalInto(Integer.class).orElse(0);
    }

    /**
     * 优惠券礼包订单里某种优惠券最后一次发放时间
     * @param packId
     * @param userId
     * @param voucherId
     * @param orderSn
     * @return
     */
    public Timestamp getLastCouponPackVoucherSendTime(int packId,int userId,int voucherId,String orderSn){
	    return db().select(DSL.max(CUSTOMER_AVAIL_COUPONS.CREATE_TIME)).from(CUSTOMER_AVAIL_COUPONS).
            where(CUSTOMER_AVAIL_COUPONS.ACCESS_MODE.eq(CouponConstant.ACCESS_MODE_COUPON_PACK)).
            and(CUSTOMER_AVAIL_COUPONS.ACCESS_ID.eq(packId)).
            and(CUSTOMER_AVAIL_COUPONS.USER_ID.eq(userId)).
            and(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).
            and(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(voucherId)).
            and(CUSTOMER_AVAIL_COUPONS.ACCESS_ORDER_SN.eq(orderSn)).
            fetchAnyInto(Timestamp.class);
    }

    /**
     * 获取优惠信息
     * @param couponSns
     * @return
     */
    public List<CouponAndVoucherDetailVo> getCouponDetailByCouponSnList(List<String> couponSns) {
        Result<Record> fetch = db().select(CUSTOMER_AVAIL_COUPONS.asterisk(),
                MRKING_VOUCHER.ACT_NAME,MRKING_VOUCHER.DENOMINATION,MRKING_VOUCHER.ACT_CODE, MRKING_VOUCHER.LEAST_CONSUME,
                MRKING_VOUCHER.USE_EXPLAIN,MRKING_VOUCHER.RECOMMEND_GOODS_ID,MRKING_VOUCHER.RECOMMEND_CAT_ID,MRKING_VOUCHER.RECOMMEND_SORT_ID,
                MRKING_VOUCHER.USE_SCORE,MRKING_VOUCHER.SCORE_NUMBER,MRKING_VOUCHER.DEL_FLAG,
                MRKING_VOUCHER.VALIDITY,MRKING_VOUCHER.VALIDITY_HOUR,MRKING_VOUCHER.VALIDITY_MINUTE,
                MRKING_VOUCHER.RANDOM_MAX,MRKING_VOUCHER.RANDOM_MIN,MRKING_VOUCHER.TYPE.as("couponType"),
                MRKING_VOUCHER.RECEIVE_PER_NUM,MRKING_VOUCHER.RECEIVE_NUM)
                .from(CUSTOMER_AVAIL_COUPONS)
                .leftJoin(MRKING_VOUCHER).on(MRKING_VOUCHER.ID.eq(CUSTOMER_AVAIL_COUPONS.ACT_ID))
                .where(CUSTOMER_AVAIL_COUPONS.COUPON_SN.in(couponSns))
                .fetch();
        List<CouponAndVoucherDetailVo> into = new ArrayList<>();
        if (fetch != null) {
            into = fetch.into(CouponAndVoucherDetailVo.class);
        }
        return into;
    }
    /**
     * 获取优惠信息一条信息
     * @param couponSn conponSn
     * @return 一条数据
     */
    public CouponAndVoucherDetailVo getOneCouponDetail(String couponSn) {
        Record record = db().select(CUSTOMER_AVAIL_COUPONS.asterisk(),
                MRKING_VOUCHER.ACT_NAME, MRKING_VOUCHER.DENOMINATION, MRKING_VOUCHER.ACT_CODE, MRKING_VOUCHER.LEAST_CONSUME,
                MRKING_VOUCHER.USE_EXPLAIN, MRKING_VOUCHER.RECOMMEND_GOODS_ID, MRKING_VOUCHER.RECOMMEND_CAT_ID, MRKING_VOUCHER.RECOMMEND_SORT_ID,
                MRKING_VOUCHER.USE_SCORE, MRKING_VOUCHER.SCORE_NUMBER, MRKING_VOUCHER.DEL_FLAG,
                MRKING_VOUCHER.VALIDITY, MRKING_VOUCHER.VALIDITY_HOUR, MRKING_VOUCHER.VALIDITY_MINUTE,
                MRKING_VOUCHER.RANDOM_MAX, MRKING_VOUCHER.RANDOM_MIN, MRKING_VOUCHER.TYPE.as("couponType"),
                MRKING_VOUCHER.RECEIVE_PER_NUM, MRKING_VOUCHER.RECEIVE_NUM)
                .from(CUSTOMER_AVAIL_COUPONS)
                .leftJoin(MRKING_VOUCHER).on(MRKING_VOUCHER.ID.eq(CUSTOMER_AVAIL_COUPONS.ACT_ID))
                .where(CUSTOMER_AVAIL_COUPONS.COUPON_SN.eq(couponSn))
                .fetchAny();
        if (record != null) {
           return record.into(CouponAndVoucherDetailVo.class);
        }
        return null;
    }

    /**
     * 获取小程序码
     * @param couponId
     * @return
     */
    public ShareQrCodeVo getMpQrCode(Integer couponId){
        MrkingVoucherRecord mrkingVoucherRecord = getOneCouponById(couponId);
        ShareQrCodeVo vo = new ShareQrCodeVo();

        String imageUrl;
        String pathParam = String.format("couponId=%d", couponId);
        if (mrkingVoucherRecord.getUseScore().equals(BaseConstant.YES) && mrkingVoucherRecord.getScoreNumber() != null) {
            imageUrl = qrCode.getMpQrCode(QrCodeTypeEnum.SCORE_COUPON, pathParam);
            vo.setImageUrl(imageUrl);
            vo.setPagePath(QrCodeTypeEnum.SCORE_COUPON.getPathUrl(pathParam));
        } else {
            imageUrl = qrCode.getMpQrCode(QrCodeTypeEnum.DISCOUN_COUPON, pathParam);
            vo.setImageUrl(imageUrl);
            vo.setPagePath(QrCodeTypeEnum.DISCOUN_COUPON.getPathUrl(pathParam));
        }

        return vo;
    }

    /**
     * 分裂优惠券可用
     * @param couponSn 优惠券sn
     */
    public void updateSplitCouponEnabled(String couponSn){
        db().update(CUSTOMER_AVAIL_COUPONS)
                .set(CUSTOMER_AVAIL_COUPONS.DIVISION_ENABLED,(byte)0)
                .where(CUSTOMER_AVAIL_COUPONS.COUPON_SN.eq(couponSn))
                .execute();
    }

    /**
     * 获取领取数量
     * @param userId 用户id
     * @param couponId 优惠卷id
     * @return 0
     */
    public int getUserCouponNum(Integer userId, Integer couponId){
        return db().selectCount().from(CUSTOMER_AVAIL_COUPONS)
            .where(CUSTOMER_AVAIL_COUPONS.USER_ID.eq(userId))
            .and(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(couponId)).fetchAny().component1();
    }

    /**
     * 获取(领取/发放)优惠卷数量
     * @param userId 用户id
     * @param couponId 优惠卷id
     * @param accessMode 1领取 2发放 3礼包
     * @return
     */
    public int getUserCouponNum(Integer userId, Integer couponId,Byte accessMode){
        return db().selectCount().from(CUSTOMER_AVAIL_COUPONS)
            .where(CUSTOMER_AVAIL_COUPONS.USER_ID.eq(userId))
            .and(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(couponId))
            .and(CUSTOMER_AVAIL_COUPONS.ACCESS_MODE.eq(accessMode))
            .fetchAny().component1();
    }

}
