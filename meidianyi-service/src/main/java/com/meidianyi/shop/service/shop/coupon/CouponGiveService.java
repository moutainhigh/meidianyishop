package com.meidianyi.shop.service.shop.coupon;

import com.mysql.cj.util.StringUtils;
import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.DelFlag;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.MrkingVoucher;
import com.meidianyi.shop.db.shop.tables.records.CustomerAvailCouponsRecord;
import com.meidianyi.shop.db.shop.tables.records.DivisionReceiveRecordRecord;
import com.meidianyi.shop.db.shop.tables.records.MrkingVoucherRecord;
import com.meidianyi.shop.service.foundation.exception.BusinessException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant.TaskJobEnum;
import com.meidianyi.shop.service.pojo.shop.coupon.CouponConstant;
import com.meidianyi.shop.service.pojo.shop.coupon.MpGetCouponParam;
import com.meidianyi.shop.service.pojo.shop.coupon.give.*;
import com.meidianyi.shop.service.pojo.shop.coupon.hold.CouponHoldListParam;
import com.meidianyi.shop.service.pojo.shop.coupon.hold.CouponHoldListVo;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.shop.member.tag.UserTagService;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.meidianyi.shop.db.shop.Tables.*;

/**
 * 优惠券管理
 *
 * @author liangchen
 * @date 2019年7月29日
 */
@Service
@Slf4j
public class CouponGiveService extends ShopBaseService {

    @Autowired
    private CouponHoldService couponHold;
    @Autowired
    public CouponService couponService;
    @Autowired
    public UserTagService userTag;
    @Autowired
    private CouponMsgNoticeService couponMsgNoticeService;
    private static final MrkingVoucher MV = MrkingVoucher.MRKING_VOUCHER.as("MV");

    /**
     * 获取方式，0：发放
     */
    private static final Byte ACCESS_MODE = 0;
    /**
     * 活动类型 定向发券 值为9
     */
    private static final Byte GET_SOURCE = 9;

    /* 省市区编码 */


    public static final String PROVINCE_CODE = "province_code";
    public static final String CITY_CODE = "city_code";
    public static final String DISTRICT_CODE = "district_code";
    /**
     * 优惠券发放情况分页列表
     *
     * @param param 选填项：活动名称
     * @return listVo 对应的发券活动信息
     */
    public PageResult<CouponGiveListVo> getCouponGiveList(CouponGiveListParam param) {
            // 查询活动信息
            SelectLimitStep<Record> couponGiveListVo =
                db().select().from(GIVE_VOUCHER).orderBy(GIVE_VOUCHER.ID.desc());
            // 模糊查询
            if (!StringUtils.isNullOrEmpty(param.getActName())) {
                couponGiveListVo =
                    ((SelectWhereStep<Record>) couponGiveListVo)
                        .where(GIVE_VOUCHER.ACT_NAME.like(this.likeValue(param.getActName())));
            }
            // 整合分页信息
            PageResult<CouponGiveListVo> listVo =
                this.getPageResult(
                    couponGiveListVo,
                    param.getCurrentPage(),
                    param.getPageRows(),
                    CouponGiveListVo.class);
            // 整合活动对应优惠券信息
            for (CouponGiveListVo vo : listVo.getDataList()) {
                CouponGiveGrantInfoParam condition = Util.json2Object(vo.getSendCondition(),CouponGiveGrantInfoParam.class,false);
                vo.setCondition(condition);
                // 解析得到活动中包含的优惠券
                String voucherId = condition.getCouponIds();
                String id = voucherId.replace("\"", "");
                String[] idArray = id.split(",");
                // 优惠券信息
                List<CouponGiveListConditionVo> tempListVo = new ArrayList<>();
                for (String selectId : idArray) {
                    Optional<CouponGiveListConditionVo> couponVo =
                        db().select(
                            MRKING_VOUCHER.ID.as("couponId"),
                            MRKING_VOUCHER.ACT_NAME.as("coupon_name"),
                            MRKING_VOUCHER.ACT_CODE,
                            MRKING_VOUCHER.USE_CONSUME_RESTRICT,
                            MRKING_VOUCHER.LEAST_CONSUME,
                            MRKING_VOUCHER.DENOMINATION,
                            MRKING_VOUCHER.START_TIME,
                            MRKING_VOUCHER.END_TIME,
                            MRKING_VOUCHER.VALIDITY_TYPE,
                            MRKING_VOUCHER.VALIDITY,
                            MRKING_VOUCHER.VALIDITY_HOUR,
                            MRKING_VOUCHER.VALIDITY_MINUTE)
                            .from(MRKING_VOUCHER)
                            .where(MRKING_VOUCHER.ID.eq(Integer.valueOf(selectId)))
                            .fetchOptionalInto(CouponGiveListConditionVo.class);
                    tempListVo.add(couponVo.orElse(null));
                }

                // 完善某一活动对应的优惠券信息
                vo.setCouponGiveListConditionVo(tempListVo);
                // 会员卡信息
                if (!StringUtils.isNullOrEmpty(vo.getCardId())) {
                    String[] cardId = vo.getCardId().split(",");
                    List<String> cardIdList = new ArrayList<>(Arrays.asList(cardId));
                    List<String> cardName =
                        db().select(MEMBER_CARD.CARD_NAME)
                            .from(MEMBER_CARD)
                            .where(MEMBER_CARD.ID.in(cardIdList))
                            .fetchInto(String.class);
                    // 将信息返回
                    vo.setCardName(cardName);
                }
                // 标签信息
                if (!StringUtils.isNullOrEmpty(vo.getTagId())) {
                    String[] tagId = vo.getTagId().split(",");
                    List<String> tagIdList = new ArrayList<>(Arrays.asList(tagId));
                    List<String> tagName =
                        db().select(TAG.TAG_NAME)
                            .from(TAG)
                            .where(TAG.TAG_ID.in(tagIdList))
                            .fetchInto(String.class);
                    // 将信息返回
                    vo.setTagName(tagName);
                }
            }
            return listVo;
    }

