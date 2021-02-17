package com.meidianyi.shop.service.shop.coupon;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.DivisionReceiveRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.MrkingVoucherRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.coupon.*;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueBo;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.decoration.module.ModuleCoupon;
import com.meidianyi.shop.service.pojo.shop.member.account.ScoreParam;
import com.meidianyi.shop.service.pojo.shop.member.score.ScoreStatusConstant;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.wxapp.coupon.AvailCouponDetailVo;
import com.meidianyi.shop.service.pojo.wxapp.coupon.CouponDelParam;
import com.meidianyi.shop.service.pojo.wxapp.coupon.CouponPageDecorationVo;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.member.tag.UserTagService;
import org.jooq.Record;
import org.jooq.Record5;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.meidianyi.shop.db.shop.Tables.*;

/**
 * @author: 王兵兵
 * @create: 2019-11-19 19:03
 **/
@Service
public class CouponMpService extends ShopBaseService {
    @Autowired
    public MemberService member;

    @Autowired
    public CouponService coupon;

    @Autowired
    public UserTagService userTag;

    /**
     * 获取装修模块优惠券列表
     * @param moduleCoupon
     * @param userId
     * @return
     */
    public List<CouponPageDecorationVo> getPageIndexCouponList(ModuleCoupon moduleCoupon, int userId){
        List<CouponPageDecorationVo> couponList = new ArrayList<>();
        for(ModuleCoupon.Coupon coupon : moduleCoupon.getCouponAyy()){
            CouponPageDecorationVo couponVo = getCouponPageDecorationVo(coupon.getCouponId());

            //赋值该优惠券的可用状态，优先显示已领取
            if(couponVo == null){
                couponVo = new CouponPageDecorationVo();
                //已领取或领取达到极限
                couponVo.setStatus((byte)6);
            } else{
                if(couponVo.getReceivePerPerson() > 0){
                    //用户已 领取/发放 优惠券数
                    int getCouponAmount = getUserCouponAmount(coupon.getCouponId(),userId);
                    if(getCouponAmount >= couponVo.getReceivePerPerson()){
                        //已领取或领取达到极限
                        couponVo.setStatus((byte)5);
                    }
                }
                if(couponVo.getEnabled().equals(BaseConstant.COUPON_ENABLED_DISABLED)){
                    //停用
                    couponVo.setStatus((byte)4);
                }
                if(couponVo.getValidityType().equals(BaseConstant.COUPON_VALIDITY_TYPE_FIXED)){
                    if(DateUtils.getLocalDateTime().after(couponVo.getEndTime())){
                        //已过期
                        couponVo.setStatus((byte)2);
                    }
                }
                if(couponVo.getSurplus() == 0 && couponVo.getLimitSurplusFlag().equals(BaseConstant.COUPON_LIMIT_SURPLUS_FLAG_LIMITED)){
                    //库存不足
                    couponVo.setStatus((byte)3);
                }
            }

            couponList.add(couponVo);
        }
        return couponList;
    }

    /**
     * 用户已 领取/发放 优惠券数
     * @param couponId
     * @param userId
     * @return
     */
    public int getUserCouponAmount(int couponId,int userId){
        return db().selectCount().from(CUSTOMER_AVAIL_COUPONS).where(CUSTOMER_AVAIL_COUPONS.USER_ID.eq(userId).and(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(couponId))).fetchSingle().into(Integer.class);
    }

    private CouponPageDecorationVo getCouponPageDecorationVo(int couponId){
        Optional<CouponPageDecorationVo> vo =  db().select().from(MRKING_VOUCHER).where(MRKING_VOUCHER.ID.eq(couponId)).fetchOptionalInto(CouponPageDecorationVo.class);
        return vo.orElse(null);
    }

    /**
     * 查询优惠券基本信息
     * @param param
     * @return
     */
    public CouponListVo getCouponData(MpGetCouponParam param){
        CouponListVo couponData = db().select().from(MRKING_VOUCHER).where(MRKING_VOUCHER.ID.eq(param.getCouponId()))
            .fetchOneInto(CouponListVo.class);
        return couponData;
    }

    /**
     * 用户已领取某优惠券数量
     * @param userId
     * @param couponId
     */
    public Integer couponAlreadyGet(Integer userId,Integer couponId){
        int res = db().selectCount().from(CUSTOMER_AVAIL_COUPONS).where(CUSTOMER_AVAIL_COUPONS.ACT_ID.eq(couponId)).and(CUSTOMER_AVAIL_COUPONS.USER_ID.eq(userId))
            .fetchOneInto(Integer.class);
        return res;
    }

