package com.meidianyi.shop.service.shop.order.virtual;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.data.JsonResultMessage;
import com.meidianyi.shop.common.foundation.excel.ExcelFactory;
import com.meidianyi.shop.common.foundation.excel.ExcelTypeEnum;
import com.meidianyi.shop.common.foundation.excel.ExcelWriter;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.records.VirtualOrderRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.pojo.shop.member.card.CardConstant;
import com.meidianyi.shop.service.pojo.shop.member.order.UserOrderBean;
import com.meidianyi.shop.service.pojo.shop.operation.RecordContentTemplate;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.virtual.MemberCardOrderExportVo;
import com.meidianyi.shop.service.pojo.shop.order.virtual.MemberCardOrderParam;
import com.meidianyi.shop.service.pojo.shop.order.virtual.MemberCardOrderVo;
import com.meidianyi.shop.service.pojo.shop.order.virtual.VirtualOrderRefundParam;
import jodd.util.StringUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.jooq.Record;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static com.meidianyi.shop.db.shop.tables.MemberCard.MEMBER_CARD;
import static com.meidianyi.shop.db.shop.tables.User.USER;
import static com.meidianyi.shop.db.shop.tables.UserCard.USER_CARD;
import static com.meidianyi.shop.db.shop.tables.VirtualOrder.VIRTUAL_ORDER;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * 虚拟商品订单 - 会员卡
 *
 * @author 郑保乐
 */
@Service
public class MemberCardOrderService extends VirtualOrderService {

    public static final String MEMBER_CARD_ORDER_SN_PREFIX = "M";

    /**
     * 订单列表
     */
    public PageResult<MemberCardOrderVo> getMemberCardOrderList(MemberCardOrderParam param) {
        SelectOnConditionStep<? extends Record> select =
            db().select(VIRTUAL_ORDER.ORDER_ID, VIRTUAL_ORDER.ORDER_SN,
                VIRTUAL_ORDER.VIRTUAL_GOODS_ID, VIRTUAL_ORDER.RETURN_FLAG, VIRTUAL_ORDER.PAY_TIME, VIRTUAL_ORDER.MONEY_PAID,
                VIRTUAL_ORDER.USE_ACCOUNT, VIRTUAL_ORDER.USE_SCORE, VIRTUAL_ORDER.RETURN_TIME, VIRTUAL_ORDER.CURRENCY, VIRTUAL_ORDER.ORDER_AMOUNT, USER.USER_ID, USER.USERNAME, USER.MOBILE,
                MEMBER_CARD.CARD_NAME, MEMBER_CARD.CARD_TYPE, MEMBER_CARD.PAY_FEE, MEMBER_CARD.PAY_TYPE,
                USER_CARD.CARD_NO)
                .from(VIRTUAL_ORDER)
                .leftJoin(MEMBER_CARD).on(MEMBER_CARD.ID.eq(VIRTUAL_ORDER.VIRTUAL_GOODS_ID))
                .leftJoin(USER).on(VIRTUAL_ORDER.USER_ID.eq(USER.USER_ID))
                .leftJoin(USER_CARD).on(VIRTUAL_ORDER.SEND_CARD_NO.eq(USER_CARD.CARD_NO));
        buildOptions(select, param);
        select.orderBy(VIRTUAL_ORDER.ORDER_ID.desc());
        PageResult<MemberCardOrderVo> pageResult = getPageResult(select, param, MemberCardOrderVo.class);
        pageResult.getDataList().forEach(cardOrderVo->{
            //超过一年不能退款
            if (cardOrderVo.getMoneyPaid().compareTo(BigDecimal.ZERO)>0&&cardOrderVo.getPayTime()!=null&& DateUtils.getLocalDateTime().after(DateUtils.getTimeStampPlus(cardOrderVo.getPayTime(),1, ChronoUnit.YEARS))){
                cardOrderVo.setCanReturn(BaseConstant.NO);
            }else {
                cardOrderVo.setCanReturn(BaseConstant.YES);
            }
        });
        return pageResult;
    }

