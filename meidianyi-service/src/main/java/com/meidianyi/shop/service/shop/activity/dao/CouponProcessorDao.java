package com.meidianyi.shop.service.shop.activity.dao;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.dao.foundation.database.DslPlus;
import com.meidianyi.shop.db.shop.tables.records.MrkingVoucherRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponConstant;
import org.jooq.Condition;
import org.jooq.Record;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.Tables.CUSTOMER_AVAIL_COUPONS;
import static com.meidianyi.shop.db.shop.Tables.MRKING_VOUCHER;

/**
 * @author 李晓冰
 * @date 2019年10月30日
 */
@Service
public class CouponProcessorDao extends ShopBaseService {

    /**
     * 获取关联的最紧密的优惠券信息，排序规则：先满折满减，再面值（金额的话取最大，打折取最小），再时间
     * @param goodsId 商品id
     * @param catId 平台分类
     * @param sortId 商家分类
     * @param date 时间
     * @return 最紧密优惠券信息
     */
    public Record getGoodsCouponClosestInfo(Integer goodsId, Integer catId, Integer sortId, Timestamp date) {
        Condition condition =buildCondition(goodsId,catId,sortId,date,false);
        List<Record> record5s = new ArrayList<>(db()
            .select(MRKING_VOUCHER.ID, MRKING_VOUCHER.ACT_CODE, MRKING_VOUCHER.DENOMINATION, MRKING_VOUCHER.USE_CONSUME_RESTRICT,
                MRKING_VOUCHER.LEAST_CONSUME,MRKING_VOUCHER.TYPE,MRKING_VOUCHER.RANDOM_MAX)
            .from(MRKING_VOUCHER).where(condition)
            .orderBy(MRKING_VOUCHER.ACT_CODE.asc(), MRKING_VOUCHER.DENOMINATION.asc(),MRKING_VOUCHER.RANDOM_MAX.asc(), MRKING_VOUCHER.CREATE_TIME.desc()).fetch());
        if (record5s.size() <= 0) {
            return null;
        }

        // 先打折，后金额，打折的取折扣最小的，金额的取金额最大的
        if (record5s.get(0).get(MRKING_VOUCHER.ACT_CODE).equals(CouponConstant.ACT_CODE_DISCOUNT)) {
            return record5s.get(0);
        } else {
            return record5s.get(record5s.size()-1);
        }
    }

    /**
     * 获取商品所有可用的优惠券
     * @param goodsId 商品id
     * @param catId 平台分类
     * @param sortId 商家分类
     * @param date 时间
     * @return 优惠券信息
     */
    public List<MrkingVoucherRecord> getGoodsCouponForDetail(Integer goodsId, Integer catId, Integer sortId, Timestamp date) {
        Condition condition =buildCondition(goodsId,catId,sortId,date,false);
        List<MrkingVoucherRecord> mrkingVoucherRecords = db().select()
            .from(MRKING_VOUCHER).where(condition)
            .orderBy(MRKING_VOUCHER.ACT_CODE.asc(), MRKING_VOUCHER.DENOMINATION.asc(),MRKING_VOUCHER.RANDOM_MAX.asc(), MRKING_VOUCHER.CREATE_TIME.desc())
            .fetchInto(MrkingVoucherRecord.class);
        Map<String, List<MrkingVoucherRecord>> collects = mrkingVoucherRecords.stream().collect(Collectors.groupingBy(MrkingVoucherRecord::getActCode, Collectors.toList()));
        List<MrkingVoucherRecord> discounts = collects.get(CouponConstant.ACT_CODE_DISCOUNT);
        List<MrkingVoucherRecord> vouchers = collects.get(CouponConstant.ACT_CODE_VOUCHER);
        List<MrkingVoucherRecord> random = collects.get(CouponConstant.ACT_CODE_RANDOM);

        List<MrkingVoucherRecord> datas = new ArrayList<>();
        if (discounts != null) {
            datas.addAll(discounts);
        }
        if (vouchers != null) {
            Collections.reverse(vouchers);
            datas.addAll(vouchers);
        }
        if (random!=null){
            Collections.reverse(random);
            datas.addAll(random);
        }
        return datas;
    }

    /**
     * 查询用户已拥有指定优惠券集合的对应数量
     * @param userId 用户id的
     * @param couponIds 优惠券id集合
     * @return key:优惠券id，value:优惠券数量
     */
    public Map<Integer, Integer> getUserCouponsAlreadyNum(Integer userId, List<Integer> couponIds) {
        Map<Integer, Long> couponAlreadyNum = db().select(CUSTOMER_AVAIL_COUPONS.ACT_ID).from(CUSTOMER_AVAIL_COUPONS).where(CUSTOMER_AVAIL_COUPONS.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
            .and(CUSTOMER_AVAIL_COUPONS.USER_ID.eq(userId)).and(CUSTOMER_AVAIL_COUPONS.ACT_ID.in(couponIds))
            .fetch().stream().collect(Collectors.groupingBy(x -> x.get(CUSTOMER_AVAIL_COUPONS.ACT_ID), Collectors.counting()));

        Map<Integer,Integer> retMp = new HashMap<>(couponIds.size());
        couponIds.forEach(actId->{
            Long num = couponAlreadyNum.getOrDefault(actId, 0L);
            retMp.put(actId,num.intValue());
        });

        return retMp;
    }
    /**
     * 创建过滤条件
     * @param goodsId 商品id
     * @param catId 平台分类
     * @param sortId 商家分类
     * @param date 时间限制
     * @param limitStartTime 是否要限制开始时间
     * @return sql where条件
     */
    public Condition buildCondition(Integer goodsId,Integer catId,Integer sortId,Timestamp date,Boolean limitStartTime){
        // 优惠券使用时间限制，小于指定结束时间或者优惠券是领取后指定天数内生效
        Condition timeCondition;
        if (limitStartTime) {
            timeCondition = (MRKING_VOUCHER.START_TIME.le(date).and(MRKING_VOUCHER.END_TIME.gt(date))).or(MRKING_VOUCHER.END_TIME.isNull());
        } else {
            timeCondition = MRKING_VOUCHER.END_TIME.gt(date).or(MRKING_VOUCHER.END_TIME.isNull());
        }
        // 优惠券剩余量限制，限量券的剩余数大于0或者是不限量券
        Condition surplusCondition = (MRKING_VOUCHER.TOTAL_AMOUNT.gt(0).and(MRKING_VOUCHER.SURPLUS.gt(0))).or(MRKING_VOUCHER.TOTAL_AMOUNT.eq(0));
        // 优惠券指定使用对象限制，全部商品或指定条件
        Condition usableTargetCondition = MRKING_VOUCHER.RECOMMEND_GOODS_ID.eq("").and(MRKING_VOUCHER.RECOMMEND_CAT_ID.eq("")).and(MRKING_VOUCHER.RECOMMEND_SORT_ID.eq(""));
        usableTargetCondition = usableTargetCondition.or(DslPlus.findInSet(goodsId,MRKING_VOUCHER.RECOMMEND_GOODS_ID)
            .or(DslPlus.findInSet(catId,MRKING_VOUCHER.RECOMMEND_CAT_ID)).or(DslPlus.findInSet(sortId,MRKING_VOUCHER.RECOMMEND_SORT_ID)));

        Condition baseCondition = MRKING_VOUCHER.DEL_FLAG.eq(DelFlag.NORMAL.getCode()).and(MRKING_VOUCHER.ENABLED.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(MRKING_VOUCHER.SUIT_GOODS.eq((byte) 0));

        return baseCondition.and(timeCondition).and(surplusCondition).and(usableTargetCondition);
    }
}