    /**
     * 领取优惠券到用户
     * @param param 0：领取成功；1：优惠券不存在；2：优惠券过期；3：优惠券体用；4：库存为0；5：可用积分不足；6：积分更新失败；7；领取次数达上限
     * @return
     */
    public Byte fetchCoupon(MpGetCouponParam param){
        CouponListVo couponData = this.getCouponData(param);
        Integer userId = param.getUserId();
        Byte couponGetStatus = this.couponGetStatus(param);
        Byte fetchCouponStatus = couponGetStatus;
        if(couponGetStatus != 0){
            return fetchCouponStatus;
        }
        //积分兑换判断
        if (couponData.getUseScore() == 1 && couponData.getScoreNumber() > 0) {
            int availCoupon = member.score.getTotalAvailableScoreById(userId);
            //查看用户可用积分
            if (couponData.getScoreNumber() > availCoupon) {
                //可用积分不足
                fetchCouponStatus = 5;
            } else {
                ScoreParam scoreParam = new ScoreParam();
                scoreParam.setScore(-(couponData.getScoreNumber()));
                scoreParam.setScoreStatus(ScoreStatusConstant.USED_SCORE_STATUS);
                scoreParam.setDesc("score");
                scoreParam.setRemarkCode(RemarkTemplate.RECEIVE_COUPON.code);
                scoreParam.setUserId(userId);
                //scoreParam.setRemark("领取优惠券");
                Integer subAccountId = 0;

                /** -交易明细类型 */
                Byte tradeType = 4;
                /** -资金流向 */
                Byte tradeFlow = 1;
                try {
                	member.score.updateMemberScore(scoreParam,subAccountId, tradeType,tradeFlow);
                } catch (MpException e) {
                    logger().info("积分更新失败");
                    fetchCouponStatus =6;
                }
            }
        }
        CouponGiveQueueParam couponParam = new CouponGiveQueueParam();
        List<Integer> userIds = new ArrayList();
        String[] couponArray = {couponData.getId().toString()};
        userIds.add(userId);
        couponParam.setUserIds(userIds);
        couponParam.setActId(0);
        couponParam.setCouponArray(couponArray);
        couponParam.setAccessMode((byte) 1);
        couponParam.setGetSource((byte) 5);
        //判断优惠券领取限制
        if(couponData.getReceivePerPerson().intValue() != 0){
            //有限制领取
            Integer alreadyGet = this.couponAlreadyGet(userId, couponData.getId());
            if(couponData.getReceivePerPerson() > alreadyGet){
                //添加优惠券到用户，调用定向发券通用方法
                coupon.couponGiveService.handlerCouponGive(couponParam);
            }else{
                //领取次数达上限
                fetchCouponStatus = 7;
            }
        }else{
            coupon.couponGiveService.handlerCouponGive(couponParam);
        }
        return fetchCouponStatus;
    }

    /**
     * 优惠券是否可领取状态
     * @param param 0：可正常领取；1：优惠券不存在；2：优惠券过期；3：优惠券停用；4：库存为0
     * @return
     */
    public Byte couponGetStatus(MpGetCouponParam param){
        Timestamp nowDate = DateUtils.getLocalDateTime();
        //判断领取限制
        CouponListVo couponData = this.getCouponData(param);
        Byte couponGetStatus;
        //通过alias_code查看优惠券是否存在
        if (null==couponData) {
           couponGetStatus = 1;
        }else if(couponData.getValidityType() == 0 && couponData.getEndTime().before(nowDate)){
            //是否过期
            couponGetStatus = 2;
        } else if (couponData.getEnabled() == 0) {
            //是否停用
            couponGetStatus = 3;
        } else if (couponData.getLimitSurplusFlag() == 0 && couponData.getSurplus() <= 0) {
            //库存判断
            couponGetStatus = 4;
        }else{
            //正常领取
            couponGetStatus = 0;
        }
        return couponGetStatus;
    }

    /**
     * 删除优惠券
     * @param param
     * @return
     */
    public Integer delCoupon(CouponDelParam param){
        int res = db().update(CUSTOMER_AVAIL_COUPONS).set(CUSTOMER_AVAIL_COUPONS.DEL_FLAG, (byte) 1)
            .where(CUSTOMER_AVAIL_COUPONS.ID.eq(param.getCouponId())).execute();
        return res;

    }