    /**
     * 条件查询
     */
    private void buildOptions(SelectOnConditionStep<? extends Record> select,
                              MemberCardOrderParam param) {
        String orderSn = param.getOrderSn();
        String cardNo = param.getCardNo();
        Byte cardType = param.getCardType();
        String userInfo = param.getUserInfo();
        Timestamp startTime = param.getStartTime();
        Timestamp endTime = param.getEndTime();
        Boolean isRefund = param.getRefund();
        select.where(VIRTUAL_ORDER.GOODS_TYPE.eq(GOODS_TYPE_MEMBER_CARD));
        if(param.getUserId()!=null){
            select.and(VIRTUAL_ORDER.USER_ID.eq(param.getUserId()));
        }
        if (isNotEmpty(orderSn)) {
            select.and(VIRTUAL_ORDER.ORDER_SN.like(format("%s%%", orderSn)));
        }
        if (isNotEmpty(userInfo)) {
            select.and(USER.USERNAME.like(format("%s%%", userInfo)).or(USER.MOBILE.like(format("%s%%", userInfo))));
        }
        if (isNotEmpty(cardNo)) {
            select.and(USER_CARD.CARD_NO.like(format("%s%%", cardNo)));
        }
        if (null != cardType) {
            select.and(MEMBER_CARD.CARD_TYPE.eq(cardType));
        }
        if (null != startTime) {
            select.and(VIRTUAL_ORDER.PAY_TIME.ge(startTime));
        }
        if (null != endTime) {
            select.and(VIRTUAL_ORDER.PAY_TIME.le(endTime));
        }
        if (StringUtil.isNotBlank(param.getPayCode())) {
            select.and(VIRTUAL_ORDER.PAY_CODE.eq(param.getPayCode()));
        }
        if (null != isRefund) {
            if (isRefund) {
                select.and(VIRTUAL_ORDER.RETURN_FLAG.eq(REFUND_STATUS_SUCCESS).or(VIRTUAL_ORDER.RETURN_FLAG.eq(REFUND_STATUS_FAILED)));
            }
        }
        select.and(VIRTUAL_ORDER.DEL_FLAG.eq((byte) 0));
    }

    /**
     * 手动退款
     */
    public JsonResultCode memberCardOrderRefund(VirtualOrderRefundParam param) throws MpException {
        this.virtualOrderRefund(param);

        /** 操作记录 */
        saas().getShopApp(getShopId()).record.insertRecord(Arrays.asList(new Integer[] { RecordContentTemplate.ORDER_MEMBER_CARD_ORDER_REFUND.code }), new String[] {param.getOrderSn()});
        return null;
    }
    /**
     *更新prepayId
     * @param orderSn 订单sn
     * @param prepayId prepayId
     */
    public void updatePrepayId(String orderSn, String prepayId) {
        db().update(VIRTUAL_ORDER).set(VIRTUAL_ORDER.PREPAY_ID, prepayId).where(VIRTUAL_ORDER.ORDER_SN.eq(orderSn)).execute();
    }

    public VirtualOrderRecord getRecord(String orderSn) {
        return db().fetchAny(VIRTUAL_ORDER, VIRTUAL_ORDER.ORDER_SN.eq(orderSn));
    }

    public Integer getExportRows(MemberCardOrderParam param) {
        SelectOnConditionStep<? extends Record> selectFrom = db()
            .selectCount()
            .from(VIRTUAL_ORDER)
            .leftJoin(MEMBER_CARD).on(MEMBER_CARD.ID.eq(VIRTUAL_ORDER.VIRTUAL_GOODS_ID))
            .leftJoin(USER).on(VIRTUAL_ORDER.USER_ID.eq(USER.USER_ID))
            .leftJoin(USER_CARD).on(VIRTUAL_ORDER.SEND_CARD_NO.eq(USER_CARD.CARD_NO));
        buildOptions(selectFrom, param);
        return selectFrom.fetchOptionalInto(int.class).orElse(0);
    }

