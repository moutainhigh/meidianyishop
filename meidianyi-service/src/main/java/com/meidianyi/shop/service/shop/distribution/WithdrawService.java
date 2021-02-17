package com.meidianyi.shop.service.shop.distribution;

import com.meidianyi.shop.common.foundation.data.DistributionConstant;
import com.meidianyi.shop.common.foundation.data.JsonResultCode;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.db.shop.tables.UserTotalFanli;
import com.meidianyi.shop.db.shop.tables.records.DistributionWithdrawRecord;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.foundation.util.IncrSequenceUtil;
import com.meidianyi.shop.service.pojo.shop.config.distribution.DistributionParam;
import com.meidianyi.shop.service.pojo.shop.distribution.withdraw.WithdrawAuditParam;
import com.meidianyi.shop.service.pojo.shop.distribution.withdraw.WithdrawRemarkParam;
import com.meidianyi.shop.service.pojo.shop.member.data.AccountData;
import com.meidianyi.shop.service.pojo.shop.operation.RecordTradeEnum;
import com.meidianyi.shop.service.pojo.shop.operation.RemarkTemplate;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.wxapp.withdraw.WithdrawApplyParam;
import com.meidianyi.shop.service.shop.config.DistributionConfigService;
import com.meidianyi.shop.service.shop.operation.RecordTradeService;
import com.meidianyi.shop.service.shop.payment.MpPaymentService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.meidianyi.shop.common.foundation.data.WithdrawConstant.*;
import static com.meidianyi.shop.db.shop.Tables.USER_TOTAL_FANLI;

/**
 * 分销提现
 * @author ws
 */
@Service
public class WithdrawService extends ShopBaseService {

    @Autowired
    private DistributionConfigService distributionConf;

    @Autowired
    private UserTotalFanliService userTotalFanli;

    @Autowired
    private DistributorWithdrawService distributorWithdraw;

    @Autowired
    private RecordTradeService recordMemberTrade;

    @Autowired
    private MpPaymentService mpPaymentService;

    @Autowired
    private DistributionMessageTmpService disMesTmp;

    final static UserTotalFanli TABLE = USER_TOTAL_FANLI;

    /**
     * 提现申请
     * @param param
     * @throws MpException
     */
    public void apply(WithdrawApplyParam param) throws MpException {
        logger().info("withdraw apply start,param : {}", param);
        DistributionParam cfg = distributionConf.getDistributionCfg();
        applyCheck(cfg, param);
        addWithdrawRecord(cfg, param);
        logger().info("withdraw apply end");
    }

    /**
     * 申提现请校验
     * @param param
     * @throws MpException
     */
    private void applyCheck(DistributionParam cfg, WithdrawApplyParam param) throws MpException {
        if(cfg == null) {
            throw new MpException(JsonResultCode.CODE_FAIL);
        }
        if(cfg.getStatus() == null || cfg.getStatus() == OrderConstant.NO) {
            //开关未开
            throw new MpException(JsonResultCode.CODE_FAIL);
        }
        if(cfg.getWithdrawStatus() == null || cfg.getWithdrawStatus() == OrderConstant.NO) {
            //开关未开
            throw new MpException(JsonResultCode.CODE_FAIL);
        }
        if(StringUtils.isBlank(param.getRealName())) {
            throw new MpException(JsonResultCode.DISTRIBUTOR_WITHDRAW_REALNAME_NOT_NULL);
        }
        if(cfg.getWithdrawCash() != null && cfg.getWithdrawCash() > 0 && BigDecimalUtil.compareTo(param.getMoney(), BigDecimalUtil.valueOf(cfg.getWithdrawCash().intValue())) < 0) {
            throw new MpException(JsonResultCode.DISTRIBUTOR_WITHDRAW_MINIMUM_LIMIT_MONEY, null, cfg.getWithdrawCash().toString());
        }
        //查询可提现金额
        BigDecimal totalMoney = userTotalFanli.getTotalMoney(param.getUserId());
        if(BigDecimalUtil.compareTo(param.getMoney(), totalMoney) > 0) {
            throw new MpException(JsonResultCode.DISTRIBUTOR_WITHDRAW_MAXIMUM_LIMIT_MONEY, null, totalMoney.toString());
        }
    }

    /**
     * 提现
     * @param cfg
     * @param param
     */
    private void addWithdrawRecord(DistributionParam cfg, WithdrawApplyParam param) throws MpException {
        //返利方式
        byte type = DistributionConstant.getWithdrawType(cfg.getWithdrawSource());
        //提现单号
        String orderSn = IncrSequenceUtil.generateOrderSn(DistributionConstant.ORDER_SN_PREFIX);
        //流水号
        int withdrawNum = distributorWithdraw.count(null);
        //用户提现序号
        int withdrawUserNum = distributorWithdraw.count(param.getUserId());
        //可提现金额
        BigDecimal totalMoney = userTotalFanli.getTotalMoney(param.getUserId());
        //pk
        DistributionWithdrawRecord record = distributorWithdraw.insert(param, type, orderSn, withdrawNum, withdrawUserNum, totalMoney, cfg.getWithdrawSource());
        //余额扣减
        updateAccount(orderSn, param.getUserId(), param.getMoney().negate());
        //获取返利汇总信息
        //UserTotalFanliVo userRebate = userTotalFanli.getUserRebate(param.getUserId());
        //修改可返利金额并冻结
        userTotalFanli.blockedOrThawAmount(param.getUserId(), BigDecimalUtil.subtrac(userTotalFanli.getTotalMoney(param.getUserId()), param.getMoney()));
        //TODO 添加提现订单明细
        addWithdrawDetail(record);
    }

