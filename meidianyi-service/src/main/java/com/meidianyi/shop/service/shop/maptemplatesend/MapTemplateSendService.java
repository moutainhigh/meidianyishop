package com.meidianyi.shop.service.shop.maptemplatesend;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.common.pojo.shop.table.InquiryOrderDo;
import com.meidianyi.shop.db.main.tables.records.MpAuthShopRecord;
import com.meidianyi.shop.db.main.tables.records.MpOfficialAccountUserRecord;
import com.meidianyi.shop.db.shop.tables.records.UserRecord;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.shop.config.message.MessageTemplateConfigConstant;
import com.meidianyi.shop.service.pojo.shop.maptemplate.*;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.saas.shop.official.MpOfficialAccountUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author chenjie
 * @date 2020年08月18日
 */
@Slf4j
@Service
public class MapTemplateSendService extends ShopBaseService {
    @Autowired
    private MpOfficialAccountUserService mpOfficialAccountUserService;
    /**
     * 提醒医生有新咨询订单
     * @param param
     */
    public void sendConsultationOrderMessage(ConsultationOrderPayParam param){
        // 订阅消息
        String[][] maData = new String[][] {
            {param.getPatientData()},
            {param.getDiseaseDetail()},
            {param.getCreateTime()},
            {param.getRemark()}
        };
        MaSubscribeData data = MaSubscribeData.builder().data47(maData).build();

        Integer mpTempleType = RabbitParamConstant.Type.NEW_CONSULTATION_ORDER;
        // 公众号消息
        String[][] mpData = new String[][] {
            {param.getDoctorName()+"医生您好，您有新的订单"},
            {param.getPatientData()},
            {param.getOrderType()},
            {param.getOrderSn()},
            {param.getCreateTime()},
            {param.getDiseaseDetail()},
            {param.getRemark()}
        };
        RabbitMessageParam param2 = RabbitMessageParam.builder()
//            .maTemplateData(
//                MaTemplateData.builder().config(SubcribeTemplateCategory.CONSULTATION_ORDER_PAY).data(data).build())
            .mpTemplateData(
                MpTemplateData.builder().config(MpTemplateConfig.NEW_CONSULTATION_ORDER).data(mpData).build())
            .page("/pages2/inquiryList/inquiryList").shopId(getShopId())
            .userIdList(param.getUserIds())
            .type(mpTempleType).build();
        saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }

    /**
     * 咨询已超时通知
     * @param param
     */
    public void sendConsultationExprieMessage(ConsultationOrderExpireParam param){
        // 订阅消息
        String[][] maData = new String[][] {
            {param.getConsultationStatus()},
            {param.getRemark()},
        };

        MaSubscribeData data = MaSubscribeData.builder().data47(maData).build();

        // 公众号消息
        String[][] mpData = new String[][] {
            {param.getConsultationStatus()},
            {param.getRemark()},
        };
        RabbitMessageParam param2 = RabbitMessageParam.builder()
            .maTemplateData(
                MaTemplateData.builder().config(SubcribeTemplateCategory.CONSULTATION_ORDER_EXPIRE).data(data).build())
//            .mpTemplateData(
//                MpTemplateData.builder().config(MpTemplateConfig.MONEY_CHANGE).data(mpData).build())
            .page("pages2/doctorConsultation/doctorConsultation?tab=1").shopId(getShopId())
            .userIdList(param.getUserIds())
            .type(MessageTemplateConfigConstant.CONSULTATION_EXPIRE).build();
        saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }

    /**
     * 咨询回复通知
     * @param param
     */
    public void sendConsultationAnswerMessage(ConsultationAnswerParam param){
        // 订阅消息
        String[][] maData = new String[][] {
            {param.getRemark()},
            {param.getDoctorName()},
            {param.getPatientName()},
            {param.getConsultationContent()},
        };

        MaSubscribeData data = MaSubscribeData.builder().data47(maData).build();

        // 公众号消息
        String[][] mpData = new String[][] {
            {param.getRemark()},
            {param.getDoctorName()},
            {param.getPatientName()},
            {param.getConsultationContent()},
        };
        RabbitMessageParam param2 = RabbitMessageParam.builder()
            .maTemplateData(
                MaTemplateData.builder().config(SubcribeTemplateCategory.CONSULTATION_ANSWER).data(data).build())
//            .mpTemplateData(
//                MpTemplateData.builder().config(MpTemplateConfig.MONEY_CHANGE).data(mpData).build())
            .page("pages2/doctorConsultation/doctorConsultation?tab=1").shopId(getShopId())
            .userIdList(param.getUserIds())
            .type(MessageTemplateConfigConstant.CONSULTATION_ANSWER).build();
        saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }

    /**
     * 医生已接诊提醒
     * @param param
     */
    public void sendConsultationSuccessMessage(ConsultationSuccessParam param){
        // 订阅消息
        String[][] maData = new String[][] {
            {param.getServiceName()},
            {param.getPatientName()},
            {param.getDoctorName()},
            {param.getReceiveTime()},
            {param.getRemark()}
        };

        MaSubscribeData data = MaSubscribeData.builder().data411(maData).build();

        // 公众号消息
        String[][] mpData = new String[][] {
            {param.getPatientName()},
            {param.getDoctorName()},
            {param.getRemark()},
        };
        RabbitMessageParam param2 = RabbitMessageParam.builder()
            .maTemplateData(
                MaTemplateData.builder().config(SubcribeTemplateCategory.CONSULTATION_SUCCESS).data(data).build())
//            .mpTemplateData(
//                MpTemplateData.builder().config(MpTemplateConfig.MONEY_CHANGE).data(mpData).build())
            .page("pages2/doctorConsultation/doctorConsultation?tab=1").shopId(getShopId())
            .userIdList(param.getUserIds())
            .type(MessageTemplateConfigConstant.CONSULTATION_SUCCESS).build();
        saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }

