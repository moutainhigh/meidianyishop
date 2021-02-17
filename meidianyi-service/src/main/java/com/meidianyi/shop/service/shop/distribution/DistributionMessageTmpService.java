package com.meidianyi.shop.service.shop.distribution;

import com.meidianyi.shop.common.foundation.util.Util;
import com.meidianyi.shop.service.foundation.service.ShopBaseService;
import com.meidianyi.shop.service.pojo.saas.schedule.TaskJobsConstant;
import com.meidianyi.shop.service.pojo.shop.config.message.MessageTemplateConfigConstant;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitMessageParam;
import com.meidianyi.shop.service.pojo.shop.market.message.RabbitParamConstant;
import com.meidianyi.shop.service.pojo.shop.market.message.maconfig.SubcribeTemplateCategory;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateConfig;
import com.meidianyi.shop.service.pojo.shop.message.MpTemplateData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaSubscribeData;
import com.meidianyi.shop.service.pojo.shop.user.message.MaTemplateData;
import com.meidianyi.shop.service.shop.order.action.base.OrderOperateSendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author changle
 * @date 2020/8/13 10:25 上午
 * 分销相关消息推送
 */
@Service
public class DistributionMessageTmpService extends ShopBaseService {
    @Autowired
    public OrderOperateSendMessage orderMes;
    /**
     * 审核通过消息推送
     * @param msg
     * @param remark
     * @param createTime
     * @param page
     * @param userId
     */
    public void distributorCheckPassMes(String msg,String remark,Timestamp createTime,String page,Integer userId){
        if(!(orderMes.isSendMp(MessageTemplateConfigConstant.AUDIT_SUCCESS))) {
            return;
        }
        logger().info("sendMaMp发送");
        String[][] maData = new String[][] { { Util.getdate("yyyy-MM-dd HH:mm:ss") }, { msg }, { remark } };
        String[][] mpData = new String[][] { { msg, "#173177" }, {createTime.toString(), "#173177" },
            { Util.getdate("yyyy-MM-dd HH:mm:ss"), "#173177" }, { remark, "#173177" }};

        List<Integer> userIdList = new ArrayList<>();
        userIdList.add(userId);
        MaSubscribeData data = MaSubscribeData.builder().data307(maData).build();
        RabbitMessageParam param = RabbitMessageParam.builder()
            .maTemplateData(
                MaTemplateData.builder().config(SubcribeTemplateCategory.AUDIT).data(data).build())
            .mpTemplateData(MpTemplateData.builder().config(MpTemplateConfig.AUDIT_SUCCESS).data(mpData).build())
            .page(page).shopId(getShopId()).userIdList(userIdList)
            .type(RabbitParamConstant.Type.SUCCESS_REVIEW).build();
        saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(),
            TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }

    /**
     * 提现申请通知
     * @param money
     * @param userId
     */
    public void withdrawApplyMes(BigDecimal money,Integer userId){
        if(!(orderMes.isSendMp(MessageTemplateConfigConstant.GET_MONEY))) {
            return;
        }
        logger().info("sendMaMp发送");
        String first = "您好，您已发起提现申请";
        String page = "pages/widthdraw/widthdraw";
        String ramark = "您的提现申请已被受理，提现金额将在1-5个工作日到账";
        String account = money.toString() +"元";
        String[][] mpData = new String[][] { { first, "#173177" },{ Util.getdate("yyyy-MM-dd HH:mm:ss"), "#173177" }, {account, "#173177" },{ramark,"#173177"}};
        List<Integer> userIdList = new ArrayList<>();
        userIdList.add(userId);
        RabbitMessageParam param = RabbitMessageParam.builder()
            .mpTemplateData(MpTemplateData.builder().config(MpTemplateConfig.GET_MONEY).data(mpData).build())
            .page(page).shopId(getShopId()).userIdList(userIdList)
            .type(RabbitParamConstant.Type.GET_MONEY).build();
        saas.taskJobMainService.dispatchImmediately(param, RabbitMessageParam.class.getName(), getShopId(),
            TaskJobsConstant.TaskJobEnum.SEND_MESSAGE.getExecutionType());
    }
}