    /**
     * 优惠券明细
     *
     * @param param 发券活动信息和优惠券信息
     * @return 优惠券-用户 明细
     */
    public PageResult<CouponHoldListVo> getDetail(CouponGiveDetailParam param) {

        CouponHoldListParam couponParam = new CouponHoldListParam();
        couponParam.setActId(param.getCouponId());
        couponParam.setMobile(param.getMobile());
        couponParam.setUsername(param.getUsername());
        couponParam.setStatus(param.getIsUsed().byteValue());
        couponParam.setCurrentPage(param.getCurrentPage());
        couponParam.setPageRows(param.getPageRows());
        couponParam.setAccessId(param.getAccessId());
        couponParam.setGetSource(GET_SOURCE);
        PageResult<CouponHoldListVo> result = couponHold.getCouponHoldList(couponParam);
        result.getDataList().forEach(vo->{
            if (vo.getUsername()==null){
                vo.setUsername("未知用户");
            }
        });
        return result;
    }

    /**
     * 发放优惠券
     *
     * @param param 发券活动条件
     */
    public void insertGrant(CouponGiveGrantParam param) {
        // 完成一次发券活动
        String condition = Util.toJson(param.getCouponGiveGrantInfoParams());
        // 插入数据库
        db().insertInto(
            GIVE_VOUCHER,
            GIVE_VOUCHER.ACT_NAME,
            GIVE_VOUCHER.SEND_CONDITION,
            GIVE_VOUCHER.CARD_ID,
            GIVE_VOUCHER.TAG_ID,
            GIVE_VOUCHER.USER,
            GIVE_VOUCHER.HAVE_PAY,
            GIVE_VOUCHER.NO_PAY,
            GIVE_VOUCHER.MAX_COUNT,
            GIVE_VOUCHER.MIN_COUNT,
            GIVE_VOUCHER.MAX_AVE_PRICE,
            GIVE_VOUCHER.MIN_AVE_PRICE,
            GIVE_VOUCHER.SEND_ACTION,
            GIVE_VOUCHER.START_TIME,
            GIVE_VOUCHER.ACT_ID)
            .values(
                param.getActName(),
                condition,
                param.getCardId(),
                param.getTagId(),
                param.getUser(),
                param.getHavePay(),
                param.getNoPay(),
                param.getMaxCount(),
                param.getMinCount(),
                param.getMaxAvePrice(),
                param.getMinAvePrice(),
                param.getSendAction(),
                param.getStartTime(),
                NumberUtils.INTEGER_ZERO)
            .execute();
        // 得到当前发券活动id
        BigInteger bigIntegerActId = db().lastID();
        Integer actId = Integer.valueOf(bigIntegerActId.toString());
        //得到去重后的用户id
        Set<Integer> userIds = getGrantUser(param);
        // 队列
        List<Integer> userIdList = new ArrayList<>(userIds);
        String couponIds = param.getCouponGiveGrantInfoParams().getCouponIds();
        String[] couponArray = couponIds.split(",");
        CouponGiveQueueParam newParam =
            new CouponGiveQueueParam(
                getShopId(), userIdList, actId, couponArray, ACCESS_MODE, GET_SOURCE);
        // 立即发送
        if (param.getSendAction() == 0) {
            saas.taskJobMainService.dispatchImmediately(
                newParam,
                CouponGiveQueueParam.class.getName(),
                getShopId(),
                TaskJobEnum.GIVE_COUPON.getExecutionType());
        }
        // 定时发送
        if (param.getSendAction() == 1) {
            saas.messageTemplateService.createCouponTaskJob(getShopId(), newParam, param.getStartTime());
        }
        // 一次发券活动完成后，将发放状态修改为已发放
        db().update(GIVE_VOUCHER)
            .set(GIVE_VOUCHER.SEND_STATUS, NumberUtils.BYTE_ONE)
            .where(GIVE_VOUCHER.ID.eq(actId))
            .execute();
    }

    /**
     * 获取预计发放人数
     * @param param 筛选条件
     * @return 用户数
     */
    public CouponUserNum getGrantUserNum(CouponGiveGrantParam param){
        Set<Integer> userIds = getGrantUser(param);
        CouponUserNum couponUserNum = new CouponUserNum();
        couponUserNum.setUserNum(userIds.size());
        return couponUserNum;
    }