    public Workbook exportOrderList(MemberCardOrderParam param, String lang) {
        SelectOnConditionStep<? extends Record> selectFrom = db()
            .select(VIRTUAL_ORDER.ORDER_ID, VIRTUAL_ORDER.ORDER_SN,
                VIRTUAL_ORDER.VIRTUAL_GOODS_ID, VIRTUAL_ORDER.RETURN_FLAG, VIRTUAL_ORDER.PAY_TIME, VIRTUAL_ORDER.MONEY_PAID, VIRTUAL_ORDER.CREATE_TIME, VIRTUAL_ORDER.MEMBER_CARD_BALANCE, VIRTUAL_ORDER.RETURN_CARD_BALANCE, VIRTUAL_ORDER.RETURN_MONEY, VIRTUAL_ORDER.RETURN_ACCOUNT, VIRTUAL_ORDER.RETURN_SCORE,
                VIRTUAL_ORDER.USE_ACCOUNT, VIRTUAL_ORDER.USE_SCORE, VIRTUAL_ORDER.RETURN_TIME, VIRTUAL_ORDER.CURRENCY, VIRTUAL_ORDER.ORDER_AMOUNT, USER.USERNAME, USER.MOBILE,
                MEMBER_CARD.CARD_NAME, MEMBER_CARD.CARD_TYPE,
                USER_CARD.CARD_NO, VIRTUAL_ORDER.CURRENCY)
            .from(VIRTUAL_ORDER)
            .leftJoin(MEMBER_CARD).on(MEMBER_CARD.ID.eq(VIRTUAL_ORDER.VIRTUAL_GOODS_ID))
            .leftJoin(USER).on(VIRTUAL_ORDER.USER_ID.eq(USER.USER_ID))
            .leftJoin(USER_CARD).on(VIRTUAL_ORDER.SEND_CARD_NO.eq(USER_CARD.CARD_NO));
        buildOptions(selectFrom, param);
        selectFrom.orderBy(VIRTUAL_ORDER.CREATE_TIME.desc());
        List<MemberCardOrderExportVo> list = selectFrom.fetchInto(MemberCardOrderExportVo.class);

        list.forEach(o -> {
            if (o.getUseScore() != null && o.getUseScore() > 0) {
                o.setPrice(o.getUseScore().toString() + Util.translateMessage(lang, JsonResultMessage.UEXP_SCORE, OrderConstant.LANGUAGE_TYPE_EXCEL));
            } else {
                String cny = "CNY";
                if (cny.equals(o.getCurrency())) {
                    o.setPrice("￥" + o.getOrderAmount().toString());
                } else {
                    o.setPrice("$" + o.getOrderAmount().toString());
                }
            }

            if (REFUND_STATUS_SUCCESS.equals(o.getReturnFlag())) {
                o.setOrderStatusName(Util.translateMessage(lang, JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_REFUNDED, OrderConstant.LANGUAGE_TYPE_EXCEL));
            } else {
                o.setOrderStatusName(Util.translateMessage(lang, JsonResultMessage.VIRTUAL_ORDER_COUPON_PACK_PAYMENT_SUCCESSFUL, OrderConstant.LANGUAGE_TYPE_EXCEL));
            }

            if (CardConstant.MCARD_TP_NORMAL.equals(o.getCardType())) {
                o.setCardTypeString(Util.translateMessage(lang, JsonResultMessage.VIRTUAL_ORDER_MEMBER_CARD_CARD_TYPE_NORMAL, OrderConstant.LANGUAGE_TYPE_EXCEL));
            } else if (CardConstant.MCARD_TP_LIMIT.equals(o.getCardType())) {
                o.setCardTypeString(Util.translateMessage(lang, JsonResultMessage.VIRTUAL_ORDER_MEMBER_CARD_CARD_TYPE_LIMIT, OrderConstant.LANGUAGE_TYPE_EXCEL));
            } else if (CardConstant.MCARD_TP_GRADE.equals(o.getCardType())) {
                o.setCardTypeString(Util.translateMessage(lang, JsonResultMessage.VIRTUAL_ORDER_MEMBER_CARD_CARD_TYPE_GRADE, OrderConstant.LANGUAGE_TYPE_EXCEL));
            }

        });

        Workbook workbook = ExcelFactory.createWorkbook(ExcelTypeEnum.XLSX);
        ExcelWriter excelWriter = new ExcelWriter(lang, workbook);
        excelWriter.writeModelList(list, MemberCardOrderExportVo.class);
        return workbook;
    }

    /**
     * 用户下单统计
     *
     * @param userId
     * @return
     */
    public UserOrderBean getUserOrderStatistics(int userId) {
        return db().select(DSL.count(VIRTUAL_ORDER.ORDER_ID).as("orderNum"),
            DSL.sum(VIRTUAL_ORDER.MONEY_PAID.add(VIRTUAL_ORDER.USE_ACCOUNT).add(VIRTUAL_ORDER.MEMBER_CARD_BALANCE)).as("totalMoneyPaid"))
            .from(VIRTUAL_ORDER)
            .where(VIRTUAL_ORDER.ORDER_STATUS.eq(ORDER_STATUS_FINISHED))
            .and(VIRTUAL_ORDER.GOODS_TYPE.eq(GOODS_TYPE_MEMBER_CARD))
            .and(VIRTUAL_ORDER.USER_ID.eq(userId))
            .fetchAnyInto(UserOrderBean.class);
    }
}