    public MpGetSplitCouponVo getSplitCoupon(MpGetSplitCouponParam param) {
        logger().info("领取分裂优惠券");
        MpGetSplitCouponVo vo = new MpGetSplitCouponVo();
        MrkingVoucherRecord couponRecord = coupon.couponGiveService.getInfoById(param.getCouponId());
        Result<Record5<Integer, String, String, Timestamp, BigDecimal>> receiveInfo = getReceiveInfo(param.getCouponSn(), param.getShareUserId());
        long count = receiveInfo.stream().filter(info -> info.get(DIVISION_RECEIVE_RECORD.USER_ID).equals(param.getUserId())).count();
        boolean isCouponOver = (couponRecord.getReceivePerNum() == 1 && couponRecord.getReceiveNum() <= receiveInfo.size())
            || (couponRecord.getLimitSurplusFlag() == 0 && couponRecord.getSurplus() <= 0);
        if (isCouponOver){
            logger().info("已经领完");
            vo.setStatus((byte)2);
            return vo;
        }
        if (count == 0) {
            logger().info("开始发卷");
            CouponAndVoucherDetailVo oneCouponDetail = coupon.getOneCouponDetail(param.getCouponSn());
            if (oneCouponDetail != null) {
                CouponGiveQueueParam couponGive = new CouponGiveQueueParam();
                couponGive.setUserIds(Collections.singletonList(param.getUserId()));
                couponGive.setCouponArray(new String[]{param.getCouponId() + ""});
                couponGive.setActId(oneCouponDetail.getActId());
                couponGive.setAccessMode((byte) 1);
                couponGive.setSplitType((byte) 1);
                couponGive.setGetSource(CouponConstant.COUPON_GIVE_SOURCE_SPLIT_COUPON);
                CouponGiveQueueBo sendData = coupon.couponGiveService.handlerCouponGive(couponGive);
                if (sendData.getSuccessSize() > 0) {
                    logger().info("发卷成功");
                    if (receiveInfo.size() == 1) {
                        logger().info("第一次分享,分享者优惠券可用");
                        coupon.updateSplitCouponEnabled(param.getCouponSn());
                    }
                    saveDivisionReceiveRecord(param, sendData);
                    vo.setStatus((byte)1);
                }else {
                    vo.setStatus((byte)4);
                }
            }
        }else {
            logger().info("已领取过优惠券");
            vo.setStatus((byte)3);
        }
        return vo;
    }

    /**
     * 分裂优惠券分享记录
     * @param param
     * @param sendData
     */
    private void saveDivisionReceiveRecord(MpGetSplitCouponParam param, CouponGiveQueueBo sendData) {
        DivisionReceiveRecordRecord record = db().newRecord(DIVISION_RECEIVE_RECORD);
        record.setUser(param.getShareUserId());
        record.setUserId(param.getUserId());
        record.setCouponId(param.getCouponId());
        record.setCouponSn(sendData.getCouponSn().get(0));
        record.setAmount(sendData.getSendCoupons().get(0).getAmount());
        record.setSource(param.getSource());
        record.setType((byte)1);
        record.setReceiveCouponSn(param.getCouponSn());
        record.insert();
    }