    /**
     * 获取当前活动涉及到的所有用户
     * @param param 筛选信息
     * @return 用户集合
     */
    public Set<Integer> getGrantUser(CouponGiveGrantParam param){
        // 获取当前活动设计到的所有用户 并将发券活动写入用户-优惠券对应表
        Set<Integer> userIds = new HashSet<>();
        // 得到相关时间
        // time-今天
        Date today = new Date();
        // time-加购人群筛选时间
        Timestamp cartDay = Util.getEarlyTimeStamp(today, -30);
        // 加购人群
        if (param.getCouponGiveGrantInfoParams().getCartBox().equals(NumberUtils.INTEGER_ONE)) {
            addCart(userIds, cartDay);
        }
        // 购买指定商品人群
        if (param.getCouponGiveGrantInfoParams().getGoodsBox().equals(NumberUtils.INTEGER_ONE)) {
            buyGoods(userIds, param.getCouponGiveGrantInfoParams().getGoodsIds());
        }
        // 持有会员卡人群
        if (param.getCouponGiveGrantInfoParams().getCardBox().equals(NumberUtils.INTEGER_ONE)) {
            haveCards(userIds, param.getCardId());
        }
        // 属于标签人群
        if (param.getCouponGiveGrantInfoParams().getTagBox().equals(NumberUtils.INTEGER_ONE)) {
            haveTags(userIds, param.getTagId());
        }
        // 选择指定的会员
        if (param.getCouponGiveGrantInfoParams().getMemberBox().equals(NumberUtils.INTEGER_ONE)) {
            getMember(userIds, param.getUser());
        }
        // N天内有交易记录
        if (param.getCouponGiveGrantInfoParams().getCustomBox().equals(NumberUtils.INTEGER_ONE)
            && param.getHavePay() != null) {
            // time-N天有交易记录人群筛选时间
            Timestamp havePayDay = Util.getEarlyTimeStamp(today, -param.getHavePay());
            havePay(userIds, havePayDay);
        }
        // N天内无交易记录
        if (param.getCouponGiveGrantInfoParams().getCustomBox().equals(NumberUtils.INTEGER_ONE)
            && param.getNoPay() != null) {
            // time-N天无交易记录人群筛选时间
            Timestamp noPayDay = Util.getEarlyTimeStamp(today, -param.getNoPay());
            noPay(userIds, noPayDay);
        }
        // 累计购买次数大于N次 min
        if (param.getCouponGiveGrantInfoParams().getCustomBox().equals(NumberUtils.INTEGER_ONE)
            && param.getMinCount() != null) {
            minCount(userIds, param.getMinCount());
        }
        // 累计购买次数小于N次 max
        if (param.getCouponGiveGrantInfoParams().getCustomBox().equals(NumberUtils.INTEGER_ONE)
            && param.getMaxCount() != null) {
            maxCount(userIds, param.getMaxCount());
        }
        // 购买商品均价大于N元 min
        if (param.getCouponGiveGrantInfoParams().getCustomBox().equals(NumberUtils.INTEGER_ONE)
            && param.getMinAvePrice() != null) {
            minAvePrice(userIds, param.getMinAvePrice());
        }
        // 购买商品均价小于N元 max
        if (param.getCouponGiveGrantInfoParams().getCustomBox().equals(NumberUtils.INTEGER_ONE)
            && param.getMaxAvePrice() != null) {
            maxAvePrice(userIds, param.getMaxAvePrice());
        }
        // 指定时间内有登陆记录
        if (param.getCouponGiveGrantInfoParams().getCustomBox().equals(NumberUtils.INTEGER_ONE)
            && param.getCouponGiveGrantInfoParams().getPointStartTime() != null
            && param.getCouponGiveGrantInfoParams().getPointEndTme() != null) {
            loginRecord(
                userIds,
                param.getCouponGiveGrantInfoParams().getPointStartTime(),
                param.getCouponGiveGrantInfoParams().getPointEndTme());
        }
        //指定区域内的用户
        if (param.getCouponGiveGrantInfoParams().getCustomBox().equals(NumberUtils.INTEGER_ONE)
            &&param.getCouponGiveGrantInfoParams().getProvinceCode()!=null){
                Map<String,Integer> region = new HashMap<String,Integer>(4){{
                    put(PROVINCE_CODE,param.getCouponGiveGrantInfoParams().getProvinceCode());
                    put(CITY_CODE,param.getCouponGiveGrantInfoParams().getCityCode());
                    put(DISTRICT_CODE,param.getCouponGiveGrantInfoParams().getDistrictCode());
                }};
            getUserIdByRegion(userIds,region);
        }
        return userIds;
    }
    /**
     * 获取30天内加购用户
     *
     * @param userIds 用户id集合
     */
    private void addCart(Set<Integer> userIds, Timestamp cartDay) {
        List<Integer> cartUserIds =
            db().select(CART.USER_ID)
                .from(CART)
                .where(CART.CREATE_TIME.greaterOrEqual(cartDay))
                .fetchInto(Integer.class);
        // 把对应的用户id插入集合
        userIds.addAll(cartUserIds);
    }

    /**
     * 获取购买过指定商品的用户
     *
     * @param userIds  用户id集合
     * @param goodsIds 商品id
     */
    private void buyGoods(Set<Integer> userIds, String goodsIds) {
        // 商品id解析为数组
        String[] goodArray = goodsIds.split(",");
        // 遍历商品id查询
        for (String goodsId : goodArray) {
            List<Integer> goodsUserIds =
                db().select(ORDER_INFO.USER_ID)
                    .from(ORDER_INFO)
                    .leftJoin(ORDER_GOODS)
                    .on(ORDER_INFO.ORDER_SN.eq(ORDER_GOODS.ORDER_SN))
                    .where(ORDER_GOODS.GOODS_ID.eq(Integer.valueOf(goodsId)))
                    .fetchInto(Integer.class);
            // 把对应的用户id插入集合
            userIds.addAll(goodsUserIds);
        }
    }

    /**
     * 获取持有会员卡人群
     *
     * @param userIds 用户id集合
     * @param cardIds 指定会员卡id
     */
    private void haveCards(Set<Integer> userIds, String cardIds) {
        // 会员卡转为数组
        String[] cardArray = cardIds.split(",");
        // 遍历查询持有该会员卡的用户
        for (String cardId : cardArray) {
            List<Integer> cardUserIds =
                db().select(USER_CARD.USER_ID)
                    .from(USER_CARD)
                    .where(USER_CARD.FLAG.eq(NumberUtils.BYTE_ZERO))
                    .and(USER_CARD.CARD_ID.eq(Integer.valueOf(cardId)))
                    .fetchInto(Integer.class);
            userIds.addAll(cardUserIds);
        }
    }

    /**
     * 获取属于指定标签人群
     *
     * @param userIds 用户id集合
     * @param tagIds  指定标签id
     */
    private void haveTags(Set<Integer> userIds, String tagIds) {
        // 标签转为数组
        String[] tagArray = tagIds.split(",");
        // 遍历查询属于该标签的用户
        for (String tagId : tagArray) {
            List<Integer> tagUserIds =
                db().select(USER_TAG.USER_ID)
                    .from(USER_TAG)
                    .where(USER_TAG.TAG_ID.eq(Integer.valueOf(tagId)))
                    .fetchInto(Integer.class);
            userIds.addAll(tagUserIds);
        }
    }

    /**
     * 获取指定用户人群
     *
     * @param userIds   用户id集合
     * @param memberIds 指定会员id
     */
    private void getMember(Set<Integer> userIds, String memberIds) {
        // 用户id转为数组
        String[] memberArray = memberIds.split(",");
        // 遍历查询指定会员
        for (String memberId : memberArray) {
            userIds.add(Integer.valueOf(memberId));
        }
    }

