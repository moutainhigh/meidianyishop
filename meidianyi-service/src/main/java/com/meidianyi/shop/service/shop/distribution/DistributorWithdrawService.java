package com.meidianyi.shop.service.shop.distribution;

import com.github.binarywang.wxpay.bean.result.WxPayRedpackQueryResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.foundation.util.PageResult;
import com.meidianyi.shop.db.shop.tables.DistributionWithdraw;
import com.meidianyi.shop.db.shop.tables.records.DistributionWithdrawRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorWithdrawDetailVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorWithdrawListParam;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorWithdrawListVo;
import com.meidianyi.shop.service.pojo.shop.distribution.DistributorWithdrawSumDetailVo;
import com.meidianyi.shop.service.pojo.wxapp.withdraw.WithdrawApplyParam;
import com.meidianyi.shop.service.shop.payment.MpPaymentService;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.UpdateSetFirstStep;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.meidianyi.shop.db.shop.Tables.DISTRIBUTION_WITHDRAW;
import static com.meidianyi.shop.db.shop.Tables.USER;
import static com.meidianyi.shop.db.shop.Tables.USER_DETAIL;
import static org.jooq.impl.DSL.field;

/**
 * 分销提现相关
 * @author 常乐
 * 2019年8月13日
 */
@Service
public class DistributorWithdrawService extends ShopBaseService{
    @Autowired
    private MpPaymentService mpPaymentService;

    @Autowired
    private DistributionMessageTmpService disMesTmpSer;

    private final DistributionWithdraw TABLE = DistributionWithdraw.DISTRIBUTION_WITHDRAW;
    /**
     * 分销提现审核列表
     * @param param
     * @return
     */
    public PageResult<DistributorWithdrawListVo> getWithdrawList(DistributorWithdrawListParam param) {
        SelectJoinStep<? extends Record> select = db().select(DISTRIBUTION_WITHDRAW.ID,DISTRIBUTION_WITHDRAW.USER_ID,USER.USERNAME,USER.MOBILE,DISTRIBUTION_WITHDRAW.REAL_NAME,DISTRIBUTION_WITHDRAW.CREATE_TIME,DISTRIBUTION_WITHDRAW.ORDER_SN,
            DISTRIBUTION_WITHDRAW.WITHDRAW_CASH,DISTRIBUTION_WITHDRAW.CHECK_TIME,DISTRIBUTION_WITHDRAW.STATUS,DISTRIBUTION_WITHDRAW.REFUSE_DESC,DISTRIBUTION_WITHDRAW.DESC,DISTRIBUTION_WITHDRAW.TYPE,
            DISTRIBUTION_WITHDRAW.WITHDRAW_NUM,DISTRIBUTION_WITHDRAW.WITHDRAW_USER_NUM)
            .from(DISTRIBUTION_WITHDRAW
                .leftJoin(USER).on(DISTRIBUTION_WITHDRAW.USER_ID.eq(USER.USER_ID)))
            .leftJoin(USER_DETAIL).on(DISTRIBUTION_WITHDRAW.USER_ID.eq(USER_DETAIL.USER_ID));
        buildOptions(select,param);
        PageResult<DistributorWithdrawListVo> pageList = this.getPageResult(select, param.getCurrentPage(), param.getPageRows(), DistributorWithdrawListVo.class);
        return pageList;
    }


    /**
     * 返利提现审核列表条件查询
     */
    public void buildOptions(SelectJoinStep<? extends Record> select ,DistributorWithdrawListParam param) {
        //申请人id
        if(param.getUserId() != null) {
            select.where(DISTRIBUTION_WITHDRAW.USER_ID.eq(param.getUserId()));
        }
        //申请人昵称
        if(param.getUsername() != null) {
            select.where(USER.USERNAME.contains(param.getUsername()));
        }
        //手机号
        if(param.getMobile() != null) {
            select.where(USER.MOBILE.contains(param.getMobile()));
        }
        //真实姓名
        if(param.getRealName() != null) {
            select.where(USER_DETAIL.REAL_NAME.contains(param.getRealName()));
        }
        //提现单号
        if(param.getOrderSn() != null) {
            select.where(DISTRIBUTION_WITHDRAW.ORDER_SN.contains(param.getOrderSn()));
        }
        //申请时间
        if(param.getStartCreateTime() != null && param.getEndCreateTime() != null) {
            select.where(DISTRIBUTION_WITHDRAW.CREATE_TIME.ge(param.getStartCreateTime())).and(DISTRIBUTION_WITHDRAW.CREATE_TIME.le(param.getEndCreateTime()));
        }
        //操作时间
        if(param.getStartOptTime() != null && param.getEndOptTime() != null) {
            select.where(DISTRIBUTION_WITHDRAW.CREATE_TIME.ge(param.getStartOptTime())).and(DISTRIBUTION_WITHDRAW.CREATE_TIME.le(param.getEndOptTime()));
        }
        //提现金额
        if(param.getStartWithdrawCash() != null && param.getEndWithdrawCash() != null) {
            select.where(DISTRIBUTION_WITHDRAW.WITHDRAW_CASH.ge(param.getStartWithdrawCash())).and(DISTRIBUTION_WITHDRAW.WITHDRAW_CASH.le(param.getEndWithdrawCash()));
        }
        //处理状态
        if(param.getStatus() != null) {
            select.where(DISTRIBUTION_WITHDRAW.STATUS.eq(param.getStatus()));
        }
        select.orderBy(DISTRIBUTION_WITHDRAW.CREATE_TIME.desc());
    }

