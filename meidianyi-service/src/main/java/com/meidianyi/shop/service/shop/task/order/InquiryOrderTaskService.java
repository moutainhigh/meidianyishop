package com.meidianyi.shop.service.shop.task.order;

import com.meidianyi.shop.common.foundation.data.ImSessionConstant;
import com.meidianyi.shop.common.foundation.util.DateUtils;
import com.meidianyi.shop.common.pojo.main.table.PlatformTotalRebateDo;
import com.meidianyi.shop.common.pojo.shop.table.ImSessionDo;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderDo;
import com.meidianyi.shop.dao.main.platform.PlatformTotalRebateDao;
import com.meidianyi.shop.dao.shop.order.InquiryOrderDao;
import com.meidianyi.shop.dao.shop.rebate.DoctorTotalRebateDao;
import com.meidianyi.shop.dao.shop.rebate.InquiryOrderRebateDao;
import com.meidianyi.shop.service.foundation.exception.MpException;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.shop.maptemplate.ConsultationOrderExpireParam;
import com.meidianyi.shop.service.pojo.shop.rebate.InquiryOrderRebateConstant;
import com.meidianyi.shop.service.pojo.wxapp.order.inquiry.InquiryOrderConstant;
import com.meidianyi.shop.service.shop.im.ImSessionService;
import com.meidianyi.shop.service.shop.maptemplatesend.MapTemplateSendService;
import com.meidianyi.shop.service.shop.order.inquiry.InquiryOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * 问诊订单定时任务
 *
 * @author yangpengcheng
 */
@Service
public class InquiryOrderTaskService extends ShopBaseService {
    @Autowired
    private InquiryOrderService inquiryOrderService;
    @Autowired
    private InquiryOrderDao inquiryOrderDao;
    @Autowired
    private ImSessionService imSessionService;
    @Autowired
    private MapTemplateSendService mapTemplateSendService;
    @Autowired
    private InquiryOrderRebateDao inquiryOrderRebateDao;
    @Autowired
    private DoctorTotalRebateDao doctorTotalRebateDao;
    @Autowired
    private PlatformTotalRebateDao platformTotalRebateDao;
    /**
     * 自动任务关闭待支付的问诊订单
     */
    public void close() {
        logger().info("问诊订单关闭定时任务start,shop:{}", getShopId());
        List<InquiryOrderDo> orderList = inquiryOrderService.getCanceledToPaidCloseOrder();
        orderList.forEach(order -> {
            order.setOrderStatus(InquiryOrderConstant.ORDER_CANCELED);
            order.setCancelledTime(DateUtils.getLocalDateTime());
            inquiryOrderDao.update(order);
        });
        logger().info("问诊订单关闭定时任务end");
    }






    /**
     * 待接诊订单自动关闭
     */
    public void closeToWaitingInquiryOrder()  {
        logger().info("待接诊问诊订单关闭定时任务start,shop:{}", getShopId());
        autoCloseToWaitingInquiryOrder();
        logger().info("待接诊问诊订单关闭定时任务end");
    }

    /**
     * 自动任务关闭待接诊的问诊订单退款
     */
    public void autoCloseToWaitingInquiryOrder()  {
        List<InquiryOrderDo> orderList = inquiryOrderService.getCanceledToWaitingCloseOrder();
        orderList.forEach(order -> {
            transaction(()->{
                if(order.getOrderAmount().compareTo(BigDecimal.ZERO)<=0){
                    order.setOrderStatus(InquiryOrderConstant.ORDER_REFUND);
                    inquiryOrderDao.update(order);
                    List<String> orderSns=new ArrayList<>();
                    orderSns.add(order.getOrderSn());
                    imSessionService.batchCancelSession(orderSns);
                    logger().info("问诊订单自动任务,待接诊0元订单取消,orderSn:{}", order.getOrderSn());
                }else {
                    try {
                        refundExecute(order);
                        logger().info("问诊订单自动任务,待接诊订单退款成功,orderSn:{}", order.getOrderSn());
                    } catch (MpException e) {
                        logger().error("问诊订单自动任务,待接诊订单退款失败,orderSn:{},错误信息{}{}", order.getOrderSn(), e.getErrorCode(), e.getMessage());
                        throw e;
                    }
                }
                //问诊退款，更改返利状态
                if(InquiryOrderConstant.SETTLEMENT_WAIT.equals(order.getSettlementFlag())){
                    inquiryOrderRebateDao.updateStatus(order.getOrderSn(), InquiryOrderRebateConstant.REBATE_FAIL,InquiryOrderRebateConstant.REASON_OVERTIME);
                }
                //超时自动退款消息提醒
                List<Integer> useIdrList=new ArrayList<>();
                useIdrList.add(order.getUserId());
                ConsultationOrderExpireParam param=ConsultationOrderExpireParam.builder().userIds(useIdrList).build();
                mapTemplateSendService.sendConsultationExprieMessage(param);
            });

        });
    }

    /**
     * 退款
     *
     * @param order
     * @return
     */
    public void refundExecute(InquiryOrderDo order) throws MpException {
        order.setSettlementFlag(InquiryOrderConstant.SETTLEMENT_NOT);
        inquiryOrderService.refundInquiryOrder(order,order.getOrderAmount(),InquiryOrderConstant.REFUND_REASON_OVERTIME);
    }

    /**
     * 接诊中超时自动结束的问诊订单
     */
    public void finishedCloseOrder(){
        logger().info("接诊中问诊订单超时自动结束定时任务start,shop:{}", getShopId());
        List<InquiryOrderDo> list=inquiryOrderDao.getFinishedCloseOrder();
        list.forEach(order -> {
            transaction(()->{
                order.setOrderStatus(InquiryOrderConstant.ORDER_FINISHED);
                order.setFinishedTime(DateUtils.getLocalDateTime());
                order.setSettlementFlag(InquiryOrderConstant.SETTLEMENT_FINISH);
                inquiryOrderDao.update(order);
                List<String> orderSnList=new ArrayList<>();
                orderSnList.add(order.getOrderSn());
                imSessionService.batchCloseSession(orderSnList);
                //完成问诊，更改返利状态
                inquiryOrderRebateDao.updateStatus(order.getOrderSn(), InquiryOrderRebateConstant.REBATED,null);
                //统计医师返利金额
                ImSessionDo im=imSessionService.getSessionInfoByOrderSn(order.getOrderSn());
                if(im.getContinueSessionCount().equals(ImSessionConstant.CONTINUE_SESSION_TIME)){
                    doctorTotalRebateDao.updateDoctorTotalRebate(order.getDoctorId(),order.getTotalRebateMoney());
                }
                //统计平台返利
                PlatformTotalRebateDo platformTotalRebateDo=new PlatformTotalRebateDo();
                platformTotalRebateDo.setShopId(order.getShopId());
                platformTotalRebateDo.setTotalMoney(order.getTotalRebateMoney());
                platformTotalRebateDo.setFinalMoney(order.getTotalRebateMoney());
                platformTotalRebateDao.savePlatFormTotalRebate(platformTotalRebateDo);
                logger().info("接诊中问诊订单超时自动结束,成功,orderSn:{}", order.getOrderSn());
            });

        });
        logger().info("接诊中问诊订单超时自动结束定时任务end");
    }
}