    /**
     * 分裂优惠券详情
     * @param param
     * @return
     */
    public MpGetCouponVo getSplitCouponDetail(MpGetSplitCouponParam param) {
        logger().info("分裂优惠券详情");
        MpGetCouponVo vo =new MpGetCouponVo();
        Timestamp time = DateUtils.getLocalDateTime();
        MrkingVoucherRecord couponRecord = coupon.couponGiveService.getInfoById(param.getCouponId());
        //分享优惠券用户信息
        Result<Record5<Integer, String, String, Timestamp, BigDecimal>> receiveInfo = getReceiveInfo(param.getCouponSn(), param.getShareUserId());
        List<MpGetCouponVo.UserInfo> userInfos =new ArrayList<>();
        receiveInfo.forEach(userRecord->{
            MpGetCouponVo.UserInfo userInfo = userRecord.into(MpGetCouponVo.UserInfo.class);
            Integer[] timeDifference = DateUtils.getTimeDifference(time, userRecord.get(DIVISION_RECEIVE_RECORD.CREATE_TIME));
            userInfo.setTime(timeDifference);
            userInfo.setUsername(userInfo.getUsername()==null?"神秘小伙伴":userInfo.getUsername());
            userInfos.add(userInfo);
        });
        if (param.getUserId().equals(param.getShareUserId())){
            vo.setHaveNum(userInfos.size());
            vo.setIsOneself((byte)1);
        }else {
            long count = receiveInfo.stream().filter(info -> info.get(DIVISION_RECEIVE_RECORD.USER_ID).equals(param.getUserId())).count();
            if (count>0){
                logger().info("领取过改优惠券");
                vo.setStatus((byte)2);
            }else {
                boolean isCouponOver = (couponRecord.getReceivePerNum() == 1 && couponRecord.getReceiveNum() <= userInfos.size())
                    || (couponRecord.getLimitSurplusFlag() == 0 && couponRecord.getSurplus() <= 0);
                if (isCouponOver){
                    logger().info("已经领完");
                    vo.setStatus((byte)3);
                }else {
                    if (couponRecord.getValidity()>0){
                        logger().info("优惠卷已过期");
                        vo.setStatus((byte)4);
                    }
                    if (couponRecord.getDelFlag().equals(DelFlag.DISABLE_VALUE)){
                        logger().info("优惠券已经删除");
                        vo.setStatus((byte)5);
                    }
                }
            }
        }
        if (CouponConstant.ACT_CODE_RANDOM.equals(couponRecord.getActCode()) || CouponConstant.ACT_CODE_VOUCHER.equals(couponRecord.getActCode())) {
            vo.setUnit("元");
        }else {
            vo.setUnit("折");
        }
        vo.setUserInfos(userInfos);
        AvailCouponDetailVo info = couponRecord.into(AvailCouponDetailVo.class);
        vo.setCouponInfo(info);
        return vo;
    }

    /**
     * 查询分裂优惠券分享信息
     * @param couponSn 优惠券sn
     * @param shareUserId 分享者id
     * @return
     */
    private Result<Record5<Integer, String, String, Timestamp, BigDecimal>> getReceiveInfo(String couponSn, Integer shareUserId){
        return  db().select(DIVISION_RECEIVE_RECORD.USER_ID,USER_DETAIL.USERNAME,USER_DETAIL.USER_AVATAR,DIVISION_RECEIVE_RECORD.CREATE_TIME ,DIVISION_RECEIVE_RECORD.AMOUNT)
                .from(DIVISION_RECEIVE_RECORD)
                .leftJoin(USER_DETAIL).on(USER_DETAIL.USER_ID.eq(DIVISION_RECEIVE_RECORD.USER_ID))
                .where(DIVISION_RECEIVE_RECORD.RECEIVE_COUPON_SN.eq(couponSn))
                .and(DIVISION_RECEIVE_RECORD.USER.eq(shareUserId))
                .orderBy(DIVISION_RECEIVE_RECORD.CREATE_TIME.desc())
                .fetch();
    }

    /**
     * 分享分裂优惠卷
     * @param param
     */
    public void shareSplitCoupon(MpCouponSnParam param) {
        db().update(DIVISION_RECEIVE_RECORD)
            .set(DIVISION_RECEIVE_RECORD.IS_SHARE,BaseConstant.YES)
            .where(DIVISION_RECEIVE_RECORD.COUPON_SN.eq(param.getCouponSn()))
            .and(DIVISION_RECEIVE_RECORD.USER.eq(param.getUserId())).execute();
    }
    /**
     *适用全部商品的正在进行中的优惠券(库存大于0)
     * @return
     */
    public List<CouponPageDecorationVo> allGoodsCoupon(){
        Timestamp nowDate = Util.currentTimeStamp();
        Result<Record> fetch = db().select().from(MRKING_VOUCHER)
            .where(MRKING_VOUCHER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)).and((MRKING_VOUCHER.END_TIME.ge(nowDate)).or(MRKING_VOUCHER.VALIDITY_TYPE.eq(BaseConstant.COUPON_VALIDITY_TYPE_FLEXIBLE)))
            .and(MRKING_VOUCHER.ENABLED.eq(BaseConstant.COUPON_ENABLED_NORMAL)).and(MRKING_VOUCHER.RECOMMEND_GOODS_ID.eq("")).and((MRKING_VOUCHER.SURPLUS.gt(0)).or(MRKING_VOUCHER.LIMIT_SURPLUS_FLAG.eq((byte)1))).fetch();
        if(fetch != null) {
            return fetch.into(CouponPageDecorationVo.class);
        } else{
            return null;
        }
    }
}