    /**
     * 提现审核详情
     * @param id
     * @return
     */
    public DistributorWithdrawDetailVo getWithdrawDetail(Integer id) {
        DistributorWithdrawDetailVo detail = db().select(USER.USER_ID,USER.USERNAME,USER.MOBILE,field(USER.CREATE_TIME.as("user_create_time")),DISTRIBUTION_WITHDRAW.REAL_NAME,DISTRIBUTION_WITHDRAW.CREATE_TIME,
            DISTRIBUTION_WITHDRAW.ORDER_SN,DISTRIBUTION_WITHDRAW.WITHDRAW_CASH,DISTRIBUTION_WITHDRAW.CHECK_TIME,DISTRIBUTION_WITHDRAW.STATUS,DISTRIBUTION_WITHDRAW.REFUSE_DESC,DISTRIBUTION_WITHDRAW.DESC,
            DISTRIBUTION_WITHDRAW.TYPE,DISTRIBUTION_WITHDRAW.WITHDRAW_USER_NUM,DISTRIBUTION_WITHDRAW.WITHDRAW_NUM,DISTRIBUTION_WITHDRAW.WITHDRAW)
            .from(DISTRIBUTION_WITHDRAW
                .leftJoin(USER).on(DISTRIBUTION_WITHDRAW.USER_ID.eq(USER.USER_ID)))
            .where(DISTRIBUTION_WITHDRAW.ID.eq(id))
            .fetchOne().into(DistributorWithdrawDetailVo.class);
        //获取当前用户ID
        int userId = db().select(DISTRIBUTION_WITHDRAW.USER_ID).from(DISTRIBUTION_WITHDRAW).where(DISTRIBUTION_WITHDRAW.ID.eq(id)).fetchOne().into(Integer.class);

        DistributorWithdrawListParam param = new DistributorWithdrawListParam();
        param.setUserId(userId);
        //当前用户其他提现记录
        PageResult<DistributorWithdrawListVo> otherWithdrawRecord = this.getWithdrawList(param);
        detail.setUserWithdrawList(otherWithdrawRecord);
        return detail;
    }

    /**
     * 根据userid获取分销提现
     * @param userId
     * @return
     */
    public DistributionWithdrawRecord getWithdrawByUserId(Integer userId) {
        return db().selectFrom(DISTRIBUTION_WITHDRAW).where(DISTRIBUTION_WITHDRAW.USER_ID.eq(userId)).fetchAny();
    }

    /**
     * 获取已提现金额
     * @return
     */
    public BigDecimal getDoneWithDraw(Integer userId) {
        return db().select(DSL.sum(DISTRIBUTION_WITHDRAW.WITHDRAW_CASH))
            .from(DISTRIBUTION_WITHDRAW)
            .where(DISTRIBUTION_WITHDRAW.USER_ID.eq(userId))
            .and(DISTRIBUTION_WITHDRAW.STATUS.eq((byte)4))
            .fetchOptionalInto(BigDecimal.class)
            .orElse(BigDecimal.ZERO);
    }

    /**
     * 提现记录
     * @param param
     * @return
     */
    public DistributorWithdrawSumDetailVo withdrawList(DistributorWithdrawListParam param) {

        PageResult<DistributorWithdrawListVo> data = getWithdrawList(param);
        BigDecimal doneWithDraw = getDoneWithDraw(param.getUserId());
        return DistributorWithdrawSumDetailVo
            .builder()
            .data(data)
            .withdrawCrash(doneWithDraw)
            .build();
    }