    /**
     * 发货成功提醒
     * @param param
     */
    public void sendOrderDeliverMessage(OrderDeliverParam param){
        // 订阅消息
        String[][] maData = new String[][] {
            {param.getConsignee()},
            {param.getMobile()},
            {param.getAddress()},
        };

        MaSubscribeData data = MaSubscribeData.builder().data47(maData).build();

        // 公众号消息
        String[][] mpData = new String[][] {
            {param.getOrderSn()}
        };
        RabbitMessageParam param2 = RabbitMessageParam.builder()
            .maTemplateData(
                MaTemplateData.builder().config(SubcribeTemplateCategory.ORDER_DELIVER).data(data).build())
//            .mpTemplateData(
//                MpTemplateData.builder().config(MpTemplateConfig.MONEY_CHANGE).data(mpData).build())
            .page("pages/orderinfo/orderinfo?orderSn=" + param.getOrderSn()).shopId(getShopId())
            .userIdList(param.getUserIds())
            .type(MessageTemplateConfigConstant.ORDER_SEND).build();
        saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }

    /**
     * 退款成功通知
     * @param param
     */
    public void sendOrderRefundSuccessMessage(OrderRefundSuccessParam param){
        // 订阅消息
        String[][] maData = new String[][] {
            {param.getRemind()},
            {param.getPayTime()},
            {param.getRefundMoney()}
        };

        MaSubscribeData data = MaSubscribeData.builder().data411(maData).build();

        // 公众号消息
        String[][] mpData = new String[][] {
            {param.getRemind()},
            {param.getPayTime()},
            {param.getRefundMoney()},
        };
        RabbitMessageParam param2 = RabbitMessageParam.builder()
            .maTemplateData(
                MaTemplateData.builder().config(SubcribeTemplateCategory.REFUND_RESULT).data(data).build())
//            .mpTemplateData(
//                MpTemplateData.builder().config(MpTemplateConfig.MONEY_CHANGE).data(mpData).build())
            .page("pages2/doctorConsultation/doctorConsultation?tab=1").shopId(getShopId())
            .userIdList(param.getUserIds())
            .type(MessageTemplateConfigConstant.STATUS_RETURN_MONEY).build();
        saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }

    /**
     * 新订单提醒
     * @param param
     */
    public void sendNewOrderMessage(OrderNewParam param){
        // 订阅消息
        String[][] maData = new String[][] {
            {param.getOrderSn()},
            {param.getUserName()},
            {param.getRemark()},
        };

        MaSubscribeData data = MaSubscribeData.builder().data47(maData).build();

        // 公众号消息
        String[][] mpData = new String[][] {
            {"您好，有新的订单生成"},
            {param.getUserName()},
            {param.getMobile()},
            {param.getDeliverType()},
            {param.getDeliverTime()},
            {param.getRemark()}
        };
        RabbitMessageParam param2 = RabbitMessageParam.builder()
//            .maTemplateData(
//                MaTemplateData.builder().config(SubcribeTemplateCategory.ORDER_NEW).data(data).build())
            .mpTemplateData(
                MpTemplateData.builder().config(MpTemplateConfig.WAIT_HANDLE_ORDER).data(mpData).build())
            .page("pages3/clerkOrderList/clerkOrderList?shippingStatus=8").shopId(getShopId())
            .userIdList(param.getUserIds())
            .type(RabbitParamConstant.Type.WAIT_HANDLE_ORDER).build();
        saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }

    /**
     * 待售后订单通知
     * @param param
     */
    public void sendWaitSaleAfterMessage(OrderSaleAfterParam param){
        // 订阅消息
        String[][] maData = new String[][] {
            {param.getOrderSn()},
            {param.getRemark()}
        };

        MaSubscribeData data = MaSubscribeData.builder().data47(maData).build();

        // 公众号消息
        String[][] mpData = new String[][] {
            {"您好，您有一笔申请退款通知！"},
            {param.getOrderSn()},
            {param.getCreateTime()},
            {param.getOrderSource()},
            {param.getRefundMoney()},
            {param.getRefundReason()},
            {param.getRemark()}
        };
        RabbitMessageParam param2 = RabbitMessageParam.builder()
//            .maTemplateData(
//                MaTemplateData.builder().config(SubcribeTemplateCategory.ORDER_NEW).data(data).build())
            .mpTemplateData(
                MpTemplateData.builder().config(MpTemplateConfig.SALE_AFTER_ORDER).data(mpData).build())
            .page("pages3/clerkOrderList/clerkOrderList?shippingStatus=10").shopId(getShopId())
            .userIdList(param.getUserIds())
            .type(RabbitParamConstant.Type.SALE_AFTER_ORDER).build();
        saas.taskJobMainService.dispatchImmediately(param2, RabbitMessageParam.class.getName(), getShopId(), TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());

    }
}