    private void addWithdrawDetail(DistributionWithdrawRecord record) {

    }

    private void updateAccount(String orderSn, Integer userId, BigDecimal money) throws MpException {
        logger().info("冻结/恢复余额start:{}", money);
        if(BigDecimalUtil.compareTo(money, null) == 0) {
            return;
        }
        AccountData accountData = AccountData.newBuilder().
            userId(userId).
            orderSn(orderSn).
            //下单金额
            amount(money).
            remarkCode(BigDecimalUtil.compareTo(money, null) < 0 ? RemarkTemplate.DISTRIBUTION_WITHDRAW.code : RemarkTemplate.DISTRIBUTION_THAW.code).
            remarkData(orderSn).
            //支付类型
            isPaid(RecordTradeEnum.UACCOUNT_WITHDRAW.val()).
            //后台处理时为操作人id为0
            adminUser(0).
            tradeType(RecordTradeEnum.TYPE_DEFAULT.val()).
            tradeFlow(BigDecimalUtil.compareTo(money, null) < 0 ? RecordTradeEnum.TRADE_FLOW_IN.val() : RecordTradeEnum.TRADE_FLOW_OUT.val()).
            noSendMsg(true).
            build();
        //调用退余额接口
        recordMemberTrade.updateUserEconomicData(accountData);
        logger().info("冻结/恢复余额end");
    }

    public void audit(WithdrawAuditParam param) throws MpException {
        DistributionWithdrawRecord record = distributorWithdraw.get(param.getOrderSn());
        if(record == null) {
            throw new MpException(JsonResultCode.CODE_FAIL);
        }
        //1待审核 2拒绝 3已审核待出账 4出账成功 5失败 6已发红包
        if(record.getStatus().equals(WITHDRAW_CHECK_WAIT_CHECK) && param.getIsPass().equals(OrderConstant.YES)) {
            //审核通过
            distributorWithdraw.update(record.getId(), WITHDRAW_CHECK_WAIT_PAY, null);
        } else if(ArrayUtils.contains(new Byte[] {WITHDRAW_CHECK_WAIT_CHECK, WITHDRAW_CHECK_WAIT_PAY}, record.getStatus()) && param.getIsPass().equals(OrderConstant.NO)) {
            transaction(()->{
                //拒绝
                distributorWithdraw.update(record.getId(), WITHDRAW_CHECK_REFUSE, param.getRefuseDesc());
                //恢复余额
                updateAccount(param.getOrderSn(), record.getUserId(), record.getWithdrawCash());
                //修改可返利金额并冻结
                userTotalFanli.blockedOrThawAmount(record.getUserId(), BigDecimalUtil.add(userTotalFanli.getTotalMoney(record.getUserId()), record.getWithdrawCash()));
            });
            //审核不通过发送消息推送
            String msg = "审核不通过";
            String remark = "您的提现申请审核未通过";
            String page = "/pages/widthdraw/widthdraw";
            disMesTmp.distributorCheckPassMes(msg,remark, Util.currentTimeStamp(),page,record.getUserId());
        } else if(param.getIsPass().equals(OrderConstant.YES) && record.getStatus().equals(WITHDRAW_CHECK_WAIT_PAY)) {
            //出账
            mpPaymentService.pay2Person(param.getOrderSn(),param.getClientIp(), record.getRealName(), record.getUserId(), record.getType(), record.getWithdrawCash());
            if(record.getType().equals(DistributionConstant.RT_SUB_MCH)) {
                distributorWithdraw.update(record.getId(), WITHDRAW_CHECK_SEND_PACKAGE, null);
            }else {
                distributorWithdraw.update(record.getId(), WITHDRAW_CHECK_PAY_SUCCESS, null);
            }

            //审核通过发送消息推送
            String msg = "审核通过";
            String remark = "您的提现申请审核通过";
            String page = "/pages/widthdraw/widthdraw";
            disMesTmp.distributorCheckPassMes(msg,remark,Util.currentTimeStamp(),page,record.getUserId());
        }
    }

    /**
     * 提现详情添加备注
     * @param param
     * @return
     */
    public int addWithdrawRemark(WithdrawRemarkParam param) {
        DistributionWithdrawRecord record = new DistributionWithdrawRecord();
        assign(param, record);
        return db().executeUpdate(record);
    }

    public void blockedOrThawAmount(Integer userId, BigDecimal money) {
        db().update(TABLE).set(TABLE.TOTAL_MONEY, money).where(TABLE.USER_ID.eq(userId)).execute();
    }
}