    /**
     * 根据userid获取分销提现记录的真实姓名
     * @param userId
     * @return
     */
    public String getUserRealName(Integer userId) {
        return db().select(DISTRIBUTION_WITHDRAW.REAL_NAME)
            .from(DISTRIBUTION_WITHDRAW)
            .where(DISTRIBUTION_WITHDRAW.USER_ID.eq(userId).and(DISTRIBUTION_WITHDRAW.STATUS.gt((byte)2)))
            .orderBy(DISTRIBUTION_WITHDRAW.CREATE_TIME.desc())
            .fetchAnyInto(String.class);

    }


    public int count(Integer userId) {
        if(userId == null) {
            return db().selectCount().from(TABLE).execute();
        }
        return db().selectCount().from(TABLE).where(TABLE.USER_ID.eq(userId)).execute();
    }

    public DistributionWithdrawRecord insert(WithdrawApplyParam param, byte type, String orderSn, int withdrawNum, int withdrawUserNum, BigDecimal totalMoney, String toJson) {
        DistributionWithdrawRecord record = db().newRecord(TABLE);
        record.setWithdrawCash(param.getMoney());
        record.setUserId(param.getUserId());
        record.setRealName(param.getRealName());
        record.setType(type);
        record.setOrderSn(orderSn);
        record.setWithdrawNum(String.valueOf(withdrawNum));
        record.setWithdrawUserNum(String.valueOf(withdrawUserNum));
        record.setWithdrawSource(toJson);
        record.setWithdraw(totalMoney);
        record.insert();
        //提现申请通知
        disMesTmpSer.withdrawApplyMes(param.getMoney(),param.getUserId());
        return record;
    }

    public DistributionWithdrawRecord get(String orderSn){
        return db().selectFrom(TABLE).where(TABLE.ORDER_SN.eq(orderSn)).fetchAny();
    }

    public void update(Integer id, byte status, String refuseDesc) {
        UpdateSetFirstStep<DistributionWithdrawRecord> update = db().update(TABLE);
        switch (status){
            case (byte)2:
                update.set(TABLE.STATUS, status).set(TABLE.REFUSE_TIME, DateUtils.getSqlTimestamp());
                break;
            case (byte)3:
                update.set(TABLE.STATUS, status).set(TABLE.CHECK_TIME, DateUtils.getSqlTimestamp());
                break;
            case (byte)4:
                update.set(TABLE.STATUS, status).set(TABLE.BILLING_TIME, DateUtils.getSqlTimestamp());
                break;
            case (byte)5:
                update.set(TABLE.STATUS, status).set(TABLE.FAIL_TIME, DateUtils.getSqlTimestamp());
                break;
            case (byte)6:
                update.set(TABLE.STATUS, status).set(TABLE.BILLING_TIME, DateUtils.getSqlTimestamp());
                break;
            default:
        }
        update.set(TABLE.REFUSE_DESC, refuseDesc).where(TABLE.ID.eq(id)).execute();
    }

    /**
     * 处理已发放待领取状态的提现单
     */
    public void dealNoFinishWithdraw()
    {
        logger().info("distributor dealNoFinishWithdraw start");
        DistributorWithdrawListParam param = new DistributorWithdrawListParam();
        param.setStatus((byte) 6);
        param.setCurrentPage(1);
        param.setPageRows(10);
        // 已领取状态
        List<Integer> receivedList = new ArrayList<>();
        // 已退款状态
        List<Integer> refundList = new ArrayList<>();
        String receive = "RECEIVED";
        String refund = "REFUND";
        try {
            while (true) {
                PageResult<DistributorWithdrawListVo> pageResult = this.getWithdrawList(param);
                List<DistributorWithdrawListVo> data = pageResult.getDataList();
                for (DistributorWithdrawListVo item : data) {
                    WxPayRedpackQueryResult result = mpPaymentService.queryRedpack(item.getOrderSn());
                    if (StringUtils.isBlank(result.getDetailId())) {
                        continue;
                    }
                    if (result.getStatus().equals(receive)) {
                        receivedList.add(item.getId());
                    } else if (result.getStatus().equals(refund)) {
                        refundList.add(item.getId());
                    }
                }
                if (param.getCurrentPage().compareTo(pageResult.getPage().getLastPage()) >= 0) {
                    break;
                }
                param.setCurrentPage(pageResult.getPage().getNextPage());
            }
            for (int i = 0; i < receivedList.size(); i++) {
                this.update(receivedList.get(i), (byte) 4, null);
            }
            for (int i = 0; i < refundList.size(); i++) {
                this.update(refundList.get(i), (byte) 5, null);
            }
        } catch (WxPayException e) {
            logger().error("mpPaymentService queryRedpack error:" + e.getReturnMsg());
        } catch (Exception e) {
            logger().error("dealNoFinishWithdraw error:" + e.getMessage());
        } finally {
            logger().info("distributor dealNoFinishWithdraw end");
        }
    }
}