    /**
     * 获取N天内有交易记录的用户
     *
     * @param userIds    用户id集合
     * @param havePayDay N天
     */
    private void havePay(Set<Integer> userIds, Timestamp havePayDay) {
        // 查询有交易记录的用户id
        List<Integer> havePayUserIds =
            db().select(ORDER_INFO.USER_ID)
                .from(ORDER_INFO)
                .where(ORDER_INFO.ORDER_STATUS.greaterThan(OrderConstant.ORDER_CLOSED))
                .and(ORDER_INFO.CREATE_TIME.greaterOrEqual(havePayDay))
                .fetchInto(Integer.class);
        userIds.addAll(havePayUserIds);
    }

    /**
     * 获取N天内无交易记录的用户
     *
     * @param userIds  用户id集合
     * @param noPayDay N天
     */
    private void noPay(Set<Integer> userIds, Timestamp noPayDay) {
        // 查询有交易记录的用户id
        List<Integer> havePayUserIds =
            db().select(ORDER_INFO.USER_ID)
                .from(ORDER_INFO)
                .where(ORDER_INFO.ORDER_STATUS.greaterThan(OrderConstant.ORDER_CLOSED))
                .and(ORDER_INFO.CREATE_TIME.greaterOrEqual(noPayDay))
                .fetchInto(Integer.class);
        // 查询所有用户id
        List<Integer> allUserIds =
            db().select(USER.USER_ID)
                .from(USER)
                .fetchInto(Integer.class);
        // 得到两个集合差集为N天内无交易记录的用户
        allUserIds.removeAll(havePayUserIds);
        userIds.addAll(allUserIds);
    }

    /**
     * 获取累计购买次数大于N次的用户
     *
     * @param userIds     用户id集合
     * @param minCountNum 累计购买次数大于N次
     */
    private void minCount(Set<Integer> userIds, Integer minCountNum) {
        // 最小购买次数大于N
        List<Integer> minCountUserIds =
            db().select(ORDER_INFO.USER_ID)
                .from(ORDER_INFO)
                .where(ORDER_INFO.ORDER_STATUS.greaterThan(OrderConstant.ORDER_CLOSED))
                .groupBy(ORDER_INFO.USER_ID)
                .having(DSL.count(ORDER_INFO.ORDER_ID).greaterThan(minCountNum))
                .fetchInto(Integer.class);
        userIds.addAll(minCountUserIds);
    }

    /**
     * 获取累计购买次数小于N次的用户
     *
     * @param userIds     用户id集合
     * @param maxCountNum 累计购买次数小于N次
     */
    private void maxCount(Set<Integer> userIds, Integer maxCountNum) {
        // 最小购买次数大于N
        List<Integer> minCountUserIds =
            db().select(ORDER_INFO.USER_ID)
                .from(ORDER_INFO)
                .where(ORDER_INFO.ORDER_STATUS.greaterThan(OrderConstant.ORDER_CLOSED))
                .groupBy(ORDER_INFO.USER_ID)
                .having(DSL.count(ORDER_INFO.ORDER_ID).greaterThan(maxCountNum))
                .fetchInto(Integer.class);
        // 查询所有用户id
        List<Integer> allUserIds =
            db().select(USER.USER_ID)
                .from(USER)
                .where(USER.DEL_FLAG.eq(NumberUtils.BYTE_ZERO))
                .fetchInto(Integer.class);
        // 得到两个集合差集为N天内无交易记录的用户
        allUserIds.removeAll(minCountUserIds);
        userIds.addAll(allUserIds);
    }

