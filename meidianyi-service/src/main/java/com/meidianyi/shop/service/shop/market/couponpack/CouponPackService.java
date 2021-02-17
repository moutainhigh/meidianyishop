package com.meidianyi.shop.service.shop.market.couponpack;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.config.DomainConfig;
import com.meidianyi.shop.db.main.tables.records.ShopRecord;
import com.meidianyi.shop.db.shop.tables.records.CouponPackRecord;
import com.meidianyi.shop.db.shop.tables.records.CouponPackVoucherRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.db.shop.tables.records.VirtualOrderRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponConstant;
import com.meidianyi.shop.service.pojo.shop.coupon.give.CouponGiveQueueParam;
import com.meidianyi.shop.service.pojo.shop.image.ShareQrCodeVo;
import com.meidianyi.shop.service.pojo.shop.market.couponpack.*;
import com.meidianyi.shop.service.pojo.shop.member.account.UserCardCouponPack;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketParam;
import com.meidianyi.shop.service.pojo.shop.overview.marketcalendar.MarketVo;
import com.meidianyi.shop.service.pojo.shop.qrcode.QrCodeTypeEnum;
import com.meidianyi.shop.service.pojo.wxapp.coupon.pack.*;
import com.meidianyi.shop.service.pojo.wxapp.member.card.GeneralUserCardVo;
import com.meidianyi.shop.service.pojo.wxapp.pay.base.WebPayVo;
import com.meidianyi.shop.service.shop.config.ShopCommonConfigService;
import com.meidianyi.shop.service.shop.config.TradeService;
import com.meidianyi.shop.service.shop.coupon.CouponService;
import com.meidianyi.shop.service.shop.goods.GoodsService;
import com.meidianyi.shop.service.shop.image.QrCodeService;
import com.meidianyi.shop.service.shop.member.MemberService;
import com.meidianyi.shop.service.shop.order.virtual.CouponPackOrderService;
import com.meidianyi.shop.service.shop.order.virtual.VirtualOrderService;
import jodd.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Record;
import org.jooq.Record4;
import org.jooq.SelectSeekStep1;
import org.jooq.SelectWhereStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.meidianyi.shop.db.shop.tables.CouponPack.COUPON_PACK;
import static com.meidianyi.shop.db.shop.tables.CouponPackVoucher.COUPON_PACK_VOUCHER;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.db.shop.tables.VirtualOrder.VIRTUAL_ORDER;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * @author: 王兵兵
 * @create: 2019-08-20 11:01
 **/
@Service
public class CouponPackService extends ShopBaseService {

    @Autowired
    private CouponPackVoucherService couponPackVoucherService;
    @Autowired
    private CouponPackOrderService couponPackOrderService;
    @Autowired
    private QrCodeService qrCode;
    @Autowired
    private CouponService couponService;
    @Autowired
    public MemberService memberService;
    @Autowired
    private DomainConfig domainConfig;
    @Autowired
    private ShopCommonConfigService shopCommonConfigService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private GoodsService goodsService;



    private static final String LANGUAGE_TYPE_EXCEL= "excel";

    /**
     * 新建优惠券礼包活动
     *
     */
    public void addCouponPack(CouponPackAddParam param) {
        this.transaction(()->{
            CouponPackRecord record = db().newRecord(COUPON_PACK);
            assign(param,record);
            record.insert();
            Integer couponPackId = record.getId();
            for(CouponPackVoucherAddParam goods : param.getCouponPackVoucher()){
                CouponPackVoucherRecord couponPackVoucherRecord = db().newRecord(COUPON_PACK_VOUCHER);
                assign(goods,couponPackVoucherRecord);
                couponPackVoucherRecord.setActId(couponPackId);
                couponPackVoucherRecord.insert();
            }
        });
    }

    /**
     * 优惠券礼包活动列表分页查询
     *
     */
    public PageResult<CouponPackPageListQueryVo> getPageList(CouponPackPageListQueryParam param) {
        SelectWhereStep<? extends Record> select = db().select(COUPON_PACK.ID,COUPON_PACK.ACT_NAME,COUPON_PACK.PACK_NAME,COUPON_PACK.START_TIME,COUPON_PACK.END_TIME,COUPON_PACK.TOTAL_AMOUNT,COUPON_PACK.ACCESS_MODE,COUPON_PACK.ACCESS_COST,COUPON_PACK.STATUS).
            from(COUPON_PACK);
        buildOptions(select,param);
        select.where(COUPON_PACK.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).orderBy(COUPON_PACK.CREATE_TIME.desc());
        PageResult<CouponPackPageListQueryVo> res = getPageResult(select,param.getCurrentPage(),param.getPageRows(),CouponPackPageListQueryVo.class);

        /**查询活动商品数量、订单付款数、付款用户数、付款总金额 */
        for(CouponPackPageListQueryVo vo : res.dataList){
            vo.setVoucherKindsNumber(couponPackVoucherService.getVoucherKindsNumber(vo.getId()));
            vo.setVoucherNumber(couponPackVoucherService.getVoucherNumber(vo.getId()));
            vo.setIssueAmount(couponPackOrderService.getCouponPackIssueAmount(vo.getId()));
            vo.setCurrentState(Util.getActStatus(vo.getStatus(),vo.getStartTime(),vo.getEndTime()));
        }

        return res;
    }





