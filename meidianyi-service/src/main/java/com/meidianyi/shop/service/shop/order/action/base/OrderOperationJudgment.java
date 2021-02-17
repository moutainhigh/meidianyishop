package com.meidianyi.shop.service.shop.order.action.base;

import com.meidianyi.shop.common.foundation.data.BaseConstant;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil.BigDecimalPlus;
import com.meidianyi.shop.common.foundation.util.BigDecimalUtil.Operator;
import com.meidianyi.shop.service.pojo.shop.order.OrderConstant;
import com.meidianyi.shop.service.pojo.shop.order.OrderListInfoVo;
import com.meidianyi.shop.service.pojo.shop.order.goods.OrderGoodsVo;
import com.meidianyi.shop.service.pojo.wxapp.order.OrderListMpVo;
import com.meidianyi.shop.service.shop.order.info.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static com.meidianyi.shop.service.pojo.shop.order.OrderConstant.*;

/**
 * 订单操作判断
 *
 * @author 王帅
 */
@Slf4j
public class OrderOperationJudgment {
    /**
     * 订单是否可退款
     *
     * @param order
     * @param isMp  true小程序；false后台
     * @return true可退；false不可退
     */
    public static boolean isReturnMoney(OrderListInfoVo order, Byte isMp) {
        //后台权限大,不支持退的也可以退
        if (OrderConstant.CFG_RETURN_TYPE_N == order.getReturnTypeCfg() && (isMp == OrderConstant.IS_MP_Y)) {
            return false;
        }
        /**
         * 1退款支持状态:待发货 已发货 已收货 已完成;
         * 2货到付款设置支付金额为0,然后判断支付金额是否>0
         * 3余额支付判断积分抵扣金额，余额消费金额，会员卡消费金额，子单金额
         */
        if (OrderConstant.IS_RETURNMONEY.contains(order.getOrderStatus())
            && !isPayZero(order)) {
            return true;
        }
        // 货到付款加确认收货可仅退款
        if (OrderConstant.ORDER_RECEIVED == order.getOrderStatus()
            && OrderConstant.PAY_CODE_COD.equals(order.getPayCode())) {
            return true;
        }
        //医师可以退审核状态的订单
        if (OrderConstant.IS_AUDIT_ORDER.contains(order.getOrderStatus())&&isMp == OrderConstant.IS_MP_DOCTOR){
            return true;
        }
        return false;
    }

    /**
     * 是否可退货退款，卖家操作
     *
     * @param order
     * @return true可退；false不可退
     */
    public static boolean adminIsReturnGoods(OrderListInfoVo order) {
        //TODO 此功能为卖家专用，现后台退部分发货已发货不支持，后期做
        //已发货、已收货,已完成状态
        if (OrderConstant.IS_RETURNGOODS.contains(order.getOrderStatus())) {
            return true;
        }
        return false;
    }

    /**
     * 是否可退货退款，买家操作
     *
     * @param order
     * @return true可退；false不可退
     */
    public static boolean mpIsReturnGoods(OrderListInfoVo order) {
        //商家是否支持
        if (order.getReturnTypeCfg().equals(OrderConstant.CFG_RETURN_TYPE_N)) {
            return false;
        }
        //部分发货 已发货 已收货 (取消发货后有支付才可以退)
        return (OrderConstant.ORDER_WAIT_DELIVERY == order.getOrderStatus() && OrderConstant.PART_SHIP == order.getPartShipFlag())
            || OrderConstant.ORDER_SHIPPED == order.getOrderStatus()
            || OrderConstant.ORDER_RECEIVED == order.getOrderStatus();
    }

    /**
     * 退运费，买家操作
     *
     * @param shippingFee      运费
     * @param returnShipingFee 已退运费/已退+此次要退
     * @param flag             true query;false execute
     * @return true可退；false不可退
     */
    public static Boolean adminIsReturnShipingFee(BigDecimal shippingFee, BigDecimal returnShipingFee, boolean flag) {
        int result = BigDecimalUtil.compareTo(shippingFee, returnShipingFee);
        if (flag) {
            if (result <= 0) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        } else {
            if (result < 0) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }

    }