    /**
     * 获取购买金额大于N元的用户
     *
     * @param userIds     用户id集合
     * @param minAvePrice 最小购买金额
     */
    private void minAvePrice(Set<Integer> userIds, BigDecimal minAvePrice) {
        List<Integer> minAvePriceUserIds =
            db().select(ORDER_INFO.USER_ID)
                .from(ORDER_INFO)
                .where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_CLOSED))
                .groupBy(ORDER_INFO.USER_ID)
                .having(
                    (DSL.sum(ORDER_INFO.ORDER_AMOUNT).divide(DSL.sum(ORDER_INFO.GOODS_AMOUNT)))
                        .greaterThan(minAvePrice))
                .fetchInto(Integer.class);
        userIds.addAll(minAvePriceUserIds);
    }

    /**
     * 获取购买金额小于N元的用户
     *
     * @param userIds     用户id集合
     * @param maxAvePrice 最大购买金额
     */
    private void maxAvePrice(Set<Integer> userIds, BigDecimal maxAvePrice) {
        List<Integer> minAvePriceUserIds =
            db().select(ORDER_INFO.USER_ID)
                .from(ORDER_INFO)
                .where(ORDER_INFO.ORDER_STATUS.greaterOrEqual(OrderConstant.ORDER_CLOSED))
                .groupBy(ORDER_INFO.USER_ID)
                .having(
                    (DSL.sum(ORDER_INFO.ORDER_AMOUNT).divide(DSL.sum(ORDER_INFO.GOODS_AMOUNT)))
                        .greaterThan(maxAvePrice))
                .fetchInto(Integer.class);
        // 查询所有用户id
        List<Integer> allUserIds =
            db().select(USER.USER_ID)
                .from(USER)
                .where(USER.DEL_FLAG.eq(NumberUtils.BYTE_ZERO))
                .fetchInto(Integer.class);
        // 得到两个集合差集为N天内无交易记录的用户
        allUserIds.removeAll(minAvePriceUserIds);
        userIds.addAll(allUserIds);
    }

    /**
     * 获取指定时间内有登录记录的用户
     *
     * @param userIds   用户id集合
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    private void loginRecord(Set<Integer> userIds, Timestamp startTime, Timestamp endTime) {
        List<Integer> loginRecordUserIds =
            db().select(USER_LOGIN_RECORD.USER_ID)
                .from(USER_LOGIN_RECORD)
                .where(USER_LOGIN_RECORD.CREATE_TIME.between(startTime, endTime))
                .fetchInto(Integer.class);
        userIds.addAll(loginRecordUserIds);
    }

    /**
     * 获取指定区域内的用户
     * @param userIds   用户id集合
     * @param region    区域代码
     */
    private void getUserIdByRegion(Set<Integer> userIds,Map<String,Integer> region){
        SelectConditionStep<Record1<Integer>> regionUserIds =
            db().select(USER.USER_ID)
                .from(USER)
                .leftJoin(USER_DETAIL).on(USER.USER_ID.eq(USER_DETAIL.USER_ID))
                .where(USER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE));
        if (region.get(PROVINCE_CODE)!=null){
            regionUserIds.and(USER_DETAIL.PROVINCE_CODE.eq(region.get(PROVINCE_CODE)));
            if (region.get(CITY_CODE)!=null){
                regionUserIds.and(USER_DETAIL.CITY_CODE.eq(region.get(CITY_CODE)));
                if (region.get(DISTRICT_CODE)!=null){
                    regionUserIds.and(USER_DETAIL.DISTRICT_CODE.eq(region.get(DISTRICT_CODE)));
                }
            }
        }
        List<Integer> result = regionUserIds.fetchInto(Integer.class);
        userIds.addAll(result);
    }
    /**
     * 定向发券，后台异步执行
     *
     * @param param 用户和优惠券信息
     */
    public CouponGiveQueueBo handlerCouponGive(CouponGiveQueueParam param) {
        CouponGiveQueueBo couponGiveBo = new CouponGiveQueueBo();
        Integer successNum = 0;
        //发券记录
        List<CustomerAvailCouponsRecord> sendRecord = new ArrayList<>();
        // 插入user-coupon关联表
        for (String couponId : param.getCouponArray()) {
            logger().info("当前优惠券ID："+couponId+",准备发放");
            // 得到当前优惠券信息
            CouponDetailsVo couponDetails = getCouponDetails(couponId);
            // 查询结果为空直接返回
            if (couponDetails == null) {
                log.error("当前优惠券发放失败，优惠券 [id：{}] 不存在", couponId);
                continue;
            }
            // 判断优惠券类型 减价or打折
            byte type = 0;
            if (CouponConstant.ACT_CODE_DISCOUNT.equalsIgnoreCase(couponDetails.getActCode())) {
                type = 1;
            }
            // 得到开始时间和结束时间
            Map<String, Timestamp> timeMap = getCouponTime(couponDetails);
            // 判断当前券的库存
            if (!param.getAccessMode().equals((byte)2)&&couponDetails.getLimitSurplusFlag().equals(NumberUtils.BYTE_ZERO)
                && couponDetails.getSurplus().equals(NumberUtils.INTEGER_ZERO)) {
                logger().info("当前优惠券ID："+couponId+",发放失败，所选优惠券库存不足");
                continue;
            }
            for (Integer userId : param.getUserIds()) {
                try {
                    //校验更新库存
                    boolean updateFlag = checkSurplusAndUpdata(param, userId, couponId, couponDetails);
                    if (!updateFlag){
                        continue;
                    }
                    byte finalType = type;

                    this.transaction(()-> {
                        // 库存足够，发券
                        sendCoupon(param, couponGiveBo, sendRecord, couponId, couponDetails, timeMap, userId, finalType);

                    });
                }catch (BusinessException e){
                        break;
                }
                // 得到成功条数
                successNum++;
                couponGiveBo.getCouponSet().add(Integer.valueOf(couponId));
                logger().info("当前优惠券ID："+couponId+",发放成功");
                if(couponDetails.getCouponTag().equals(BaseConstant.COUPON_TAG)){
                    //给领券用户打标签
                    List<Integer> couponTagIds = Util.stringToList(couponDetails.getCouponTagId());
                    userTag.addActivityTag(userId,couponTagIds, UserTagService.SRC_COUPON,Integer.valueOf(couponId));
                }
            }
        }
        couponGiveBo.setSendCoupons(sendRecord);
        //更新优惠券表发放/领取数量
        couponService.updateCouponGiveOrReceiveNum(param.getAccessMode(), param.getCouponArray());
        couponGiveBo.setSuccessSize(successNum);
        logger().info("发券方法完成");
        //发送公众号消息
        sendCouponMessage(couponGiveBo.getSendCoupons());
        return couponGiveBo;
    }

    private void sendCoupon(CouponGiveQueueParam param, CouponGiveQueueBo couponGiveBo, List<CustomerAvailCouponsRecord> sendRecord, String couponId, CouponDetailsVo couponDetails, Map<String, Timestamp> timeMap, Integer userId, byte finalType) {
        CustomerAvailCouponsRecord customerAvailCouponsRecord = db().newRecord(CUSTOMER_AVAIL_COUPONS);
        customerAvailCouponsRecord.setType(finalType);
        customerAvailCouponsRecord.setActId(Integer.valueOf(couponId));
        customerAvailCouponsRecord.setUserId(userId);
        customerAvailCouponsRecord.setActDesc(couponDetails.getActName());
        customerAvailCouponsRecord.setAmount(couponDetails.getDenomination());
        customerAvailCouponsRecord.setCouponSn(getCouponSn());
        customerAvailCouponsRecord.setAccessId(param.getActId());
        customerAvailCouponsRecord.setStartTime(timeMap.get("startTime"));
        customerAvailCouponsRecord.setEndTime(timeMap.get("endTime"));
        customerAvailCouponsRecord.setAccessMode(param.getAccessMode());
        customerAvailCouponsRecord.setGetSource(param.getGetSource());
        customerAvailCouponsRecord.setAccessOrderSn(StringUtil.isNotBlank(param.getAccessOrderSn()) ? param.getAccessOrderSn() : "");
        customerAvailCouponsRecord.setLimitOrderAmount(couponDetails.getLeastConsume());
        //判断如果是分裂优惠券默认不能使用
        if(couponDetails.getType().equals((byte)1)&&param.getSplitType().equals((byte)0)){
            log.info("发放的分裂优惠券不可用");
            customerAvailCouponsRecord.setDivisionEnabled((byte)1);
        }else {
            customerAvailCouponsRecord.setDivisionEnabled((byte)0);
        }
        if (couponDetails.getType().equals((byte)1)&& "random".equals(couponDetails.getActCode())){
            log.info("面额随机优惠券");
            //Math.random()*(n-m)+m
            BigDecimal randomAmount = couponDetails.getRandomMax().subtract(couponDetails.getRandomMin()).multiply(BigDecimal.valueOf(Math.random())).add(couponDetails.getRandomMin());
            customerAvailCouponsRecord.setAmount(randomAmount);
            log.info("随机生成优惠券金额在{}~{}直接:{}",couponDetails.getRandomMin(),couponDetails.getRandomMax(),randomAmount);
        }
        //发券操作
        sendRecord.add(customerAvailCouponsRecord);
        customerAvailCouponsRecord.insert();
        couponGiveBo.getCouponSn().add(customerAvailCouponsRecord.getCouponSn());
        if (couponDetails.getType().equals((byte)1)&& param.getSplitType().equals((byte)0)){
            log.info("分裂优惠券增领取记录");
            saveSplitRecord(param, couponId, userId, customerAvailCouponsRecord);
        }
    }

    /**
     * 库存限制 领取限制 更新库存
     * @param param
     * @param userId
     * @param couponId
     * @param couponDetails
     * @return
     */
    private boolean checkSurplusAndUpdata(CouponGiveQueueParam param, Integer userId, String couponId, CouponDetailsVo couponDetails) {
        if (param.getLimitNumType().equals(BaseConstant.YES)){
            log.info("校验优惠卷库存");
            if (couponDetails.getReceivePerPerson()>0){
                int userCouponNum = couponService.getUserCouponNum(userId, Integer.valueOf(couponId));
                log.info("每人领取限制{},user{}coupon{}领取{}个",couponDetails.getReceivePerPerson(),userId,couponId,userCouponNum);
                if (couponDetails.getReceivePerPerson()<=userCouponNum){
                    return false;
                }
            }
            // 如果是限制库存类型
            if (couponDetails.getLimitSurplusFlag().equals(NumberUtils.BYTE_ZERO) && couponDetails.getSurplus()>0) {
                int affectedRows = updateSurplusSubtractOne(couponId);
                log.info("优惠卷库存减1结果{}",affectedRows);
                if (affectedRows <= 0) {
                    log.info("优惠卷减库存失败");
                    return false;
                }
                //减库存成功后，同步库存
                couponDetails.setSurplus(couponDetails.getSurplus()-1);
            }
        }else {
            log.info("不校验优惠卷的库存，accessMode{}",param.getAccessMode());
        }
        return true;
    }

    private int updateSurplusSubtractOne(String couponId) {
        return db().update(MRKING_VOUCHER)
            .set(MRKING_VOUCHER.SURPLUS, MRKING_VOUCHER.SURPLUS.subtract(1))
            .where(MRKING_VOUCHER.ID.eq(Integer.parseInt(couponId)))
            .and(MRKING_VOUCHER.SURPLUS.gt(NumberUtils.INTEGER_ZERO))
            .execute();
    }

    /**
     *  保存分裂优惠卷信息
     * @param param 参数
     * @param couponId 发送的优惠卷id
     * @param userId  用户id
     * @param customerAvailCouponsRecord
     */
    private void saveSplitRecord(CouponGiveQueueParam param, String couponId, Integer userId, CustomerAvailCouponsRecord customerAvailCouponsRecord) {
        DivisionReceiveRecordRecord record = db().newRecord(DIVISION_RECEIVE_RECORD);
        record.setUser(userId);
        record.setUserId(userId);
        record.setCouponId(Integer.valueOf(couponId));
        record.setAmount(customerAvailCouponsRecord.getAmount());
        record.setCouponSn(customerAvailCouponsRecord.getCouponSn());
        record.setSource(param.getGetSource());
        record.setReceiveCouponSn(customerAvailCouponsRecord.getCouponSn());
        record.insert();
    }

    private CouponDetailsVo getCouponDetails(String couponId) {
        return db().select(
                MRKING_VOUCHER.LIMIT_SURPLUS_FLAG,
                MRKING_VOUCHER.SURPLUS,
                MRKING_VOUCHER.ACT_CODE,
                MRKING_VOUCHER.ACT_NAME,
                MRKING_VOUCHER.DENOMINATION,
                MRKING_VOUCHER.START_TIME,
                MRKING_VOUCHER.END_TIME,
                MRKING_VOUCHER.VALIDITY_TYPE,
                MRKING_VOUCHER.VALIDITY,
                MRKING_VOUCHER.VALIDITY_HOUR,
                MRKING_VOUCHER.LEAST_CONSUME,
                MRKING_VOUCHER.TYPE,
                MRKING_VOUCHER.RANDOM_MAX,
                MRKING_VOUCHER.RANDOM_MIN,
                MRKING_VOUCHER.RECEIVE_PER_PERSON,
                MRKING_VOUCHER.VALIDITY_MINUTE,
                MRKING_VOUCHER.COUPON_TAG_ID,
                MRKING_VOUCHER.COUPON_TAG)
                .from(MRKING_VOUCHER)
                .where(MRKING_VOUCHER.ID.eq(Integer.valueOf(couponId)))
                .and(MRKING_VOUCHER.DEL_FLAG.eq(NumberUtils.BYTE_ZERO))
                .fetchOneInto(CouponDetailsVo.class);
    }

    /**
     * 得到优惠券有效期
     *
     * @param couponDetails 优惠券信息
     * @return 优惠券有效期
     */
    private Map<String, Timestamp> getCouponTime(CouponDetailsVo couponDetails) {

        // 定义开始时间和结束时间作为最后的参数
        Timestamp startTime;
        Timestamp endTime;
        // 判断发送类型，得到发送时间
        if (couponDetails.getValidityType().equals(NumberUtils.BYTE_ZERO)) {
            startTime = couponDetails.getStartTime();
            endTime = couponDetails.getEndTime();
        } else {
            // 设置日期格式
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 得到系统当前时间作为开始时间
            String startTimeString = simpleDateFormat.format(System.currentTimeMillis());
            startTime = Timestamp.valueOf(startTimeString);
            // 计算结束时间
            long days = couponDetails.getValidity().longValue() * 1000 * 60 * 60 * 24;
            long hours = couponDetails.getValidityHour().longValue() * 1000 * 60 * 60;
            long minutes = couponDetails.getValidityMinute().longValue() * 1000 * 60;
            // 当前时间加上天、时、分得到结束时间
            long endTimeLong = System.currentTimeMillis() + days + hours + minutes;
            String endTimeString = simpleDateFormat.format(endTimeLong);
            endTime = Timestamp.valueOf(endTimeString);
        }
        return new HashMap<String, Timestamp>(4) {
            {
                put("startTime", startTime);
                put("endTime", endTime);
            }
        };
    }

    /**
     * 生成优惠券编号
     *
     * @return 优惠券编号
     */
    public String getCouponSn() {
        String couponSn;
        Calendar calendar = Calendar.getInstance();
        couponSn =
            "C"
                + Integer.toHexString(calendar.get(Calendar.MONTH) + 1).toUpperCase()
                + calendar.get(Calendar.DATE);
        Date date = new Date();
        couponSn =
            couponSn + String.valueOf(date.getTime()).substring(8, 13) + new Random().nextInt(99);
        // 判断是否与库存已存在编号冲突
        Integer couponInDb =
            db().select(CUSTOMER_AVAIL_COUPONS.ID)
                .from(CUSTOMER_AVAIL_COUPONS)
                .where(CUSTOMER_AVAIL_COUPONS.COUPON_SN.eq(couponSn))
                .fetchOptionalInto(Integer.class)
                .orElse(NumberUtils.INTEGER_ZERO);
        // 若冲突 递归获取唯一的优惠券编号
        if (!couponInDb.equals(NumberUtils.INTEGER_ZERO)) {
            couponSn = getCouponSn();
        }
        return couponSn;
    }

    /**
     * 优惠券弹窗
     *
     * @param param 选填：优惠券名称
     * @return popListVo
     */
    public List<CouponGivePopVo> popWindows(CouponGivePopParam param) {
        // 查询，并筛选出正确的使用限制条件
        SelectWhereStep<? extends Record> select =
            db().select(
                MRKING_VOUCHER.ID,
                MRKING_VOUCHER.ACT_CODE,
                MRKING_VOUCHER.ACT_NAME,
                MRKING_VOUCHER.DENOMINATION,
                MRKING_VOUCHER.LEAST_CONSUME,
                MRKING_VOUCHER.USE_CONSUME_RESTRICT,
                MRKING_VOUCHER.SURPLUS,
                MRKING_VOUCHER.TYPE,
                MRKING_VOUCHER.VALIDITY_TYPE,
                MRKING_VOUCHER.VALIDITY,
                MRKING_VOUCHER.VALIDITY_HOUR,
                MRKING_VOUCHER.VALIDITY_MINUTE,
                MRKING_VOUCHER.START_TIME,
                MRKING_VOUCHER.END_TIME,
                MRKING_VOUCHER.RECOMMEND_GOODS_ID,
                MRKING_VOUCHER.RECOMMEND_CAT_ID,
                MRKING_VOUCHER.RECOMMEND_SORT_ID,
                MRKING_VOUCHER.USE_SCORE,
                MRKING_VOUCHER.SCORE_NUMBER,
                MRKING_VOUCHER.RANDOM_MAX,
                MRKING_VOUCHER.RANDOM_MIN,
                MRKING_VOUCHER.LIMIT_SURPLUS_FLAG)
                .from(MRKING_VOUCHER);
        popWindowsBuildOptions(select, param);
        select.orderBy(MRKING_VOUCHER.CREATE_TIME.desc());
        List<CouponGivePopVo> list = select.fetchInto(CouponGivePopVo.class);
        for (CouponGivePopVo vo : list) {
        	vo.setStatus(couponService.couponMpService.couponGetStatus(new MpGetCouponParam(vo.getId(), null)));
		}
        return list;
    }

    /**
     * 优惠卷弹窗条件
     *
     * @param select
     * @param param
     */
    private void popWindowsBuildOptions(SelectWhereStep<? extends Record> select, CouponGivePopParam param) {
        select.where(MRKING_VOUCHER.DEL_FLAG.eq(DelFlag.NORMAL_VALUE))
                .and(MRKING_VOUCHER.ENABLED.eq(CouponConstant.ENABLED))
                .and(MRKING_VOUCHER.VALIDITY_TYPE.eq(CouponConstant.AFTER_RECEIVING)
                    .or(MRKING_VOUCHER.VALIDITY_TYPE.eq(CouponConstant.FIXED_TIME)
                        .and(MRKING_VOUCHER.END_TIME.greaterThan(Util.currentTimeStamp()))))
                .and(MRKING_VOUCHER.LIMIT_SURPLUS_FLAG.eq(CouponConstant.NOT_LIMIT_SURPLUS)
                    .or(MRKING_VOUCHER.LIMIT_SURPLUS_FLAG.eq(CouponConstant.LIMIT_SURPLUS)
                        .and(MRKING_VOUCHER.SURPLUS.greaterThan(CouponConstant.SURPLUS))));
        if (StringUtil.isNotEmpty(param.getActName())) {
            select.where(MRKING_VOUCHER.ACT_NAME.contains(param.getActName()));
        }
        if (param.getType() != null) {
            switch (param.getType()) {
                case CouponConstant.COUPON_TYPE_NORMAL:
                    select.where(MRKING_VOUCHER.TYPE.eq(CouponConstant.COUPON_TYPE_NORMAL));
                    break;
                case CouponConstant.COUPON_TYPE_SPILT:
                    select.where(MRKING_VOUCHER.TYPE.eq(CouponConstant.COUPON_TYPE_SPILT));
                    break;
                default:
            }
        }
        if (param.getStatus() != null) {
            switch (param.getStatus()) {
                case CouponConstant.COUPON_STATUS_PROCESSING:
                    select.where(MRKING_VOUCHER.END_TIME.greaterThan(Util.currentTimeStamp()))
                        .and(MRKING_VOUCHER.START_TIME.lessThan(Util.currentTimeStamp()));
                    break;
                case CouponConstant.COUPON_STATUS_NOT_START:
                    select.where(MRKING_VOUCHER.START_TIME.greaterThan(Util.currentTimeStamp()));
                    break;
                default:
            }
        }
    }

    /**
     * 废除优惠券
     *
     * @param param 优惠券id
     */
    public void deleteCoupon(CouponGiveDeleteParam param) {
        // 假删除实现废除某个用户的某张优惠券
        db().update(CUSTOMER_AVAIL_COUPONS)
            .set(CUSTOMER_AVAIL_COUPONS.DEL_FLAG, (byte) 1)
            .where(CUSTOMER_AVAIL_COUPONS.ID.eq(param.getId()))
            .execute();
    }

    /**
     * 参与表单反馈领取优惠券
     *
     * @param couponId 优惠券活动id
     * @param userId   用户id
     * @return 优惠券编号
     */
    public String collectingCoupons(Integer couponId, Integer userId) {
        Optional<MrkingVoucherRecord> result =
            db().selectFrom(MV).where(MV.ID.eq(couponId)).fetchOptional();
        MrkingVoucherRecord record =
            result.orElseThrow(() -> new RuntimeException("Coupon_id Info doesn't exist"));
        CustomerAvailCouponsRecord couponsRecord = new CustomerAvailCouponsRecord();
        couponsRecord.setUserId(userId);
        couponsRecord.setAccessId(couponId);
        couponsRecord.setAccessMode((byte) 1);
        couponsRecord.setActDesc(record.getActName());
        couponsRecord.setActId(couponId);
        couponsRecord.setAmount(record.getDenomination());
        couponsRecord.setStartTime(record.getStartTime());
        couponsRecord.setEndTime(record.getEndTime());
        // 1表示表单送券
        couponsRecord.setGetSource((byte) 1);
        // 优惠券类型，打折还是优惠;0为减价，1为打折
        if (CouponConstant.ACT_CODE_DISCOUNT.equals(record.getActCode())) {
            couponsRecord.setActType(1);
            couponsRecord.setType((byte) 1);
        } else if (CouponConstant.ACT_CODE_VOUCHER.equals(record.getActCode())) {
            couponsRecord.setActType(0);
            couponsRecord.setType((byte) 0);
        }
        // 0：未使用
        couponsRecord.setIsUsed((byte) 0);
        // 0：未删除
        couponsRecord.setDelFlag((byte) 0);
        String couponSn = getCouponSn();
        couponsRecord.setCouponSn(couponSn);
        db().executeInsert(couponsRecord);
        return couponSn;
    }

    public void sendVoucher(Integer userId, List<Integer> couponIds,Byte source) {
    	logger().info("发送优惠券");
    	String[] couponArray = new String[couponIds.size()];
    	for(int i = 0;i<couponIds.size();i++) {
    		couponArray[i] = String.valueOf(couponIds.get(i));
    	}
    	CouponGiveQueueParam param = new CouponGiveQueueParam(
    				Arrays.<Integer>asList(userId),
    				NumberUtils.INTEGER_ZERO,
    				couponArray,
    				NumberUtils.BYTE_ONE,
    				source
    			);
    	handlerCouponGive(param);
    }

    /**
     * 根据Id获取优惠券信息
     *
     * @param id
     * @return
     */
    public MrkingVoucherRecord getInfoById(Integer id) {
        return db().selectFrom(MRKING_VOUCHER).where(MRKING_VOUCHER.ID.eq(id)).fetchAny();
    }

    /**
     * 根据Id列表获取优惠券列表信息
     */
    public List<CouponGivePopVo> getInfoByIds(List<Integer> ids) {
    	List<CouponGivePopVo> list = db().selectFrom(MRKING_VOUCHER).where(MRKING_VOUCHER.ID.in(ids)).fetchInto(CouponGivePopVo.class);
        for (CouponGivePopVo vo : list) {
        	vo.setStatus(couponService.couponMpService.couponGetStatus(new MpGetCouponParam(vo.getId(), null)));
		}
        return list;
    }

    /**
     * 卡券领取成功发送公众号消息
     * @param couponsRecords 领取记录
     */
    public void sendCouponMessage(List<CustomerAvailCouponsRecord> couponsRecords){
        logger().info("发券事务完成，开始处理发送卡券领取成功公众号消息");
        logger().info("user-coupon记录是否存在：{}",couponsRecords!=null);
        if (couponsRecords!=null&&couponsRecords.size()>0) {
            logger().info("user-coupon记录为：{}", couponsRecords);
            couponsRecords.forEach(c -> {
                MrkingVoucherRecord couponDetail = getInfoById(c.getActId());
                String couponDesc = "";
                switch (couponDetail.getActCode()) {
                    case CouponConstant.ACT_CODE_VOUCHER:
                        couponDesc = couponDetail.getDenomination() + "元";
                        break;
                    case CouponConstant.ACT_CODE_DISCOUNT:
                        couponDesc = couponDetail.getDenomination() + "折";
                        break;
                    case CouponConstant.ACT_CODE_RANDOM:
                        couponDesc = couponDetail.getRandomMin() + "元-" + couponDetail.getRandomMax() + "元";
                        break;
                    default:
                }
                String duration = c.getStartTime().toString().substring(0,19)+"至"+c.getEndTime().toString().substring(0,19);
                String procession = DateUtils.getLocalDateFormat();
                couponMsgNoticeService.sendCouponMsgNotice(c.getUserId(), couponDetail.getActName(), procession, duration, couponDesc);
            });
        }
    }
}