    /**
     * 查询条件
     */
    private void buildOptions(SelectWhereStep<? extends Record> select, CouponPackPageListQueryParam param) {
        if(param.getState() > 0) {
            /** 状态过滤*/
            Timestamp now = DateUtils.getLocalDateTime();
            switch(param.getState()) {
                case (byte)1:
                    select.where(COUPON_PACK.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(COUPON_PACK.START_TIME.lt(now)).and(COUPON_PACK.END_TIME.gt(now));
                    break;
                case (byte)2:
                    select.where(COUPON_PACK.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(COUPON_PACK.START_TIME.gt(now));
                    break;
                case (byte)3:
                    select.where(COUPON_PACK.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(COUPON_PACK.END_TIME.lt(now));
                    break;
                case (byte)4:
                    select.where(COUPON_PACK.STATUS.eq(BaseConstant.ACTIVITY_STATUS_DISABLE));
                    break;
                default:
            }
        }
        if (isNotEmpty(param.getActName())) {
            select.where(COUPON_PACK.ACT_NAME.contains(param.getActName()));
        }
        if (isNotEmpty(param.getPackName())) {
            select.where(COUPON_PACK.PACK_NAME.contains(param.getPackName()));
        }
        if(param.getAccessMode() != null && param.getAccessMode() >= CouponPackConstant.ACCESS_MODE_CASH) {
            /** 领取方式过滤*/
            select.where(COUPON_PACK.ACCESS_MODE.eq(param.getAccessMode()));
        }
    }

    /**
     * 删除优惠券礼包活动
     *
     */
    public void delCouponPack(Integer id) {
        db().update(COUPON_PACK).
            set(COUPON_PACK.DEL_FLAG,DelFlag.DISABLE.getCode()).
            set(COUPON_PACK.DEL_TIME, DateUtils.getLocalDateTime()).
            where(COUPON_PACK.ID.eq(id)).
            execute();
    }

    /**
     * 取单个优惠券礼包活动信息
     *
     */
    public CouponPackUpdateVo getCouponPackById(Integer id){
        CouponPackUpdateVo res =
            db().selectFrom(COUPON_PACK).
        where(COUPON_PACK.ID.eq(id)).and(COUPON_PACK.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).
        fetchOneInto(CouponPackUpdateVo.class);

        if(res != null){
            List<CouponPackUpdateVoucherVo> couponPackVoucherList =
            db().select(COUPON_PACK_VOUCHER.VOUCHER_ID,COUPON_PACK_VOUCHER.TOTAL_AMOUNT,COUPON_PACK_VOUCHER.IMMEDIATELY_GRANT_AMOUNT,COUPON_PACK_VOUCHER.TIMING_EVERY,COUPON_PACK_VOUCHER.TIMING_UNIT,COUPON_PACK_VOUCHER.TIMING_TIME,COUPON_PACK_VOUCHER.TIMING_AMOUNT).
            from(COUPON_PACK_VOUCHER).
            where(COUPON_PACK_VOUCHER.ACT_ID.eq(res.getId())).
            fetchInto(CouponPackUpdateVoucherVo.class);

            if(couponPackVoucherList != null && couponPackVoucherList.size() > 0){
                for(CouponPackUpdateVoucherVo couponPackVoucher : couponPackVoucherList){
                    couponPackVoucher.setCouponView(saas.getShopApp(getShopId()).coupon.getCouponViewById(couponPackVoucher.getVoucherId()));
                }
            }

            res.setCouponPackVoucher(couponPackVoucherList);
        }

        return res;
    }

    /**
     * 更新优惠券礼包活动
     *
     */
    public void updateCouponPack(CouponPackUpdateParam param) {
        CouponPackRecord record = new CouponPackRecord();
        assign(param,record);
        db().executeUpdate(record);
    }

    /**
     * 优惠券礼包活动订单列表
     *
     */
    public PageResult<CouponPackOrderListQueryVo> getCouponPackOrderPageList(CouponPackOrderListQueryParam param){
        SelectWhereStep<? extends Record> select = db().select(VIRTUAL_ORDER.ORDER_SN,VIRTUAL_ORDER.MONEY_PAID,VIRTUAL_ORDER.USE_ACCOUNT,VIRTUAL_ORDER.USE_SCORE,VIRTUAL_ORDER.MEMBER_CARD_BALANCE,VIRTUAL_ORDER.ORDER_STATUS,VIRTUAL_ORDER.CREATE_TIME,USER.USER_ID,USER.USERNAME,USER.MOBILE).
        from(VIRTUAL_ORDER).leftJoin(USER).on(VIRTUAL_ORDER.USER_ID.eq(USER.USER_ID));
        buildOrderOptions(select,param);
        select.where(VIRTUAL_ORDER.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).orderBy(VIRTUAL_ORDER.CREATE_TIME.desc());
        return getPageResult(select,param.getCurrentPage(),param.getPageRows(),CouponPackOrderListQueryVo.class);
    }

    /**
     * 优惠券礼包订单查询条件
     */
    private void buildOrderOptions(SelectWhereStep<? extends Record> select, CouponPackOrderListQueryParam param) {
        select.where(VIRTUAL_ORDER.VIRTUAL_GOODS_ID.eq(param.getId())).and(VIRTUAL_ORDER.GOODS_TYPE.eq(VirtualOrderService.GOODS_TYPE_COUPON_PACK)).and(VIRTUAL_ORDER.ORDER_STATUS.eq(CouponPackOrderService.ORDER_STATUS_FINISHED));
        if (isNotEmpty(param.getOrderSn())) {
            select.where(VIRTUAL_ORDER.ORDER_SN.contains(param.getOrderSn()));
        }
        if (isNotEmpty(param.getUserInfo())) {
            select.where(USER.USERNAME.contains(param.getUserInfo()).or(USER.MOBILE.contains(param.getUserInfo())));
        }
        if(param.getStartTime() != null){
            select.where(VIRTUAL_ORDER.CREATE_TIME.ge(param.getStartTime()));
        }
        if(param.getEndTime() != null){
            select.where(VIRTUAL_ORDER.CREATE_TIME.le(param.getEndTime()));
        }
    }

    /**
     * 优惠券礼包订单导出
     * @param param
     * @param lang
     * @return
     */
    public Workbook exportCouponPackOrderList(CouponPackOrderListQueryParam param, String lang) {
        SelectWhereStep<? extends Record> select = db().select(VIRTUAL_ORDER.ORDER_SN,VIRTUAL_ORDER.MONEY_PAID,VIRTUAL_ORDER.USE_ACCOUNT,VIRTUAL_ORDER.USE_SCORE,VIRTUAL_ORDER.MEMBER_CARD_BALANCE,VIRTUAL_ORDER.ORDER_STATUS,VIRTUAL_ORDER.CREATE_TIME,USER.USER_ID,USER.USERNAME,USER.MOBILE).
        from(VIRTUAL_ORDER).leftJoin(USER).on(VIRTUAL_ORDER.USER_ID.eq(USER.USER_ID));
        buildOrderOptions(select,param);
        select.where(VIRTUAL_ORDER.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).orderBy(VIRTUAL_ORDER.CREATE_TIME.desc());
        List<CouponPackOrderExportVo> couponPackOrderList =  select.fetchInto(CouponPackOrderExportVo.class);

        /**循环处理状态列*/
        for(CouponPackOrderExportVo vo : couponPackOrderList) {
            switch(vo.getOrderStatus()) {
                case 0:
                    vo.setStatusName(Util.translateMessage(lang, JsonResultMessage.ORDER_STATUS_WAIT_PAY,LANGUAGE_TYPE_EXCEL));
                    break;
                case 1:
                    vo.setStatusName(Util.translateMessage(lang, JsonResultMessage.ORDER_STATUS_FINISHED,LANGUAGE_TYPE_EXCEL));
                    break;
                default:
            }
        }

        Workbook workbook= ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang,workbook);
        excelWriter.writeModelList(couponPackOrderList,CouponPackOrderExportVo.class);
        return workbook;
    }

    /**
     * 优惠券礼包活动领取明细列表
     *
     */
    public PageResult<CouponPackDetailListQueryVo> getCouponPackDetailPageList(CouponPackDetailListQueryParam param){
        SelectWhereStep<? extends Record> select = db().select(USER.USERNAME,USER.MOBILE,VIRTUAL_ORDER.ACCESS_MODE,VIRTUAL_ORDER.ORDER_SN,VIRTUAL_ORDER.CREATE_TIME).
        from(VIRTUAL_ORDER).leftJoin(USER).on(USER.USER_ID.eq(VIRTUAL_ORDER.USER_ID));
        buildDetailOptions(select,param);
        select.where(VIRTUAL_ORDER.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).orderBy(VIRTUAL_ORDER.CREATE_TIME.desc());
        PageResult<CouponPackDetailListQueryVo> res = getPageResult(select,param.getCurrentPage(),param.getPageRows(),CouponPackDetailListQueryVo.class);
        for (CouponPackDetailListQueryVo vo : res.dataList){
            vo.setVoucherAccessCount(couponPackOrderService.getVoucherAccessCount(vo.getOrderSn()));
        }
        return res;
    }

    /**
     * 优惠券礼包明细查询条件
     */
    private void buildDetailOptions(SelectWhereStep<? extends Record> select, CouponPackDetailListQueryParam param) {
        select.where(VIRTUAL_ORDER.GOODS_TYPE.eq(VirtualOrderService.GOODS_TYPE_COUPON_PACK)).and(VIRTUAL_ORDER.VIRTUAL_GOODS_ID.eq(param.getId())).and(VIRTUAL_ORDER.ORDER_STATUS.eq(CouponPackOrderService.ORDER_STATUS_FINISHED)).and(VIRTUAL_ORDER.DEL_FLAG.eq(DelFlag.NORMAL.getCode())).and(VIRTUAL_ORDER.VIRTUAL_GOODS_ID.eq(param.getId()));

        if (isNotEmpty(param.getOrderSn())) {
            select.where(VIRTUAL_ORDER.ORDER_SN.contains(param.getOrderSn()));
        }
        if (isNotEmpty(param.getUsername())) {
            select.where(USER.USERNAME.contains(param.getUsername()));
        }
        if (isNotEmpty(param.getMobile())) {
            select.where(USER.MOBILE.contains(param.getMobile()));
        }
        if(param.getStartTime() != null){
            select.where(VIRTUAL_ORDER.CREATE_TIME.ge(param.getStartTime()));
        }
        if(param.getEndTime() != null){
            select.where(VIRTUAL_ORDER.CREATE_TIME.le(param.getEndTime()));
        }
        if(param.getAccessMode() != null && param.getAccessMode() >= CouponPackConstant.ACCESS_MODE_CASH){
            select.where(VIRTUAL_ORDER.ACCESS_MODE.eq(param.getAccessMode()));
        }
    }

    /**
     * 获取小程序码
     */
    public ShareQrCodeVo getMpQrCode(Integer id) {

        String pathParam = "packId=" + id;
        String imageUrl = qrCode.getMpQrCode(QrCodeTypeEnum.DISCOUNT_COUPON_PAGCKAGE, pathParam);

        ShareQrCodeVo vo = new ShareQrCodeVo();
        vo.setImageUrl(imageUrl);
        vo.setPagePath(QrCodeTypeEnum.DISCOUNT_COUPON_PAGCKAGE.getPathUrl(pathParam));
        return vo;
    }

    /**
     * 小程序活动页面数据拼装
     * @param packId
     * @param userId
     * @return
     */
    public CouponPackActInfoVo getCouponPackActInfo(Integer packId,Integer userId){
        CouponPackActInfoVo vo = new CouponPackActInfoVo();

        CouponPackActBaseVo packInfo = db().selectFrom(COUPON_PACK).where(COUPON_PACK.ID.eq(packId)).fetchOptionalInto(CouponPackActBaseVo.class).orElse(null);
        if(packInfo == null){
            return null;
        }
        vo.setPackInfo(packInfo);

        List<CouponPackVoucherVo> packList = couponPackVoucherService.getCouponPackVoucherList(packId);
        packList.forEach(p->{
            p.setGrantCount(couponService.getUserCouponCountByPackId(packId,userId,p.getVoucherId(),null));
        });
        vo.setPackList(packList);
        vo.setBuyCount(couponPackOrderService.getUserCouponPackBuyCount(packId,userId));

        return vo;
    }

    public CouponPackCheckVo couponPackToBuy(Integer packId, Integer userId,String clientIp){
        CouponPackCheckVo vo = new CouponPackCheckVo();
        CouponPackRecord couponPackRecord = db().fetchAny(COUPON_PACK,COUPON_PACK.ID.eq(packId));

        byte state = checkIsCanOrder(couponPackRecord,userId);
        vo.setState(state);
        if(vo.getState() > 0){
            return vo;
        }

        //直接领取
        if(couponPackRecord.getAccessMode().equals(CouponPackConstant.ACCESS_MODE_FREE)){
            CouponPackOrderParam orderParam = new CouponPackOrderParam();
            orderParam.setPackId(packId);
            orderParam.setOrderAmount(BigDecimal.ZERO);
            orderParam.setScoreDiscount(0);
            WebPayVo webPayVo = couponPackOrderService.createOrder(orderParam,userId,clientIp);
            //vo.setOrderSn(orderSn);
            vo.setState((byte)6);
        }

        return vo;
    }

    /**
     * 下单前的活动校验
     * @param couponPackRecord
     * @param userId
     * @return
     */
    private byte checkIsCanOrder(CouponPackRecord couponPackRecord,int userId){
        if(couponPackRecord.getStatus().equals(BaseConstant.ACTIVITY_STATUS_DISABLE) || couponPackRecord.getDelFlag().equals(DelFlag.DISABLE_VALUE) || couponPackRecord.getEndTime().before(DateUtils.getLocalDateTime())){
            return (byte)1;
        }
        if(couponPackRecord.getStartTime().after(DateUtils.getLocalDateTime())){
            return (byte)2;
        }
        if(couponPackRecord.getTotalAmount() > 0 && couponPackRecord.getTotalAmount() <= couponPackOrderService.getCouponPackIssueAmount(couponPackRecord.getId())){
            return (byte)3;
        }
        if(couponPackRecord.getLimitGetTimes() > 0 && couponPackRecord.getLimitGetTimes() <= couponPackOrderService.getUserCouponPackBuyCount(couponPackRecord.getId(),userId)){
            return (byte)4;
        }
        if(couponPackRecord.getAccessMode().equals(CouponPackConstant.ACCESS_MODE_SCORE) && (memberService.score.getUserScore(userId) < couponPackRecord.getAccessCost().intValue())){
            return (byte)5;
        }
        return (byte)0;
    }

    /**
     * 订单确认页数据组装
     * @param param
     * @param userId
     * @return
     */
    public CouponPackOrderBeforeVo couponPackOrderConfirm(CouponPackOrderBeforeParam param, Integer userId){
        CouponPackOrderBeforeVo vo = new CouponPackOrderBeforeVo();

        //店铺通用信息
        ShopRecord shop = saas.shop.getShopById(getShopId());
        if(StringUtil.isNotEmpty(shop.getLogo())){
            vo.setShopLogo(domainConfig.imageUrl(shop.getLogo()));
        }
        if(StringUtil.isNotEmpty(shop.getShopAvatar())){
            vo.setShopAvatar(domainConfig.imageUrl(shop.getShopAvatar()));
        }
        vo.setInvoiceSwitch(shopCommonConfigService.getInvoice());
        vo.setScoreProportion(memberService.score.scoreCfgService.getScoreProportion());
        vo.setIsShowServiceTerms(shopCommonConfigService.getServiceTerms());
        if (vo.getIsShowServiceTerms().equals(BaseConstant.YES)) {
            vo.setServiceChoose(tradeService.getServiceChoose());
            vo.setServiceName(tradeService.getServiceName());
            vo.setServiceDocument(tradeService.getServiceDocument());
        }
        vo.setCardFirst(tradeService.getCardFirst());
        vo.setBalanceFirst(tradeService.getBalanceFirst());
        vo.setScoreFirst(tradeService.getScoreFirst());

        //用户的余额和积分
        UserRecord user = memberService.getUserRecordById(userId);
        vo.setAccount(user.getAccount());
        vo.setScore(user.getScore());

        //活动信息
        CouponPackRecord couponPackRecord = db().fetchAny(COUPON_PACK,COUPON_PACK.ID.eq(param.getPackId()));
        vo.setPackInfo(couponPackRecord.into(CouponPackActBaseVo.class));
        List<CouponPackVoucherVo> packList = couponPackVoucherService.getCouponPackVoucherList(param.getPackId());
        vo.setOrderGoods(packList);
        if(couponPackRecord.getAccessMode().equals(CouponPackConstant.ACCESS_MODE_SCORE)){
            //vo.setOrderAmount(couponPackRecord.getAccessCost().divide(BigDecimal.valueOf(vo.getScoreProportion()),0,BigDecimal.ROUND_FLOOR));
            vo.setOrderAmount(BigDecimal.ZERO);
            vo.setMoneyPaid(BigDecimal.ZERO);
        }else {
            vo.setOrderAmount(couponPackRecord.getAccessCost());
            vo.setMoneyPaid(couponPackRecord.getAccessCost());
        }

        //会员卡
        List<GeneralUserCardVo> memberCardLit = memberService.userCardService.getCanUseGeneralCardList(userId);
        vo.setMemberCardList(memberCardLit);
        if(StringUtils.isNoneBlank(param.getCardNo()) && !memberCardLit.isEmpty()){
            //默认选第一个
            String defaultCardNo = "0";
            if(param.getCardNo().equals(defaultCardNo)){
                vo.setMemberCardNo(memberCardLit.get(0).getCardNo());
                vo.setMemberCardInfo(memberCardLit.get(0));
            }else{
                vo.setMemberCardInfo(memberCardLit.stream().filter(generalUserCardVo->generalUserCardVo.getCardNo().equals(param.getCardNo())).collect(Collectors.toList()).get(0));
                vo.setMemberCardNo(param.getCardNo());
            }
        }else{
            //主动不选会员卡
            if(!memberCardLit.isEmpty()){
                vo.setMemberCardInfo(null);
                vo.setMemberCardNo(null);
            }
        }

        return vo;
    }

    /**
     * 发放优惠券
     * @param orderList
     */
    public void sendCouponPack(List<VirtualOrderRecord> orderList){
        orderList.forEach(order->{
            List<CouponPackVoucherBo> couponList = couponPackVoucherService.getCouponPackVoucherList(order.getVirtualGoodsId(),order.getUserId(),order.getOrderSn());
            int finishCount = 0;
            for(CouponPackVoucherBo coupon : couponList){
                if(coupon.getLastSendTime() != null && DateUtils.timestampIsNowDay(coupon.getLastSendTime())){
                    // 说明今天已发送过，校验重复发送
                    continue;
                }
                if(coupon.getGrantCouponNumber() >= coupon.getTotalAmount()){
                    //这种券发放完成
                    finishCount++;
                    continue;
                }
                // 得到发送优惠券数量
                int sentNum = getSentNum(order, coupon);

                //发放优惠券
                List<Integer> userIds = new ArrayList<>();
                userIds.add(order.getUserId());
                String [] couponArray = {String.valueOf(coupon.getVoucherId())};
                for(int i=0;i<sentNum;i++){
                    CouponGiveQueueParam newParam = new CouponGiveQueueParam(
                        getShopId(),userIds , order.getVirtualGoodsId(),couponArray , BaseConstant.ACCESS_MODE_COUPON_PACK, BaseConstant.GET_SOURCE_COUPON_PACK);
                    newParam.setAccessOrderSn(order.getOrderSn());
                    newParam.setLimitNumType(BaseConstant.NO);
                    saas.taskJobMainService.dispatchImmediately(newParam, CouponGiveQueueParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.GIVE_COUPON.getExecutionType());
                }
            }
            if(finishCount == couponList.size()){
                //说明每种优惠券都发完了
                couponPackOrderService.updateStillSendFlag(order.getOrderSn(),CouponPackConstant.STILL_SEND_FLAG_FINISH);
            }
        });
    }

    /**
     * 得到发送优惠券数量
     * @param order
     * @param coupon
     * @return
     */
    private int getSentNum(VirtualOrderRecord order, CouponPackVoucherBo coupon) {
        //这一轮需要发出去的优惠券数量
        int sentNum=0;

        if(coupon.getLastSendTime() == null && coupon.getGrantCouponNumber() <= 0 && coupon.getImmediatelyGrantAmount() > 0){
            //立即发放
            sentNum = coupon.getImmediatelyGrantAmount();
        }else{
            Timestamp lastTime;
            if(coupon.getLastSendTime() == null){
                lastTime = order.getPayTime();
            } else{
                lastTime = coupon.getLastSendTime();
            }
            if(coupon.getImmediatelyGrantAmount() < coupon.getTotalAmount()){
                Timestamp nextTime = null;
                if(DateUtils.timestampIsSameDay(lastTime,order.getPayTime())){
                    //立即发放之后的第一次周期发放
                    Calendar cal = Calendar.getInstance();
                    switch (coupon.getTimingUnit()){
                        case CouponPackConstant.TIMING_UNIT_DAY:
                            nextTime = DateUtils.getTimeStampPlus(lastTime,coupon.getTimingEvery(), ChronoUnit.DAYS);
                            break;
                        case CouponPackConstant.TIMING_UNIT_WEEK:
                            cal.setTime(DateUtils.getTimeStampPlus(lastTime,coupon.getTimingEvery(), ChronoUnit.WEEKS));
                            cal.setFirstDayOfWeek(Calendar.MONDAY);
                            cal.add(Calendar.DATE, cal.getFirstDayOfWeek()- cal.get(Calendar.DAY_OF_WEEK) + coupon.getTimingTime() - 1);
                            nextTime = new Timestamp(cal.getTime().getTime());
                            break;
                        case CouponPackConstant.TIMING_UNIT_MONTH:
                            cal.setTime(DateUtils.getTimeStampPlus(lastTime,coupon.getTimingEvery(), ChronoUnit.MONTHS));
                            cal.set(Calendar.DATE,coupon.getTimingTime());
                            nextTime = new Timestamp(cal.getTime().getTime());
                            break;
                        default:
                    }
                }else{
                    //不是第一次周期发放
                    switch (coupon.getTimingUnit()){
                        case CouponPackConstant.TIMING_UNIT_DAY:
                            nextTime = DateUtils.getTimeStampPlus(lastTime,coupon.getTimingEvery(), ChronoUnit.DAYS);
                            break;
                        case CouponPackConstant.TIMING_UNIT_WEEK:
                            nextTime = DateUtils.getTimeStampPlus(lastTime,coupon.getTimingEvery(), ChronoUnit.WEEKS);
                            break;
                        case CouponPackConstant.TIMING_UNIT_MONTH:
                            nextTime = DateUtils.getTimeStampPlus(lastTime,coupon.getTimingEvery(), ChronoUnit.MONTHS);
                            break;
                        default:
                    }
                }
                if(DateUtils.timestampIsNowDay(nextTime)){
                    sentNum = (coupon.getGrantCouponNumber() + coupon.getTimingAmount()) > coupon.getTotalAmount() ? coupon.getTotalAmount() - coupon.getGrantCouponNumber() : coupon.getTimingAmount();
                }
            }
        }
        return sentNum;
    }

    /**
     * 购物车里可用的优惠券礼包
     *
     * @param userId
     * @param cartGoodsIdsList
     * @return
     */
    public CouponPackCartVo getCartCouponPack(Integer userId, List<Integer> cartGoodsIdsList) {
        CouponPackCartVo vo = new CouponPackCartVo();
        Set<Integer> cartGoodsIds = new HashSet<>(cartGoodsIdsList);

        List<CouponPackRecord> usablePackList = getCouponPackRecords();

        if (usablePackList != null && usablePackList.size() > 0) {
            for (CouponPackRecord couponPackRecord : usablePackList) {
                if (couponPackRecord.getShowCart().equals(BaseConstant.YES)) {
                    //该礼包里是否有可用的优惠券
                    boolean isApplicable = false;
                    //是否全是折扣券
                    boolean onlyDiscount = true;
                    Set<Integer> applicableGoodsIds = new HashSet<>();
                    List<CouponPackVoucherVo> voucherVos = couponPackVoucherService.getCouponPackVoucherList(couponPackRecord.getId());
                    //包里的优惠券张数
                    int totalAmount = 0;
                    //包里的优惠券总价值
                    BigDecimal totalValue = BigDecimal.ZERO;
                    for (CouponPackVoucherVo voucher : voucherVos) {
                        totalAmount += voucher.getTotalAmount();
                        if (voucher.getActCode().equals(CouponConstant.ACT_CODE_VOUCHER)) {
                            onlyDiscount = false;
                            totalValue = BigDecimalUtil.add(totalValue, BigDecimalUtil.multiply(voucher.getDenomination(), new BigDecimal(voucher.getTotalAmount())));
                        } else if (voucher.getActCode().equals(CouponConstant.ACT_CODE_RANDOM)) {
                            onlyDiscount = false;
                            totalValue = BigDecimalUtil.add(totalValue, BigDecimalUtil.multiply(voucher.getRandomMax(), new BigDecimal(voucher.getTotalAmount())));
                        }
                        if (voucher.getIsAllGoodsUse()) {
                            isApplicable = true;
                            continue;
                        }
                        if (StringUtil.isNotEmpty(voucher.getRecommendGoodsId())) {
                            applicableGoodsIds.addAll(Util.splitValueToList(voucher.getRecommendGoodsId()));
                        }
                        if (StringUtil.isNotEmpty(voucher.getRecommendCatId()) || StringUtil.isNotEmpty(voucher.getRecommendSortId())) {
                            List<Integer> goodsIds = goodsService.getOnShelfGoodsIdList(Util.splitValueToList(voucher.getRecommendCatId()), Util.splitValueToList(voucher.getRecommendSortId()), Collections.emptyList());
                            applicableGoodsIds.addAll(goodsIds);
                        }
                    }
                    if (!isApplicable) {
                        applicableGoodsIds.retainAll(cartGoodsIds);
                        if (CollectionUtils.isNotEmpty(applicableGoodsIds)) {
                            isApplicable = true;
                        }
                    }
                    if (isApplicable) {
                        CouponPackCartVo.PackInfo pack = new CouponPackCartVo.PackInfo();
                        pack.setActId(couponPackRecord.getId());
                        pack.setPackName(couponPackRecord.getPackName());
                        pack.setOnlyDiscount(onlyDiscount);
                        pack.setIsReceive(couponPackOrderService.getUserCouponPackBuyCount(couponPackRecord.getId(), userId) > 0 ? true : false);
                        pack.setTotalAmount(totalAmount);
                        pack.setTotalValue(totalValue);
                        vo.getPackList().add(pack);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(vo.getPackList())) {
                vo.setPackList(vo.getPackList().stream().sorted(Comparator.comparing(CouponPackCartVo.PackInfo::getIsReceive)).collect(Collectors.toList()));
                Optional<CouponPackCartVo.PackInfo> max;
                if (vo.getPackList().stream().filter(c -> !c.getIsReceive()).count() > 0) {
                    max = vo.getPackList().stream().filter(c -> !c.getIsReceive()).max((x, y) -> x.getTotalValue().compareTo(y.getTotalValue()));
                } else {
                    max = vo.getPackList().stream().max((x, y) -> x.getTotalValue().compareTo(y.getTotalValue()));
                }

                vo.setMaxReduce(max.isPresent() ? max.get().getTotalValue() : null);
                return vo;
            }
            return null;
        } else {
            return null;
        }
    }

    private List<CouponPackRecord> getCouponPackRecords() {
        return db().selectFrom(COUPON_PACK)
                .where(COUPON_PACK.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .and(COUPON_PACK.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL))
                .and(COUPON_PACK.ISSUED_AMOUNT.lt(COUPON_PACK.TOTAL_AMOUNT))
                .and(COUPON_PACK.START_TIME.le(DateUtils.getLocalDateTime()))
                .and(COUPON_PACK.END_TIME.ge(DateUtils.getLocalDateTime()))
                .and(COUPON_PACK.SHOW_CART.eq(BaseConstant.YES))
                .fetch();
    }

    /**
	 * 营销日历用id查询活动
	 *
	 * @param id
	 * @return
	 */
	public MarketVo getActInfo(Integer id) {
		return db().select(COUPON_PACK.ID, COUPON_PACK.ACT_NAME, COUPON_PACK.START_TIME, COUPON_PACK.END_TIME)
				.from(COUPON_PACK).where(COUPON_PACK.ID.eq(id)).fetchAnyInto(MarketVo.class);
	}

	/**
	 * 营销日历用查询目前正常的活动
	 *
	 * @param param
	 * @return
	 */
	public PageResult<MarketVo> getListNoEnd(MarketParam param) {
		SelectSeekStep1<Record4<Integer, String, Timestamp, Timestamp>, Integer> select = db()
				.select(COUPON_PACK.ID, COUPON_PACK.ACT_NAME, COUPON_PACK.START_TIME, COUPON_PACK.END_TIME)
				.from(COUPON_PACK)
				.where(COUPON_PACK.DEL_FLAG.eq(DelFlag.NORMAL_VALUE)
						.and(COUPON_PACK.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)
								.and(COUPON_PACK.END_TIME.gt(DateUtils.getSqlTimestamp()))))
				.orderBy(COUPON_PACK.ID.desc());
		PageResult<MarketVo> pageResult = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(),
				MarketVo.class);
		return pageResult;
	}

	/**
	 * 获取所有的可用优惠券礼包活动
	 */
	public List<UserCardCouponPack> getAllValidCouponPack(){
		/**  状态过滤	*/
		Timestamp now = DateUtils.getLocalDateTime();
		return db().select(COUPON_PACK.ID,COUPON_PACK.ACT_NAME,COUPON_PACK.PACK_NAME)
				.from(COUPON_PACK)
				.where(COUPON_PACK.DEL_FLAG.eq(DelFlag.NORMAL.getCode()))
				.and((COUPON_PACK.STATUS.eq(BaseConstant.ACTIVITY_STATUS_NORMAL)).and(COUPON_PACK.START_TIME.lt(now)).and(COUPON_PACK.END_TIME.gt(now)))
				.orderBy(COUPON_PACK.CREATE_TIME.desc())
				.fetchInto(UserCardCouponPack.class);
	}



}