    /**
     * 判断当前订单是否支付为0：
     * 余额支付：积分抵扣金额，余额消费金额，会员卡消费金额，子单金额（
     * 货到付款：设置支付金额moneyPaid为0）
     *
     * @param order
     * @return
     */
    public static boolean isPayZero(OrderListInfoVo order) {
        return BigDecimalUtil.compareTo(OrderConstant.PAY_CODE_COD.equals(order.getPayCode()) ? BigDecimal.ZERO : order.getMoneyPaid(), BigDecimal.ZERO) > 1
            || ((OrderConstant.PAY_CODE_BALANCE_PAY.equals(order.getPayCode())
            && (BigDecimalUtil.compareTo(order.getScoreDiscount(), BigDecimal.ZERO) > 1
            || BigDecimalUtil.compareTo(order.getUseAccount(), BigDecimal.ZERO) > 1
            || BigDecimalUtil.compareTo(order.getMemberCardBalance(), BigDecimal.ZERO) > 1
            || BigDecimalUtil.compareTo(order.getSubGoodsPrice(), BigDecimal.ZERO) > 1)));
    }

    /**
     * 退货判断
     *
     * @param order
     * @param isMp
     * @return
     */
    public static boolean isReturnGoods(OrderListInfoVo order, Byte isMp) {
        if (isMp == OrderConstant.IS_MP_Y) {
            //判断mp是否可以退货
            if (mpIsReturnGoods(order)) {
                return true;
            }
        } else {
            //后台是否可以退货
            if (adminIsReturnGoods(order)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 订单是否可以取消
     *
     * @param order
     * @return
     */
    public static boolean mpIsCancel(OrderListInfoVo order) {
        return (
            //1待付款  且  无补款或补款未支付
            (order.getOrderStatus() == OrderConstant.ORDER_WAIT_PAY && order.getBkOrderPaid() == OrderConstant.BK_PAY_NO)
                //2待发货  且 余额支付  且 系统金额为0(。。。不可以取消)
                //|| order.getOrderStatus() == OrderConstant.ORDER_WAIT_DELIVERY && order.getPayCode() == OrderConstant.PAY_CODE_BALANCE_PAY && BigDecimalUtil.compareTo(getOnlinePayAmount(order), null) == 0
                //3待发货  且  货到付款(。。。不可以取消)
                || order.getOrderStatus() == OrderConstant.ORDER_WAIT_DELIVERY && order.getPayCode() == OrderConstant.PAY_CODE_COD
        );
    }

    /**
     * 获取系统金额
     *
     * @param order
     * @return
     */
    public static BigDecimal getOnlinePayAmount(OrderListInfoVo order) {
        return BigDecimalUtil.addOrSubtrac(
            BigDecimalPlus.create(OrderConstant.PAY_CODE_COD.equals(order.getPayCode()) ? BigDecimal.ZERO : order.getMoneyPaid(), Operator.add),
            BigDecimalPlus.create(order.getScoreDiscount(), Operator.add),
            BigDecimalPlus.create(order.getMemberCardBalance(), Operator.add),
            BigDecimalPlus.create(order.getUseAccount(), null));
    }

    /**
     * 订单是否可以关闭
     *
     * @param order
     * @param isMp
     * @return
     */
    public static boolean mpIsClose(OrderListInfoVo order, Byte isMp) {
        return (
            //待支付不存在补款或(自动任务补款未支付)
            (order.getOrderStatus() == OrderConstant.ORDER_WAIT_PAY
                && (order.getBkOrderPaid() == OrderConstant.BK_PAY_NO
                || (isMp.equals(OrderConstant.IS_MP_AUTO) && order.getBkOrderPaid() == OrderConstant.BK_PAY_FRONT)))
                //待发货 且 货到付款 且 系统支付0
                || (order.getOrderStatus() == OrderConstant.ORDER_WAIT_DELIVERY
                && BigDecimalUtil.compareTo(getOnlinePayAmount(order), null) == 0
                && OrderConstant.PAY_CODE_COD.equals(order.getPayCode()))
        );
    }

    /**
     * 订单是否可以完成
     *
     * @param order
     * @return true可完成； false不可完
     */
    public static boolean mpIsFinish(OrderListInfoVo order, Integer isReturnCount) {
        if (order.getOrderStatus() == OrderConstant.ORDER_RECEIVED) {
            if (order.getReturnTypeCfg() == OrderConstant.CFG_RETURN_TYPE_N) {
                return true;
            }
            if (isReturnCount != null && isReturnCount > 0) {
                return false;
            }
            if (order.getReturnTypeCfg() == OrderConstant.CFG_RETURN_TYPE_Y
                && Instant.now().isAfter(order.getConfirmTime().toInstant().plusSeconds(Duration.ofDays(order.getOrderTimeoutDays()).getSeconds()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 订单是否可以核销
     *
     * @param order
     * @return
     */
    public static boolean isVerify(OrderListInfoVo order) {
        if (order.getOrderStatus() == OrderConstant.ORDER_WAIT_DELIVERY && order.getDeliverType() == DELIVER_TYPE_SELF) {
            return true;
        }
        return false;
    }

    /**
     * mp端订单删除
     *
     * @param order
     * @return
     */
    public static boolean isDelete(OrderListMpVo order) {
        if (order.getOrderStatus() == OrderConstant.ORDER_CANCELLED || order.getOrderStatus() == OrderConstant.ORDER_CLOSED) {
            return true;
        }
        return false;
    }

    /**
     * mp端订单收货
     *
     * @param order
     * @return
     */
    public static boolean isReceive(OrderListInfoVo order) {
        if (order.getOrderStatus() == OrderConstant.ORDER_SHIPPED) {
            return true;
        }
        return false;
    }

    /**
     * admin设置操作
     *
     * @param order
     * @param isReturnCount
     * @param canBeShippedList
     */
    public static void operationSet(OrderListInfoVo order, Integer isReturnCount, List<OrderGoodsVo> canBeShippedList) {
        //设置所有订单是否可以关闭
        if (OrderOperationJudgment.mpIsClose(order, OrderConstant.IS_MP_ADMIN)) {
            order.setCanClose(Boolean.TRUE);
        }
        //设置所有订单是否可以完成
        if (OrderOperationJudgment.mpIsFinish(order, isReturnCount)) {
            order.setCanFinish(Boolean.TRUE);
        }
        //待发货状态判断是否可发货
        if (order.getOrderStatus() == OrderConstant.ORDER_WAIT_DELIVERY) {
            if (CollectionUtils.isNotEmpty(canBeShippedList)) {
                order.setCanDeliver(Boolean.TRUE);
            }
        }
        //判断核销
        if (OrderOperationJudgment.isVerify(order)) {
            order.setCanVerify(Boolean.TRUE);
        }
    }

    /**
     * 延长收货（状态或者配置）
     *
     * @param order
     * @param extendReceiveDays
     * @return
     */
    public static Boolean isExtendReceive(OrderListMpVo order, int extendReceiveDays) {
        if (extendReceiveDays > 0 && order.getOrderStatus() == OrderConstant.ORDER_SHIPPED) {
            //自动收货时间
            Instant autoReceive = order.getShippingTime().toInstant().plusSeconds(Duration.ofDays(order.getReturnDaysCfg()).getSeconds());
            //用户在订单自动确认收货前2天可对该笔订单申请延长收货时间，申请后可延长 extendReceiveDays天，每笔订单可申请一次
            int twoDays = 2;
            if (Instant.now().plusSeconds(Duration.ofDays(twoDays).getSeconds()).isAfter(autoReceive)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 订单是否可以成为待发货订单
     *
     * @param orderStatus
     * @return
     */
    public static Boolean canWaitDeliver(Byte orderStatus) {
        if (orderStatus < OrderConstant.ORDER_WAIT_DELIVERY) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 订单是否显示提醒发货按钮(待发货 and 非 送礼)
     * 每日只能提醒一次与提醒次数不超过三次不在此逻辑判断
     *
     * @param order
     * @return
     */
    public static boolean isShowRemindShip(OrderListMpVo order) {
        if (CollectionUtils.isEmpty(order.getOrderType())) {
            order.setOrderType(Arrays.asList(OrderInfoService.orderTypeToByte(order.getGoodsType())));
        }
        if (order.getDeliverType() == DELIVER_TYPE_SELF) {
            return false;
        }
        if (order.getOrderStatus() == OrderConstant.ORDER_WAIT_DELIVERY && !order.getOrderType().contains(BaseConstant.ACTIVITY_TYPE_GIVE_GIFT)) {
            return true;
        }
        return false;
    }

    /**
     * 是否展示再次购买
     *
     * @param order
     * @return
     */
    public static boolean isShowAgainBuy(OrderListMpVo order) {
        if (order.getOrderStatus() != OrderConstant.ORDER_WAIT_PAY && order.getIsLotteryGift() == 0) {
            return true;
        }
        return false;
    }

    public static void operationSet(OrderListMpVo source) {
        OrderListInfoVo target = new OrderListInfoVo();
        try {
            PropertyUtils.copyProperties(target, source);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error(OrderOperationJudgment.class.getName() + "调用operationSet时PropertyUtils.copyProperties异常");
            return;
        }
        //退款
        source.setIsReturnMoney(isReturnMoney(target, OrderConstant.IS_MP_Y) ? YES : NO);
        //退货
        source.setIsReturnGoods(mpIsReturnGoods(target) ? YES : NO);
        ;
        //取消
        source.setIsCancel(mpIsCancel(target) ? YES : NO);
        //删除
        source.setIsDelete(isDelete(source) ? YES : NO);
    }
}
